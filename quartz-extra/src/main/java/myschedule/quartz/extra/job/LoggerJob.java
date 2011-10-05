package myschedule.quartz.extra.job;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * A simple quartz job that just log runtime information.
 *
 * @author Zemian Deng
 */
public class LoggerJob implements Job {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Log context and job data map object as DEBUG msg, and then a completed INFO msg at the end of job.
	 * 
	 * @param jobExecutionContext
	 * @throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("JobExecutionContext {}", jobExecutionContext);
		logger.debug("getMergedJobDataMap {}", jobExecutionContext.getMergedJobDataMap());
		JobDetail jobDetail = jobExecutionContext.getJobDetail();
		logger.info("Job {} has been executed.", jobDetail.getKey());
	}

}
