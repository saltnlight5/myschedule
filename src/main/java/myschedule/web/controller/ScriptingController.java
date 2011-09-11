package myschedule.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import myschedule.service.QuartzSchedulerService;
import myschedule.service.ScriptingService;
import myschedule.service.quartz.SchedulerTemplate;
import myschedule.web.SessionSchedulerServiceFinder;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

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
	protected ScriptingService scriptingService;
	
	@Autowired @Qualifier("schedulerServiceFinder")
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
	
	public void setScriptingService(ScriptingService scriptingService) {
		this.scriptingService = scriptingService;
	}
	
	@RequestMapping(value="run", method=RequestMethod.GET)
	public DataModelMap run() {
		return new DataModelMap();
	}
	
	@RequestMapping(value="run-action", method=RequestMethod.POST)
	public ModelAndView runAction(
			@RequestParam String groovyScriptText,
			HttpSession session) {
		logger.debug("Running Groovy Script Text.");
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		PrintWriter webOut = new PrintWriter(outStream);
		QuartzSchedulerService schedulerService = schedulerServiceFinder.findSchedulerService(session);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("schedulerService", schedulerService);
		variables.put("schedulerTemplate", new SchedulerTemplate(schedulerService.getScheduler()));
		variables.put("quartzScheduler", schedulerService.getScheduler());
		variables.put("servletContext", servletContext);
		variables.put("logger", logger);
		variables.put("webOut", webOut);

		DataModelMap data = new DataModelMap();
		try {
			Object scriptingOutput = scriptingService.run(groovyScriptText, variables );
			data.addData("scriptingOutput", scriptingOutput);
		} catch (Exception e) {
			data.addData("errorMessage", ExceptionUtils.getMessage(e));
			data.addData("fullStackTrace", ExceptionUtils.getFullStackTrace(e));
			data.addData("groovyScriptText", groovyScriptText);
			return new ModelAndView("scripting/run", data);
		} finally {
			webOut.close();
			String webOutResult = outStream.toString();
			data.addData("webOutResult", webOutResult);
		}	
		return new ModelAndView("scripting/run-action", data);
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
