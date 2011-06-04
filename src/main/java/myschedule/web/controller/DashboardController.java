package myschedule.web.controller;

import javax.annotation.Resource;

import myschedule.service.SchedulerService;

import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/** 
 * HomeController is the landing/default page for the application.
 *
 * @author Zemian Deng
 */
@Controller
@RequestMapping(value="/dashboard")
public class DashboardController {
	protected static Logger logger = LoggerFactory.getLogger(SchedulerController.class);
	
	@Resource
	protected SchedulerService schedulerService;
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public ModelMap index() {
		DashboardPageData data = new DashboardPageData();
		data.setSchedulerSummary(getSchedulerSummary());
		return new ModelMap("data", data);
	}

	protected String getSchedulerSummary() {
		SchedulerMetaData schedulerMetaData = schedulerService.getSchedulerMetaData();
		try {
			return schedulerMetaData.getSummary();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
}
