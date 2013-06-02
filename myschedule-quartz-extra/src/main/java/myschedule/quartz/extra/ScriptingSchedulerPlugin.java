package myschedule.quartz.extra;

import myschedule.quartz.extra.util.ScriptingUtils;
import myschedule.quartz.extra.util.Utils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This plugin allow you to execute any script during scheduler initialize, start or shutdown state.
 * <p/>
 * <p>In your quartz.properties, you may configure this plugin like this:
 * <pre>
 * # Scripting plugin
 * org.quartz.plugin.MyScriptingPlugin.class = myschedule.quartz.extra.ScriptingSchedulerPlugin
 * org.quartz.plugin.MyScriptingPlugin.scriptEngineName = JavaScript
 * org.quartz.plugin.MyScriptingPlugin.initializeScript = my-initialize.js
 * org.quartz.plugin.MyScriptingPlugin.startScript = my-start.js
 * org.quartz.plugin.MyScriptingPlugin.shutdownScript = my-shutdown.js
 * </pre>
 * <p/>
 * <p>Any of the scripts will have these implicit variables:
 * <ul>
 * <li><code>scheduler</code> - a SchedulerTemplate instance that wraps the Quartz scheduler.</li>
 * <li><code>logger</code> - a SLF4J logger object from this class.</li>
 * <li><code>schedulerPlugin</code> - the plugin instance that loaded the script.</li>
 * </ul>
 * <p/>
 * <p>The default script engine is the JDK built-in "JavaScript". You may easily add other engine such as Groovy or JRuby as long as
 * you add their jars in classpath.
 * <p/>
 * <p>You may use this in replacement to the org.quartz.plugins.xml.XMLSchedulingDataProcessorPlugin and have more
 * power and flexible on how you add jobs into scheduler upon startup.
 *
 * @author Zemian Deng <saltnlight5@gmail.com>
 */
public class ScriptingSchedulerPlugin implements SchedulerPlugin {
    private static final Logger logger = LoggerFactory.getLogger(ScriptingSchedulerPlugin.class);

    private String name;
    private Scheduler scheduler;

    private String scriptEngineName = "JavaScript";
    private String initializeScript;
    private String startScript;
    private String shutdownScript;

    public String getName() {
        return name;
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
        scriptEngineName = scriptEngineName.toLowerCase(); // it's more safe to use lowercase for lookup.

        if (initializeScript != null) {
            logger.debug("Running initialize script {}", initializeScript);
            String[] filenames = initializeScript.split("\\s*,\\s*");
            for (String filename : filenames) {
                runScriptFile(filename);
            }
        }
    }

    @Override
    public void start() {
        if (startScript != null) {
            logger.debug("Running start script {}", startScript);
            String[] filenames = startScript.split("\\s*,\\s*");
            for (String filename : filenames) {
                runScriptFile(filename);
            }
        }
    }

    @Override
    public void shutdown() {
        if (shutdownScript != null) {
            logger.debug("Running shutdown script {}", shutdownScript);
            String[] filenames = shutdownScript.split("\\s*,\\s*");
            for (String filename : filenames) {
                runScriptFile(filename);
            }
        }
    }

    protected void runScriptFile(String filename) {
        logger.debug("Run script {}", filename);

        Map<String, Object> bindings = Utils.toMap();
        bindings.put("schedulerPlugin", this);
        bindings.put("scheduler", new SchedulerTemplate(scheduler));
        bindings.put("logger", logger);

        ScriptingUtils.runScriptFile(scriptEngineName, filename, bindings);
    }
}
