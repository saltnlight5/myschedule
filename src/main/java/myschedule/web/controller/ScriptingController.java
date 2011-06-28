package myschedule.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import myschedule.service.GroovyScriptingService;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceFinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;

/** 
 * Scheduler Scripting Web Controller.
 *
 * <p>The following variables are expose to the script automatically:
 * 
 * <pre>
 *   webOut - An instance of java.io.PrintWriter to allow script to display output to web page after Run.
 *   quartzScheduler - An instance of org.quartz.Scheduler scheduler in this application.
 *   servletContext - The ServletContext of this webapp.
 *   logger - An instance of org.slf4j.Logger with name="myschedule.web.controller.ScriptingController".
 *            The logger output will only appear in the server side, not web page.
 * </pre>
 * 
 * @author Zemian Deng
 */
@Controller
@RequestMapping(value="/scripting")
public class ScriptingController implements ServletContextAware {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected ServletContext servletContext;
	
	@Autowired
	protected GroovyScriptingService scriptingService;
	
	@Autowired @Qualifier("schedulerServiceFinder")
	protected SchedulerServiceFinder schedulerServiceFinder;
	
	public void setScriptingService(GroovyScriptingService scriptingService) {
		this.scriptingService = scriptingService;
	}
	
	@RequestMapping(value="run", method=RequestMethod.GET)
	public DataModelMap run() {
		return new DataModelMap();
	}
	
	@RequestMapping(value="run-action", method=RequestMethod.POST)
	public DataModelMap runAction(
			@RequestParam String groovyScriptText,
			HttpSession session) {
		logger.debug("Running Groovy Script Text.");
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		PrintWriter webOut = new PrintWriter(outStream);
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("schedulerService", schedulerService);
		variables.put("quartzScheduler", schedulerService.getUnderlyingScheduler());
		variables.put("servletContext", servletContext);
		variables.put("logger", logger);
		variables.put("webOut", webOut);
		Object scriptingOutput = scriptingService.run(groovyScriptText, variables );
		webOut.close();
		String webOutResult = outStream.toString();
		
		ModelMap data = new ModelMap();
		data.put("scriptingOutput", scriptingOutput);
		data.put("webOutResult", webOutResult);
		return new DataModelMap(data);
	}

	/**
	 * Callback method from ServletContextAware.
	 * @param arg0
	 */
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
