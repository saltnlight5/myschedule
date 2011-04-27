package myschedule.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** BeanJob
 *
 * @author Zemian Deng
 */
public class BeanJob {
	
	private static Logger logger = LoggerFactory.getLogger(BeanJob.class);
	
	public void run() {
		logger.info("Running bean job.");
	}
}
