package myschedule.quartz.extra.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * A simple quartz job that just log a message when invoked.
 *
 * @author Zemian Deng
 */
public class SimpleJob implements Job {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Override @see org.quartz.Job#execute(org.quartz.JobExecutionContext) method.
	 * @param context
	 * @throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("Job is running with context: " + context);
	}

}
