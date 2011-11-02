package myschedule.quartz.extra.job;

import java.util.Arrays;

import myschedule.quartz.extra.util.ProcessUtils;
import myschedule.quartz.extra.util.ProcessUtils.BackgroundProcess;

import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A quartz job that execute an external command. This is an improved version in comparison to the Quartz's built-in
 * <code>org.quartz.jobs.NativeJob</code> Job.
 * 
 * <p>This job is also interruptable by Quartz system. In case interrupted, it will abort (destroy) the command 
 * pre-maturely. This is a safety mechanism so that a user/scheduler may have a chance to signal for interruption, 
 * and the external command will terminate. But the call of scheduler.interrupt(jobKey) is still up to end user to 
 * implements though.
 * 
 * <p>You need to customize this job using Quartz's data map with following keys:
 * <ul>
 *   <li>CommandArguments - Required. An OS external command program. This needs to be String[] array type with 
 *                          executable and all of its options and arguments set in each array elements.</li>
 *   <li>RunInBackground - Optional. If set to "true", the command will run in background and job will not block 
 *                         the worker thread. Default to "false" (job will wait for command to complete.)</li>
 *   <li>Timeout - Optional. If RunInBackground="false", and Timeout > 0, this job will wait for the command no longer
 *                 than the timeout period specified. Unit is in millis. Default is -1, meaning not to use it.</li>
 * </ul>
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class OsCommandJob implements Job, InterruptableJob {
	
	public static final String CMD_ARGS_KEY = "CommandArguments";
	public static final String TIMEOUT_KEY = "Timeout";
	public static final String RUN_IN_BACKGROUND_KEY = "RunInBackground";

	private static final Logger logger = LoggerFactory.getLogger(OsCommandJob.class);
	private BackgroundProcess bgProcess;
	private JobKey jobKey;
	
	@Override
	public void interrupt() throws UnableToInterruptJobException {
		if (!bgProcess.isDone()) {
			bgProcess.destroy();
			logger.debug("Job {} was interrupted and process has destroyed.", jobKey);
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// Extract command from data map.
		JobDetail jobDetail = context.getJobDetail();
		JobDataMap dataMap = context.getMergedJobDataMap();
		if (!dataMap.containsKey(CMD_ARGS_KEY)) {
			throw new JobExecutionException(CMD_ARGS_KEY + " not found in data map");			
		}
		String[] commandArguments = (String[])dataMap.get(CMD_ARGS_KEY);
		logger.debug("Executing command: {}", Arrays.asList(commandArguments));
		
		// Will only in use when RUN_IN_BACKGROUND_KEY is not set (meaning the job will block until command is done.)
		long timeout = ProcessUtils.NO_TIMEOUT;
		if (dataMap.containsKey(TIMEOUT_KEY)) {
			timeout = dataMap.getLong(TIMEOUT_KEY);			
		}
		logger.debug("Timeout parameter: {}", timeout);
		
		boolean runInBackground = false;
		if (dataMap.containsKey(RUN_IN_BACKGROUND_KEY)) {
			runInBackground = dataMap.getBoolean(RUN_IN_BACKGROUND_KEY);			
		}
		logger.debug("RunInBackground parameter: {}", runInBackground);

		// Running the command
		bgProcess = ProcessUtils.runInBackground(commandArguments, new ProcessUtils.LineAction() {			
			@Override
			public void onLine(String line) {
				logger.debug("CommandOutput: " + line);
			}
		});

		// What to do after command started.
		if (!runInBackground) {
			int exitCode = -1;
			if (timeout > 0) {
				long startTime = System.currentTimeMillis();
				long checkInterval = (long)(timeout * 0.10);
				logger.debug("Monitoring command for timeout of {} ms with interval of {} ms check.", 
						timeout, checkInterval);
				while ((System.currentTimeMillis() - startTime) < timeout && !bgProcess.isDone()) {
					try {
						Thread.sleep(checkInterval);
					} catch (InterruptedException e) {
						throw new JobExecutionException("Failed to pause and check for Command timeout.", e);
					}
				}
				// Timed out?
				if (bgProcess.isDone()) {
					exitCode = bgProcess.getExitCode();
					context.setResult(exitCode);
					logger.info("Command finished with exitCode=" + exitCode);
				} else {
					long stopTime = System.currentTimeMillis();
					// Process is still running. We must force determination of the Process.
					bgProcess.destroy();
					context.setResult(null);
					logger.error("Process has timed-out. It ran for {}/{} ms.", (stopTime - startTime), timeout);
				}
			} else {
				logger.debug("Waiting for command to finish.");
				exitCode = bgProcess.waitForExit();
				context.setResult(exitCode);
				logger.info("Command finished with exitCode=" + exitCode);
			}
		} else {
			logger.info("Command has been started in background. {}.", bgProcess);
		}
		
		// Job is done.
		jobKey = jobDetail.getKey();
		logger.info("Job {} has been excuted.", jobKey);		
	}

}
