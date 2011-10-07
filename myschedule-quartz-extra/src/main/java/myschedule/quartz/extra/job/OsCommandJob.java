package myschedule.quartz.extra.job;

import myschedule.quartz.extra.ProcessUtils;
import myschedule.quartz.extra.ProcessUtils.BackgroundProcess;

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
 * A quartz job that execute a external command.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class OsCommandJob implements Job, InterruptableJob {
	
	private static final Logger logger = LoggerFactory.getLogger(OsCommandJob.class);
	private static final String COMMAND_KEY = "Command";
	
	protected BackgroundProcess bdProcess;
	protected JobKey jobKey;
	
	@Override
	public void interrupt() throws UnableToInterruptJobException {
		if (!bdProcess.isDone()) {
			bdProcess.destroy();
			logger.debug("Job {} was interrupted and process is destroyed.", jobKey);
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// Extract command from data map.
		JobDetail jobDetail = context.getJobDetail();
		JobDataMap dataMap = context.getMergedJobDataMap();
		if (!dataMap.containsKey(COMMAND_KEY)) {
			throw new JobExecutionException(COMMAND_KEY + " not found in data map");			
		}
		String command = dataMap.getString(COMMAND_KEY);
		
		logger.debug("Executing command: {}", command);
		String[] commandArguments = command.split(" ");
		bdProcess = ProcessUtils.runInBackground(commandArguments, new ProcessUtils.LineAction() {			
			@Override
			public void onLine(String line) {
				logger.debug("CommandOutput: " + line);
			}
		});
		jobKey = jobDetail.getKey();
		
		logger.info("Job {} has been executed.", jobKey);
	}

}
