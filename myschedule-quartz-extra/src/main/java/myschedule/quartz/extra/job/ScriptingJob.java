package myschedule.quartz.extra.job;

import java.io.FileReader;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * A Quartz job to evaluate any script text or file using one of Java javax.script.ScriptEngine implementation.
 * 
 * <p>Note that default Java 1.6 or higher comes with a JavaScript ScriptEngine built-in already! You may optionally
 * include other such as Groovy or JRuby by add their jars into your application classpath.
 * 
 * <p>You need to customize this job using Quartz's data map with following keys:
 * <ul>
 *   <li>ScriptEgnineName - the name of ScriptEngine implementation to use. Default to 'JavaScript'.</li>
 *   <li>ScriptFile - set a script file to be evaluated when this job is executed. Default to null.</li>
 *   <li>ScriptText - set any script text to be evaluated when this job is executed. Default to ''.</li>
 *   <li>LogScriptText - flag to log entire script text or not as INFO level. Default to 'false'</li>
 * </ul>
 * 
 * <p>Only one 'ScriptFile' or 'ScriptText' can be use, and if both given, then 'ScriptText' will be used. Obviously
 * the 'LogScriptText' will only be used when 'ScriptText' is given.
 * 
 * <p>Before evaluating the script, the following implicit variables will be binded: 
 * <ul>
 *   <li>jobExecutionContext - instance of org.quartz.JobExecutionContext when this job is run.</li>
 *   <li>logger - instance of org.slf4j.Logger from this class.</li>
 *   <li>scriptText - the script source if "scriptText" key is used.</li>
 *   <li>scriptFile - the file location if "scriptFile" key is used.</li>
 * </ul>
 * 
 * @author Zemian Deng
 */
public class ScriptingJob implements Job {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String DEFAULT_SCRIPT_ENGINE_NAME = "JavaScript";

	public static final String SCRIPT_ENGINE_NAME = "ScriptEngineName";
	
	public static final String SCRIPT_TEXT_KEY = "ScriptText";
	
	public static final String SCRIPT_FILE_KEY = "ScriptFile";
	
	public static final String LOG_SCRIPT_TEXT_KEY = "LogScriptText";
	
	/**
	 * Run the job to evaluate the script text or file using an javax.script.ScriptEngine impl.
	 * 
	 * @param jobExecutionContext
	 * @throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDetail jobDetail = jobExecutionContext.getJobDetail();
		try {
			logger.debug("Running job {}.", jobDetail.getKey());
			String scriptType = SCRIPT_TEXT_KEY;
						
			// Extract job data map to setup script engine.
			JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();		
			String engineName = dataMap.getString(SCRIPT_ENGINE_NAME);
			if (engineName == null) {
				engineName = DEFAULT_SCRIPT_ENGINE_NAME;
			}
			String scriptText = dataMap.getString(SCRIPT_TEXT_KEY);
			String filename = null;
			if (scriptText == null) {
				// Try to look for file
				filename = dataMap.getString(SCRIPT_FILE_KEY);
				if (filename == null) {
					throw new JobExecutionException("No " + SCRIPT_TEXT_KEY + " or " + SCRIPT_FILE_KEY + 
							" found in data map.");
				}
				scriptType = SCRIPT_FILE_KEY;
			}
			logger.debug("Creating ScriptEngine {} to evaluate {}.", engineName, scriptType);
			
			// Create a Java ScriptEngine
			ScriptEngineManager factory = new ScriptEngineManager();
	        ScriptEngine scriptEngine = factory.getEngineByName(engineName);	        
	        if (scriptEngine == null) {
				throw new JobExecutionException("Failed to find ScriptEngine " + SCRIPT_ENGINE_NAME); 	        	
	        }

			// Script engine binding variables.
	        ScriptContext scriptContext = new SimpleScriptContext();
	        Bindings engineScope = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
	        engineScope.put("jobExecutionContext", jobExecutionContext);
			engineScope.put("logger", logger);
			
			Object result = null;
			if (scriptType.equals(SCRIPT_TEXT_KEY)) {
				engineScope.put("scriptText", scriptText);
				logger.debug("Binding variables added: jobExecutionContext, logger, scriptText");
				
				// Evaluate script text.
				String logScriptTextVal = dataMap.getString(LOG_SCRIPT_TEXT_KEY);
				boolean logScriptText = logScriptTextVal == null ? false : true;
				if (logScriptText) {
					logger.info("Evaluating scriptText: {}", scriptText);
				} else {
					logger.debug("Evaluating scriptText length {}.", scriptText.length());
				}
				result = scriptEngine.eval(scriptText, scriptContext);
			} else {
				engineScope.put("scriptFile", filename);
				logger.debug("Binding variables added: jobExecutionContext, logger, scriptFile");

				// Evaluate script file.
				logger.debug("Evaluating scriptFile {}.", filename);
				FileReader reader = new FileReader(filename);
				try {
					result = scriptEngine.eval(reader, scriptContext);
				} finally {
					reader.close();
				}
			}
			
			// Store the result
			jobExecutionContext.setResult(result);
			logger.info("Job {} has been executed. Result: {}", jobDetail.getKey(), result);
		} catch (Exception e) {
			throw new JobExecutionException("Failed to execute job " + jobDetail.getKey(), e);
		}
	}	

}
