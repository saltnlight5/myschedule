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
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.QuartzSchedulerService;
import myschedule.service.ResourceLoader;
import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.ViewData;
import myschedule.web.servlet.ViewDataActionHandler;
import myschedule.web.session.SessionData;
import myschedule.web.session.SessionSchedulerServiceFinder;
import org.apache.commons.lang.exception.ExceptionUtils;

public class ScriptingHandlers {
		
	@Setter
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
	@Setter
	protected ResourceLoader resourceLoader;
	
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
			String scriptEngineName = sessionData.getScriptEngineName();
			List<String> scriptEngineNames = getScriptingEngineNames();
			viewData.addData("data", ViewData.mkMap(
					"scriptEngineNames", scriptEngineNames, 
					"selectedScriptEngineName", scriptEngineName));
		}
	};
	
	@Getter
	protected ActionHandler runActionHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(myschedule.web.servlet.ViewData viewData) {
			logger.debug("Running Scripting Text.");
			HttpSession session = viewData.getRequest().getSession(true);
			String scriptEngineName = viewData.findData("scriptEngineName");
			String scriptText = viewData.findData("scriptText");
			
			// Re-save the script engine name into session
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			if (!scriptEngineName.equals(sessionData.getScriptEngineName())) {
				logger.debug("Changing session data scriptEngineName to {}", scriptEngineName);
				sessionData.setScriptEngineName(scriptEngineName);
			}

			// Create a Java ScriptEngine
			ScriptEngineManager factory = new ScriptEngineManager();
	        ScriptEngine scriptEngine = factory.getEngineByName(scriptEngineName);	        
	        if (scriptEngine == null) {
				throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "Failed to find ScriptEngine " + 
						scriptEngineName); 	        	
	        }
	        
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			PrintWriter webOut = new PrintWriter(outStream);
			QuartzSchedulerService schedulerService = schedulerServiceFinder.findSchedulerService(session);
			
			// Script engine binding variables.
	        ScriptContext scriptContext = new SimpleScriptContext();
	        Bindings engineScope = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);			
			engineScope.put("scheduler", new SchedulerTemplate(schedulerService.getScheduler()));
			engineScope.put("logger", logger);
			engineScope.put("webOut", webOut);
			
			Map<String, Object> map = ViewData.mkMap();
			try {
				Object scriptingOutput = scriptEngine.eval(scriptText, scriptContext);
				map.put("scriptingOutput", scriptingOutput);
			} catch (Exception e) {
				logger.error("Failed to run script text.", e);
				map.put("errorMessage", ExceptionUtils.getMessage(e));
				map.put("fullStackTrace", ExceptionUtils.getFullStackTrace(e));
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
