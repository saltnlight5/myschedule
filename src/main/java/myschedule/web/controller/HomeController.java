package myschedule.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/** 
 * HomeController is the landing/default page for the application.
 *
 * @author Zemian Deng
 */
@Controller
public class HomeController {
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home() {
		return "redirect:/scheduler/dashboard";
	}
}
