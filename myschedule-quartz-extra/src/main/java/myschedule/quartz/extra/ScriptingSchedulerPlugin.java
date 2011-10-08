package myschedule.quartz.extra;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This plugin allow you to execute any script during scheduler initialize, start or shutdown state. 
 * 
 * <p>In your quartz.properties, you may configure this plugin like this:
 * <pre>
 * 
 * </pre>
 * 
 * <p>Any of the scripts will have:
 * <ul>
 *   <li>"scheduler" a SchedulerTemplate instance that wraps the Quartz scheduler.</li>
 *   <li>"logger" a SLF4J logger object from this class.</li>
 *   <li>"schedulerPlugin" the plugin instance that loaded the script.</li>
 * <p>The default script engine is the JDK built-in "JavaScript". You may easily add other engine such as Groovy or JRuby as long as
 * you add their jars in classpath.
 * 
 * <p>You may use this in replacement to the org.quartz.plugins.xml.XMLSchedulingDataProcessorPlugin and have more
 * power and flexible on how you add jobs into scheduler upon startup.
 *  
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class ScriptingSchedulerPlugin implements SchedulerPlugin {
	
	private static final Logger logger = LoggerFactory.getLogger(ScriptingSchedulerPlugin.class);
	
	protected String name;
	protected String scriptEngineName = "JavaScript";
	protected String initializeScript;
	protected String startScript;
	protected String shutdownScript;
	
	protected ScriptEngine scriptEngine;
	protected ScriptContext scriptContext;
	
	public void setScriptEngineName(String scriptEngineName) {
		this.scriptEngineName = scriptEngineName;
	}
	public void setInitializeScript(String initializeScript) {
		this.initializeScript = initializeScript;
	}
	public void setStartScript(String startScript) {
		this.startScript = startScript;
	}
	public void setShutdownScript(String shutdownScript) {
		this.shutdownScript = shutdownScript;
	}
		
	public String getName() {
		return name;
	}
	public String getScriptEngineName() {
		return scriptEngineName;
	}
	public String getInitializeScript() {
		return initializeScript;
	}
	public String getStartScript() {
		return startScript;
	}
	public String getShutdownScript() {
		return shutdownScript;
	}
	
	@Override
	public void initialize(String name, Scheduler scheduler) throws SchedulerException {
		this.name = name;
		
		logger.debug("Initializing scripting plugin {} with ScriptEngine {}", name, scriptEngineName);
		ScriptEngineManager factory = new ScriptEngineManager();
        scriptEngine = factory.getEngineByName(scriptEngineName);
        scriptContext = new SimpleScriptContext();
        Bindings engineScope = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
        engineScope.put("schedulerPlugin", this);
        engineScope.put("scheduler", new SchedulerTemplate(scheduler));
		engineScope.put("logger", logger);
		
		if (initializeScript != null) {
	        logger.debug("Running initialize script {}", initializeScript);
	        runScript(initializeScript);
		}
	}
	
	@Override
	public void start() {
		if (startScript != null) {
	        logger.debug("Running start script {}", startScript);
	        runScript(startScript);
		}
	}

	@Override
	public void shutdown() {
		if (shutdownScript != null) {
	        logger.debug("Running shutdown script {}", shutdownScript);
	        runScript(shutdownScript);
		}
	}

	protected void runScript(String filename) {
		FileReader reader = null;
		try {
			reader = new FileReader(filename);
			scriptEngine.eval(reader, scriptContext);
		} catch (FileNotFoundException e) {
			throw new QuartzRuntimeException("Failed to find script " + filename, e);
		} catch (ScriptException e) {
			throw new QuartzRuntimeException("Failed to run script " + filename, e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				throw new QuartzRuntimeException("Failed to close reader for script " + filename, e);
			}
		}
		logger.info("Script ran successfully. Filename: {}", filename);
	}
}
