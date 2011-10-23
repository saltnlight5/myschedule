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
 *   <li>ScriptEgnineName - Required. The name of ScriptEngine implementation to use. Default to 'JavaScript'.</li>
 *   <li>ScriptText or ScriptFile - Required. Specify where to find the script to run. Only one is needed. No default.</li>
 *   <li>LogScriptText - Optional. A boolean flag to log ScriptText value or not as INFO level. Default to 'false'.</li>
 *   <li>UseJobExecutionException - Optional. A boolean flag to use JobExecutionException or a RuntimeException wrapper
 *   when script is throwing an exception. (Note: JobExecutionException will not cause Quartz to trigger
 *   listener callback!) Default to 'false'.</li>
 * </ul>
 *  
 * <p>Before evaluating the script, the following implicit variables will be binded and available to the script: 
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
		
	public static final String DEFAULT_SCRIPT_ENGINE_NAME = "JavaScript";

	public static final String SCRIPT_ENGINE_NAME_KEY = "ScriptEngineName";
	
	public static final String SCRIPT_TEXT_KEY = "ScriptText";
	
	public static final String SCRIPT_FILE_KEY = "ScriptFile";
	
	public static final String LOG_SCRIPT_TEXT_KEY = "LogScriptText";
	
	public static final String USE_JOB_EXECUTION_EXCEPTION_KEY = "UseJobExecutionException";

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected boolean useJobExecutionException = false;
	
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
									
			// Extract job data map to setup script engine.
			JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();		
			String engineName = DEFAULT_SCRIPT_ENGINE_NAME;
			if (dataMap.containsKey(SCRIPT_ENGINE_NAME_KEY)) {
				engineName = dataMap.getString(SCRIPT_ENGINE_NAME_KEY);
			}
			String scriptText = null;
			String filename = null;
			String scriptType = null;
			if (dataMap.containsKey(SCRIPT_TEXT_KEY)) {
				scriptText = dataMap.getString(SCRIPT_TEXT_KEY);
				scriptType = SCRIPT_TEXT_KEY;
			} else if (dataMap.containsKey(SCRIPT_FILE_KEY)) {
				filename = dataMap.getString(SCRIPT_FILE_KEY);
				scriptType = SCRIPT_FILE_KEY;
			} else {
				throw new JobExecutionException("Neither " + SCRIPT_TEXT_KEY + " nor " + 
						SCRIPT_FILE_KEY + " is found in data map."); 
			}
			logger.debug("Creating ScriptEngine {} to evaluate {}.", engineName, scriptType);

			// Extract flag
			if (dataMap.containsKey(USE_JOB_EXECUTION_EXCEPTION_KEY)) {
				useJobExecutionException = dataMap.getBoolean(USE_JOB_EXECUTION_EXCEPTION_KEY);
				logger.debug("Setting useJobExecutionException: {}", useJobExecutionException);
			}
			
			// Create a Java ScriptEngine
			ScriptEngineManager factory = new ScriptEngineManager();
	        ScriptEngine scriptEngine = factory.getEngineByName(engineName);	        
	        if (scriptEngine == null) {
	        	throw new IllegalArgumentException("Failed to find ScriptEngine " + SCRIPT_ENGINE_NAME_KEY);
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
				// We only have two options, and this is the last case.
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
			
			// Store the result in case there is JobListener or TriggerListener setup to retrieve it.
			jobExecutionContext.setResult(result);
			Class<?> resultClass = (result == null) ? null : result.getClass();
			logger.info("Job {} has been executed. Result type: {}, value: {}", 
					new Object[]{ jobDetail.getKey(), resultClass, result });
		} catch (Exception e) {
        	if (useJobExecutionException) {
        		throw new JobExecutionException("Failed to execute job " + jobDetail.getKey(), e);
        	}
        	throw new RuntimeException("Failed to execute job " + jobDetail.getKey(), e);
		}
	}

}
