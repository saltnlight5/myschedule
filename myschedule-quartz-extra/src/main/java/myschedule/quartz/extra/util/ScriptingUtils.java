package myschedule.quartz.extra.util;

import myschedule.quartz.extra.QuartzRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A utilities class to support Java scripting.
 * @author Zemian Deng
 */
public class ScriptingUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptingUtils.class);

    /** Get all scripting engine names available from this JVM. */
    public static List<String> getAllScriptEngineNames() {
        List<String> scriptEngineNames = new ArrayList<String>();
        ScriptEngineManager factory = new ScriptEngineManager();
        for (ScriptEngineFactory fac : factory.getEngineFactories()) {
            String name = fac.getLanguageName();
            // Use consistent Camel case, pretty naming.
            if (name.toLowerCase().equals("ecmascript")) {
                name = "JavaScript";
            } else if (name.toLowerCase().endsWith("ruby")) {
                name = "JRuby";
            }
            scriptEngineNames.add(name);
        }
        Collections.sort(scriptEngineNames);
        return scriptEngineNames;
    }

    public static ScriptEngine getScriptEngine(String scriptEngineName) {
        ScriptEngineManager factory = new ScriptEngineManager();

        // If JRuby script engine, we need to use transient variable bindings so we do not need to prefix '$'
        // NOTE we are setting this globally and not cleaning up!
        if (scriptEngineName.equals("jruby") && System.getProperty("org.jruby.embed.localvariable.behavior") == null) {
            System.setProperty("org.jruby.embed.localvariable.behavior", "transient");
        }

        ScriptEngine scriptEngine = factory.getEngineByName(scriptEngineName);
        if (scriptEngine == null) {
            throw new RuntimeException("Script engine=" + scriptEngineName + " not found.");
        }
        return scriptEngine;
    }

    public static Object runScriptText(String scriptEngineName, String scriptText, Map<String, Object> bindingParams) {
        LOGGER.debug("Evaluating script text. length={}", scriptText.length());
        Reader reader = new StringReader(scriptText);
        return runScriptFile(scriptEngineName, reader, bindingParams);
    }

    public static Object runScriptFile(String scriptEngineName, String filename, Map<String, Object> bindingParams) {
        LOGGER.debug("Evaluating script filename={}", filename);
        Reader reader = null;
        try {
            URL url = ClasspathURLStreamHandler.createURL(filename);
            InputStream inStream = url.openStream();
            reader = new InputStreamReader(inStream);
            return runScriptFile(scriptEngineName, reader, bindingParams);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read script filename=" + filename, e);
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn("Failed to close reader for script filename={}", filename, e);
                }
        }
    }

    public static Object runScriptFile(String scriptEngineName, File file, Map<String, Object> bindingParams) {
        LOGGER.debug("Evaluating script file={}", file);
        Reader reader = null;
        try {
            reader = new FileReader(file);
            return runScriptFile(scriptEngineName, reader, bindingParams);
        } catch (FileNotFoundException e) {
            throw new QuartzRuntimeException("Failed to find script file=" + file, e);
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn("Failed to close reader for script file={}", file, e);
                }
        }
    }

    /** Read script text from reader and evaluate it and return its result using script engine given by name. User is
     * responsible to close the reader! */
    public static Object runScriptFile(String scriptEngineName, Reader scriptReader, Map<String, Object> bindingParams) {
        LOGGER.debug("Evaluating script text using engine={}.", scriptEngineName);
        ScriptEngine scriptEngine = getScriptEngine(scriptEngineName);

        // Script engine binding variables.
        Bindings bindings = scriptEngine.createBindings();

        // Bind scheduler as implicit variable
        if (bindingParams != null)
            for (Map.Entry<String, Object> entry : bindingParams.entrySet()) {
                LOGGER.debug("Binding param: {}", entry);
                bindings.put(entry.getKey(), entry.getValue());
            }

        try {
            Object result = scriptEngine.eval(scriptReader, bindings);
            LOGGER.info("Script evaluated with result={}", result);
            return result;
        } catch (ScriptException e) {
            throw new RuntimeException("Failed to run script.", e);
        }
    }
}
