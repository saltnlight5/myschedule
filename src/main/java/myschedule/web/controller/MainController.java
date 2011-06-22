package myschedule.web.controller;

import myschedule.web.WebAppContextListener;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/** 
 * Main dispatcher home/landing page.
 *
 * @author Zemian Deng
 */
@Controller
public class MainController {
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		return "redirect:" + WebAppContextListener.MAIN_PATH + "/scheduler/summary";
	}
	
	@RequestMapping(value="/menu", method=RequestMethod.GET)
	public String menu(@RequestParam String menu) {
		return "redirect:" + WebAppContextListener.MAIN_PATH + menu;
	}
}
