package myschedule.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import myschedule.service.SchedulerService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/** HomeController
 *
 * @author Zemian Deng
 */
@Controller
@RequestMapping(value="/scheduler")
public class SchedulerController {
	
	@Resource
	private SchedulerService schedulerService;
	
	@RequestMapping(value="/list-triggers", method=RequestMethod.GET)
	public ModelMap listTriggers() {
		List<String[]> triggerNames = schedulerService.getTriggerNames();
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("triggerNames", triggerNames);
		return modelMap;
	}
	
	@RequestMapping(value="/list-scheduled-jobs", method=RequestMethod.GET)
	public ModelMap listScheduledJobs() {
		List<ScheduledJobPageData> scheduledJobs = new ArrayList<ScheduledJobPageData>();
		List<String[]> triggerNames = schedulerService.getTriggerNames();
		for (String[] pair : triggerNames) {
			ScheduledJobPageData sj = new ScheduledJobPageData();
			sj.triggerName = pair[0];
			sj.triggerGroup = pair[1];
			sj.nextFireTime = schedulerService.getNextFireTimes(sj.triggerName, sj.triggerGroup, new Date(), 1).get(0);
			scheduledJobs.add(sj);
		}
		
		// sort the data for pretty display
		Collections.sort(scheduledJobs);
		
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("scheduledJobs", scheduledJobs);
		return modelMap;
	}
	
	@RequestMapping(value="/list-firetimes/{triggerName}/{triggerGroup}/{maxCount}", method=RequestMethod.GET)
	public ModelAndView listNextFireTimes(
			@PathVariable String triggerName,
			@PathVariable String triggerGroup,
			@PathVariable int maxCount) {
		
		List<Date> fireTimes = schedulerService.getNextFireTimes(triggerName, triggerGroup, new Date(), maxCount);
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("triggerName", triggerName);
		modelMap.addAttribute("triggerGroup", triggerGroup);
		modelMap.addAttribute("maxCount", maxCount);
		modelMap.addAttribute("fireTimes", fireTimes);
		
		return new ModelAndView("scheduler/list-firetimes", modelMap);
	}
	
	public static class ScheduledJobPageData implements Comparable<ScheduledJobPageData>{
		private String triggerGroup;
		private String triggerName;
		private Date nextFireTime;
		
		/**
		 * Getter.
		 * @return the triggerGroup - String
		 */
		public String getTriggerGroup() {
			return triggerGroup;
		}		
		/**
		 * Getter.
		 * @return the triggerName - String
		 */
		public String getTriggerName() {
			return triggerName;
		}
		/**
		 * Getter.
		 * @return the nextFireTime - Date
		 */
		public Date getNextFireTime() {
			return nextFireTime;
		}
		/**
		 * Override @see java.lang.Comparable#compareTo(java.lang.Object) method.
		 * @param o
		 * @return
		 */
		@Override
		public int compareTo(ScheduledJobPageData that) {
			return triggerGroup.compareTo(that.triggerGroup) +
				triggerName.compareTo(that.triggerName);
		}
	}
}
