package myschedule.quartz.extra.job;

import myschedule.quartz.extra.util.ProcessUtils;
import myschedule.quartz.extra.util.ProcessUtils.BackgroundProcess;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A quartz job that execute an external command. This is an improved version in comparison to the Quartz's built-in
 * <code>org.quartz.jobs.NativeJob</code> Job.
 * <p/>
 * <p>This job is also interruptable by Quartz system. In case interrupted, it will abort (destroy) the command
 * pre-maturely. This is a safety mechanism so that a user/scheduler may have a chance to signal for interruption,
 * and the external command will terminate. But the call of scheduler.interrupt(jobKey) is still up to end user to
 * implements though.
 * <p/>
 * <p>You need to customize this job using Quartz's data map with following keys:
 * <ul>
 * <li><code>CommandArguments</code> - Required. An OS external command program. This needs to be String[] array type with
 * executable and all of its options and arguments set in each array elements.</li>
 * <li><code>RunInBackground</code> - Optional. If set to "true", the command will run in background and job will not block
 * the worker thread. Default to "false" (job will wait for command to complete.)</li>
 * <li><code>Timeout</code> - Optional. If RunInBackground="false", and Timeout > 0, this job will wait for the command no longer
 * than the timeout period specified. Unit is in millis. Default is -1, meaning not to use it.</li>
 * </ul>
 *
 * @author Zemian Deng <saltnlight5@gmail.com>
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
        String[] commandArguments = null;
        Object cmdObj = dataMap.get(CMD_ARGS_KEY);
        if (cmdObj instanceof String) {
            commandArguments = splitCommandLine((String) cmdObj);
        } else if (cmdObj instanceof String[]) {
            commandArguments = (String[]) cmdObj;
        } else {
            throw new JobExecutionException(CMD_ARGS_KEY + " type is invalid. Use either String or String array.");
        }
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
                long checkInterval = (long) (timeout * 0.10);
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
        logger.info("Job {} has been executed.", jobKey);
    }

    /**
     * Split a command line input into array of command and arguments by a space.
     * This split would escape spaces within any quoted substring with either "" or ''.
     *
     * @param cmdLine single command line input.
     * @return Array of command and arguments.
     * @throws IllegalArgumentException if substring contains un-paired quotes.
     */
    private String[] splitCommandLine(String cmdLine) {
        cmdLine = cmdLine.trim();
        List<String> result = new ArrayList<String>();
        int pos = 0, lastPos = 0, max = cmdLine.length();
        while (pos < max) {
            // Skip any spaces
            while (cmdLine.charAt(pos) == ' ' && pos < max) {
                pos++;
            }

            // Ensure we didn't pass end of the string.
            if (pos >= max)
                break;
            lastPos = pos; // reset our last pos found.

            // Ensure we escape quotes (" or ').
            if (cmdLine.charAt(pos) == '"') {
                lastPos = pos + 1; // skip the starting " char.
                pos = cmdLine.indexOf("\"", lastPos);
                if (pos < 0)
                    throw new IllegalArgumentException("Missing closing quote \" that started at position " + lastPos);

                result.add(cmdLine.substring(lastPos, pos));
                pos++; // skip the ending " char.
            } else if (cmdLine.charAt(pos) == '\'') {
                lastPos = pos + 1; // skip the starting ' char.
                pos = cmdLine.indexOf("'", lastPos);
                if (pos < 0)
                    throw new IllegalArgumentException("Missing closing quote \" that started at position " + lastPos);

                result.add(cmdLine.substring(lastPos, pos));
                pos++; // skip the ending ' char.
            } else {
                // We don't see quote, so let's grap the next word until next space is found.
                pos = cmdLine.indexOf(" ", lastPos);
                if (pos < 0) {
                    result.add(cmdLine.substring(lastPos));
                    break; // we hit the end of cmdLine here.
                } else {
                    result.add(cmdLine.substring(lastPos, pos));
                    pos++; // skip the space.
                }
            }
        }
        // Done, return array from list.
        return result.toArray(new String[result.size()]);
    }
}
