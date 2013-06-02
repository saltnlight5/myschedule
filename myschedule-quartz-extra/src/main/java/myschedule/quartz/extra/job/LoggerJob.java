package myschedule.quartz.extra.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple quartz job that just log runtime information.
 * <p/>
 * <p/>
 * This job logs many job runtime information as DEBUG level msgs, and then a completed status INFO msg at
 * the end of job.
 *
 * @author Zemian Deng
 */
public class LoggerJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(LoggerJob.class);

    /**
     * Log information.
     *
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("JobInstance: {}", jobExecutionContext.getJobInstance());
        logger.debug("JobRunTime: {}", jobExecutionContext.getJobRunTime());
        logger.debug("FireTime: {}", jobExecutionContext.getFireTime());
        logger.debug("NextFireTime: {}", jobExecutionContext.getNextFireTime());
        logger.debug("PreviousFireTime: {}", jobExecutionContext.getPreviousFireTime());
        logger.debug("ScheduledFireTime: {}", jobExecutionContext.getScheduledFireTime());
        logger.debug("RefireCount: {}", jobExecutionContext.getRefireCount());
        logger.debug("Recovering: {}", jobExecutionContext.isRecovering());
        logger.debug("Calendar: {}", jobExecutionContext.getCalendar());
        logger.debug("FireInstanceId: {}", jobExecutionContext.getFireInstanceId());
        logger.debug("Scheduler: {}", jobExecutionContext.getScheduler());
        logger.debug("MergedJobDataMap: {}", jobExecutionContext.getMergedJobDataMap().getWrappedMap());
        logger.debug("JobDetail: {}", jobExecutionContext.getJobDetail());
        logger.debug("Trigger: {}", jobExecutionContext.getTrigger());

        logger.info("Job {} has been executed.", jobExecutionContext.getJobDetail().getKey());
    }

}
