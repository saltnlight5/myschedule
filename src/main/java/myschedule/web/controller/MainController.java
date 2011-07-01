package myschedule.web.controller;

import java.util.List;

import myschedule.service.SchedulerServiceContainer;
import myschedule.web.WebAppContextListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/** 
 * Main dispatcher home/landing page.
 *
 * @author Zemian Deng
 */
@Controller
public class MainController {

	@Autowired @Qualifier("schedulerServiceContainer")
	protected SchedulerServiceContainer schedulerServiceContainer;
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		List<String> names = schedulerServiceContainer.getInitializedSchedulerServiceNames();
		if (names.size() == 0)
			return "redirect:" + WebAppContextListener.MAIN_PATH + "/dashboard/create";
		else
			return "redirect:" + WebAppContextListener.MAIN_PATH + "/job/list";
	}

}
