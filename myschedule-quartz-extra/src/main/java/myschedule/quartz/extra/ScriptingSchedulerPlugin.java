package myschedule.quartz.extra;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
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
	
	private static final String CLASSPATH_PREFIX = "classpath:";

	private static final Logger logger = LoggerFactory.getLogger(ScriptingSchedulerPlugin.class);

	private String name;
	private ScriptEngine scriptEngine;
	private Scheduler scheduler;
	
	protected String scriptEngineName = "JavaScript";
	protected String initializeScript;
	protected String startScript;
	protected String shutdownScript;
	
	public String getName() {
		return name;
	}
	public ScriptEngine getScriptEngine() {
		return scriptEngine;
	}
	public Scheduler getScheduler() {
		return scheduler;
	}
	
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
		this.scheduler = scheduler;
		
		logger.debug("Initializing scripting plugin {} with ScriptEngine {}", name, scriptEngineName);
		ScriptEngineManager factory = new ScriptEngineManager();
        scriptEngine = factory.getEngineByName(scriptEngineName);
		
		if (initializeScript != null) {
	        logger.debug("Running initialize script {}", initializeScript);
			String[] filenames = initializeScript.split("\\s*,\\s*");
			for (String filename : filenames) {
		        runScript(filename);
			}
		}
	}
	
	@Override
	public void start() {
		if (startScript != null) {
	        logger.debug("Running start script {}", startScript);
			String[] filenames = startScript.split("\\s*,\\s*");
			for (String filename : filenames) {
		        runScript(filename);
			}
		}
	}

	@Override
	public void shutdown() {
		if (shutdownScript != null) {
	        logger.debug("Running shutdown script {}", shutdownScript);
			String[] filenames = shutdownScript.split("\\s*,\\s*");
			for (String filename : filenames) {
		        runScript(filename);
			}
		}
	}
	
	protected URL getResource(String resName) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return loader.getResource(resName);
	}

	protected void runScript(String filename) {
		logger.debug("Run script {}", filename);

        ScriptContext scriptContext = new SimpleScriptContext();
        Bindings engineScope = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
        engineScope.put("schedulerPlugin", this);
        engineScope.put("scheduler", new SchedulerTemplate(scheduler));
		engineScope.put("logger", logger);
		
		URL url = null;
		Reader reader = null;
		try {
			if (filename.startsWith(CLASSPATH_PREFIX)) {
				String resName = filename.substring(CLASSPATH_PREFIX.length());
				url = getResource(resName);
			} else {
				try {
					url = new URL(filename);
				} catch (MalformedURLException e) {
					url = new File(filename).toURI().toURL();
				}
			}
			logger.debug("Reading url {}", url);
			InputStream inStream = url.openStream();
			reader = new InputStreamReader(inStream);
			scriptEngine.eval(reader, scriptContext);
		} catch (FileNotFoundException e) {
			throw new QuartzRuntimeException("Failed to find script " + filename, e);
		} catch (ScriptException e) {
			throw new QuartzRuntimeException("Failed to run script " + filename, e);
		} catch (IOException e) {
			throw new QuartzRuntimeException("Failed to read script " + filename, e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				throw new QuartzRuntimeException("Failed to close reader for script " + filename, e);
			}
		}
		logger.info("Script {} ran successfully.", filename);
	}
}
