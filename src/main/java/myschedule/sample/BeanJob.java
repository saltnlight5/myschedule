package myschedule.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * A plain java bean that can be called by scheduler as job execution.
 *
 * @author Zemian Deng
 */
public class BeanJob {
	
	private static Logger logger = LoggerFactory.getLogger(BeanJob.class);
	
	public void run() {
		logger.info("Running bean job.");
	}
}
