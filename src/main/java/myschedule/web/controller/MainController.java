package myschedule.web.controller;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;

/** 
 * Main dispatcher home/landing page.
 *
 * @author Zemian Deng
 */
@Controller
public class MainController implements ServletContextAware {
	
	private ServletContext servletContext;
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		String mainPath = (String)servletContext.getAttribute("mainPath");
		return "redirect:" + mainPath + "/scheduler/summary";
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
