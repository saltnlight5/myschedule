package myschedule.job;

import java.io.File;

import myschedule.service.ScriptingService;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Quartz job to evaluate any Groovy script text or file from the data map.
 * 
 * If "GroovyScriptText" is found, it will ignore the "GroovyScriptFile".
 *
 * @author Zemian Deng
 */
public class GroovyScriptJob implements Job {
	
	protected static Logger logger = LoggerFactory.getLogger(GroovyScriptJob.class);
	
	public static final String GROOVY_SCRIPT_TEXT_KEY = "GroovyScriptText";
	
	public static final String GROOVY_SCRIPT_FILE_KEY = "GroovyScriptFile";

	/**
	 * Run the job to evaluate the Groovy script text.
	 * @param context
	 * @throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		logger.info("Running Groovy Script Job: " + jobDetail.getFullName());
		JobDataMap dataMap = jobDetail.getJobDataMap();
		String scriptText = dataMap.getString(GROOVY_SCRIPT_TEXT_KEY);
		String filename = null;
		if (scriptText == null) {
			// Try to look for file
			filename = dataMap.getString(GROOVY_SCRIPT_FILE_KEY);
			if (filename == null) {
				logger.warn("No Groovy script text nor file name found from data map.");
				return;
			}
		}
		
		ScriptingService scriptService = new ScriptingService();
		Object result = null;
		if (filename == null) {
			logger.debug("Running script text.");
			result = scriptService.run(scriptText);
		} else {
			File file = new File(filename);
			logger.debug("Running script file=" + file);
			result = scriptService.runScript(file);
		}
		logger.info("Groovy script " + (filename == null ? "text" : filename) + 
				" evaluated. Result: " + result);
	}	

}
