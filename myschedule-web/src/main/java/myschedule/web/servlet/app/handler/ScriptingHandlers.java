package myschedule.web.servlet.app.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import lombok.Getter;
import lombok.Setter;
import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.ResourceLoader;
import myschedule.service.SchedulerContainer;
import myschedule.service.SchedulerService;
import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.ViewData;
import myschedule.web.servlet.ViewDataActionHandler;
import myschedule.web.session.SessionData;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptingHandlers {
	
	@Setter
	private SchedulerContainer schedulerContainer;
	@Setter
	private ResourceLoader resourceLoader;
	
	private static final Logger logger = LoggerFactory.getLogger(ScriptingHandlers.class);
	private static final Map<String, String> SCRIPT_EXT_MAPPINGS = new HashMap<String, String>();
	
	static {
		SCRIPT_EXT_MAPPINGS.put("JavaScript", ".js");
		SCRIPT_EXT_MAPPINGS.put("Groovy", ".groovy");
		SCRIPT_EXT_MAPPINGS.put("JRuby", ".rb");
	}
			
	@Getter
	protected ActionHandler runHandler = new ViewDataActionHandler(){
		@Override
		protected void handleViewData(myschedule.web.servlet.ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String selectedScriptEngineName = sessionData.getScriptEngineName();
			List<String> scriptEngineNames = getScriptingEngineNames();
			viewData.addData("data", ViewData.mkMap(
					"scriptEngineNames", scriptEngineNames, 
					"selectedScriptEngineName", selectedScriptEngineName));
		}
	};
	
	@Getter
	protected ActionHandler runActionHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(myschedule.web.servlet.ViewData viewData) {
			logger.debug("Running Scripting Text.");
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			String scriptEngineName = viewData.findData("scriptEngineName");
			String scriptText = viewData.findData("scriptText");
			
			// Save selected scriptEngineName to session
			SessionData session = viewData.findData(SessionData.SESSION_DATA_KEY);
			if (!scriptEngineName.equals(session.getScriptEngineName())) {
				session.setScriptEngineName(scriptEngineName);
			}
			
			// Create a Java ScriptEngine
			String lcEngineName = scriptEngineName.toLowerCase();
			
			// If JRuby script engine, we need to use transient variable bindings so we do not need to prefix '$'
			if (lcEngineName.equals("jruby")) {
				System.setProperty("org.jruby.embed.localvariable.behavior", "transient");
			}
			
			ScriptEngineManager factory = new ScriptEngineManager();
	        ScriptEngine scriptEngine = factory.getEngineByName(lcEngineName);	        
	        if (scriptEngine == null) {
				throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "Failed to find ScriptEngine " + 
						scriptEngineName); 	        	
	        }
	        
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			PrintWriter webOut = new PrintWriter(outStream);
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			
			// Script engine binding variables.
	        Bindings bindings = scriptEngine.createBindings();			
			bindings.put("scheduler", schedulerService.getScheduler());
			bindings.put("logger", logger);
			bindings.put("output", webOut);
			
			Map<String, Object> map = ViewData.mkMap();
			try {
				Object scriptingOutput = scriptEngine.eval(scriptText, bindings);
				map.put("scriptingOutput", scriptingOutput);
			} catch (Exception e) {
				logger.error("Failed to run script text.", e);
				map.put("errorMessage", StringEscapeUtils.escapeHtml(ExceptionUtils.getMessage(e)));
				map.put("fullStackTrace", StringEscapeUtils.escapeHtml(ExceptionUtils.getFullStackTrace(e)));
				map.put("scriptText", scriptText);
				map.put("scriptEngineNames", getScriptingEngineNames());
				map.put("selectedScriptEngineName", scriptEngineName);
				viewData.setViewName("/scripting/run");
			} finally {
				webOut.close();
				String webOutResult = outStream.toString();
				map.put("webOutResult", webOutResult);
			}
			viewData.addData("data", map);
		};
	};
	
	@Getter
	protected ActionHandler scriptExampleHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String scriptEngineName = viewData.findData("scriptEngineName");
			String name = viewData.findData("name");
			String ext = SCRIPT_EXT_MAPPINGS.get(scriptEngineName);
			if (ext == null) {
				ext = ".js";
			}
			String resName = "myschedule/script/examples/" + name + ext;
			try {
				Writer writer = viewData.getResponse().getWriter();
				try {
					resourceLoader.copyResource(resName, writer);
				} catch (IllegalArgumentException e) {
					// Resource not found.
					writer.write("// This particular example is not available.\n");
					writer.flush();
				}
			} catch (IOException e) {
				throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "Failed to get resource " + name, e);
			}
			// Set viewName to null, so it will not render view.
			viewData.setViewName(null);
		}
	};
	
	protected List<String> getScriptingEngineNames() {
		List<String> scriptEngineNames = new ArrayList<String>();
		ScriptEngineManager factory = new ScriptEngineManager();
		for (ScriptEngineFactory fac : factory.getEngineFactories()) {
			String name = fac.getLanguageName();
			// Use consistent naming.
			if (name.toLowerCase().equals("ecmascript")) {
				name = "JavaScript";
			} else if (name.toLowerCase().endsWith("ruby")) {
				name = "JRuby";
			}
			scriptEngineNames.add(name);
		}
		return scriptEngineNames;
	}
}
