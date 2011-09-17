package myschedule.job;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import myschedule.service.GroovyScriptingService;
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
 * <p>If "GroovyScriptText" is found, it will ignore the "GroovyScriptFile".
 * 
 * <p>The following variables are expose to the script automatically:
 * 
 * <pre>
 *   jobExecutionContext - instance of org.quartz.JobExecutionContext when job is run.
 *   logger - instance of org.slf4j.Logger with name="myschedule.job.GroovyScriptJob".
 *   groovyScriptText - Groovy script source if "groovyScriptText" key is used.
 *   groovyScriptFile - Groovy script source if "groovyScriptFile" key is used.
 * </pre>
 * 
 * @author Zemian Deng
 */
public class GroovyScriptJob implements Job {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String GROOVY_SCRIPT_TEXT_KEY = "groovyScriptText";
	
	public static final String GROOVY_SCRIPT_FILE_KEY = "groovyScriptFile";

	/**
	 * Run the job to evaluate the Groovy script text.
	 * @param jobExecutionContext
	 * @throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			JobDetail jobDetail = jobExecutionContext.getJobDetail();
			logger.info("Running Groovy Script Job {}." + jobDetail.getKey());
			JobDataMap dataMap = jobDetail.getJobDataMap();
			String scriptText = dataMap.getString(GROOVY_SCRIPT_TEXT_KEY);
			String filename = null;
			if (scriptText == null) {
				// Try to look for file
				filename = dataMap.getString(GROOVY_SCRIPT_FILE_KEY);
				if (filename == null) {
					throw new JobExecutionException("No Groovy script text nor file name found from data map.");
				}
			}
			
			ScriptingService scriptService = new GroovyScriptingService();
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("jobExecutionContext", jobExecutionContext);
			variables.put("logger", logger);
			Object result = null;
			if (filename == null) {
				logger.debug("Running script text.");
				variables.put("groovyScriptText", scriptText);
				result = scriptService.run(scriptText, variables);
			} else {
				File file = new File(filename);
				logger.debug("Running script file=" + file);
				variables.put("groovyScriptFile", file);
				result = scriptService.runScript(file, variables);
			}
			logger.info("Groovy script " + (filename == null ? "text" : filename) + 
					" evaluated. Result: " + result);
		} catch (Exception e) {
			throw new JobExecutionException("Failed executing GroovyScriptJob.", e);
		}
	}	

}
