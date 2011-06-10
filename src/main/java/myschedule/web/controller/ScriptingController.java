package myschedule.web.controller;

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
 *   quartzScheduler - instance of JobExecutionContext when job is run.
 *   servletContext - the ServletContext of this webapp.
 *   logger - a SLF4J logger
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
		Object scriptingOutput = scriptingService.run(groovyScriptText, getScriptingVariables());
		ModelMap data = new ModelMap();
		data.put("scriptingOutput", scriptingOutput);
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
