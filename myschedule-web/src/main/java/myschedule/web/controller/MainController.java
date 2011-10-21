package myschedule.web.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import myschedule.service.SchedulerServiceRepository;
import myschedule.web.WebAppContextListener;
import myschedule.web.session.SessionSchedulerServiceFinder;

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
	
	@Autowired @Qualifier("schedulerServiceFinder")
	protected SessionSchedulerServiceFinder schedulerServiceFinder;

	protected SchedulerServiceRepository schedulerServiceRepo = SchedulerServiceRepository.getInstance();

	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index(HttpSession session) {
		List<String> names = schedulerServiceRepo.getSchedulerServiceConfigIds();
		if (names.size() == 0) {
			return "redirect:" + WebAppContextListener.MAIN_PATH + "/dashboard/create";
		} else {
			try {
				// This find method will throw exception if no scheduler are available. 
				// If returned success, then we have a good scheduler service ready to be list jobs. 
				schedulerServiceFinder.findSchedulerService(session);				
				return "redirect:" + WebAppContextListener.MAIN_PATH + "/job/list";
			} catch (RuntimeException e) {
				// No scheduler service are initialized, so list them all
				return "redirect:" + WebAppContextListener.MAIN_PATH + "/dashboard/list";
			}
		}
	}
	
	@RequestMapping(value="/about", method=RequestMethod.GET)
	public void help() {
	}

}
