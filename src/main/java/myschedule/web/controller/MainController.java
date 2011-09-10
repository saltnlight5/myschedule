package myschedule.web.controller;

import java.util.List;

import myschedule.service.SchedulerServiceRepository;
import myschedule.web.WebAppContextListener;

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

	protected SchedulerServiceRepository schedulerServiceRepo = SchedulerServiceRepository.getInstance();

	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		List<String> names = schedulerServiceRepo.getSchedulerServiceNames();
		if (names.size() == 0)
			return "redirect:" + WebAppContextListener.MAIN_PATH + "/dashboard/create";
		else
			return "redirect:" + WebAppContextListener.MAIN_PATH + "/job/list";
	}

}
