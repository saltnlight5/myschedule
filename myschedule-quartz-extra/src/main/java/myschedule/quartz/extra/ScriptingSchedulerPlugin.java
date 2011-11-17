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
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
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
 * # Scripting plugin
 * org.quartz.plugin.MyScriptingPlugin.class = myschedule.quartz.extra.ScriptingSchedulerPlugin
 * org.quartz.plugin.MyScriptingPlugin.scriptEngineName = JavaScript
 * org.quartz.plugin.MyScriptingPlugin.initializeScript = my-initialize.js
 * org.quartz.plugin.MyScriptingPlugin.startScript = my-start.js
 * org.quartz.plugin.MyScriptingPlugin.shutdownScript = my-shutdown.js
 * </pre>
 * 
 * <p>Any of the scripts will have these implicit variables:
 * <ul>
 *   <li><code>scheduler</code> - a SchedulerTemplate instance that wraps the Quartz scheduler.</li>
 *   <li><code>logger</code> - a SLF4J logger object from this class.</li>
 *   <li><code>schedulerPlugin</code> - the plugin instance that loaded the script.</li>
 * </ul>
 * 
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
	
	private String scriptEngineName = "JavaScript";
	private String initializeScript;
	private String startScript;
	private String shutdownScript;
	
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
		
		scriptEngineName = scriptEngineName.toLowerCase(); // it's more safe to use lowercase for lookup.
		logger.debug("Initializing scripting plugin {} with ScriptEngine {}", name, scriptEngineName);
		
		// If JRuby script engine, we need to use transient variable bindings so we do not need to prefix '$'
		if (scriptEngineName.equals("jruby")) {
			System.setProperty("org.jruby.embed.localvariable.behavior", "transient");
		}
					
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

        Bindings bindings = scriptEngine.createBindings();
        bindings.put("schedulerPlugin", this);
        bindings.put("scheduler", new SchedulerTemplate(scheduler));
		bindings.put("logger", logger);
		
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
			if (url == null) {
				throw new FileNotFoundException("Filename " + filename + "not found.");
			}
			logger.debug("Reading url {}", url);
			InputStream inStream = url.openStream();
			reader = new InputStreamReader(inStream);
			scriptEngine.eval(reader, bindings);
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
