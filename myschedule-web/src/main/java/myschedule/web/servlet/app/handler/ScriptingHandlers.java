package myschedule.web.servlet.app.handler;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.ViewData;
import myschedule.web.servlet.ViewDataActionHandler;
import myschedule.web.session.SessionSchedulerServiceFinder;

import org.apache.commons.lang.exception.ExceptionUtils;

public class ScriptingHandlers {
		
	@Setter
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
		
	@Getter
	protected ActionHandler runHandler = new ViewDataActionHandler(){
		@Override
		protected void handleViewData(myschedule.web.servlet.ViewData viewData) {
			List<String> scriptEngineNames = getScriptingEngineNames();
			viewData.addData("data", ViewData.mkMap("scriptEngineNames", scriptEngineNames));
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
				map.put("groovyScriptText", scriptText);
				map.put("scriptEngineNames", getScriptingEngineNames());
				viewData.setViewName("/scripting/run");
			} finally {
				webOut.close();
				String webOutResult = outStream.toString();
				map.put("webOutResult", webOutResult);
			}
			viewData.addData("data", map);
		};
	};
	
	protected List<String> getScriptingEngineNames() {
		List<String> scriptEngineNames = new ArrayList<String>();
		ScriptEngineManager factory = new ScriptEngineManager();
		for (ScriptEngineFactory fac : factory.getEngineFactories()) {
			String name = fac.getLanguageName();
			// JavaScript is a better name.
			if (name.equals("ECMAScript")) {
				name = "JavaScript";
			}
			scriptEngineNames.add(name);
		}
		return scriptEngineNames;
	}
}
