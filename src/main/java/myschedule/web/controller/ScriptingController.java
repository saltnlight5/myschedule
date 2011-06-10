package myschedule.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import myschedule.service.GroovyScriptingService;

import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static Logger logger = LoggerFactory.getLogger(ScriptingController.class);
	
	private ServletContext servletContext;
	@Resource
	private Scheduler quartzScheduler;
	@Resource
	private GroovyScriptingService scriptingService;
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		return "redirect:run";
	}

	@RequestMapping(value="run", method=RequestMethod.GET)
	public ModelMap run() {
		ModelMap data = new ModelMap();
		return new ModelMap("data", data);
	}
	
	@RequestMapping(value="run-action", method=RequestMethod.POST)
	public ModelMap runAction(@RequestParam String groovyScriptText) {
		logger.debug("Running Groovy Script Text.");
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		PrintWriter webOut = new PrintWriter(outStream);
		Map<String, Object> variables = getScriptingVariables();
		variables.put("webOut", webOut);
		Object scriptingOutput = scriptingService.run(groovyScriptText, variables );
		webOut.close();
		String webOutResult = outStream.toString();
		ModelMap data = new ModelMap();
		data.put("scriptingOutput", scriptingOutput);
		data.put("webOutResult", webOutResult);
		return new ModelMap("data", data);
	}

	/**
	 * @return a map of binding variables.
	 */
	private Map<String, Object> getScriptingVariables() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("quartzScheduler", quartzScheduler);
		variables.put("servletContext", servletContext);
		variables.put("logger", logger);
		return variables;
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
