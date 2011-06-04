package myschedule.web.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import myschedule.service.SchedulerService;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/** 
 * HomeController is the landing/default page for the application.
 *
 * @author Zemian Deng
 */
@Controller
@RequestMapping(value="/job")
public class JobController {
	protected static Logger logger = LoggerFactory.getLogger(SchedulerController.class);
	
	@Resource
	protected SchedulerService schedulerService;
	
	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public ModelMap delete(
			@RequestParam String name,
			@RequestParam String group,
			@RequestParam String type) {
		logger.info("Deleting " + type + ", name=" + name + ", group=" + group);

		ModelMap data = new ModelMap();
		data.put("name", name);
		data.put("group", group);
		data.put("type", type);
		
		try {
			if ("trigger".equals(type)) {
				schedulerService.deleteTrigger(name, group);
			} else if ("job".equals(type)) {
				schedulerService.deleteJob(name, group);
			}
			data.put("message", "Deleted successfully.");
		} catch (RuntimeException e) {
			logger.error("Failed to delete " + type, e);
			data.put("message", "Delete failed: " + e.getMessage());
		}
		return new ModelMap("data", data);
	}
	
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public ModelMap create() {
		return new ModelMap("data", Collections.EMPTY_MAP);
	}
	
	@RequestMapping(value="/load", method=RequestMethod.GET)
	public ModelMap load() {
		return new ModelMap("data", Collections.EMPTY_MAP);
	}
	
	/** Show TriggerPageData. */
	@RequestMapping(value="/firetimes", method=RequestMethod.GET)
	public ModelMap firetimes(
			@RequestParam String triggerName,
			@RequestParam String triggerGroup,
			@RequestParam int fireTimesCount) {
		logger.info("Getting next " + fireTimesCount + " fire times for trigger name=" + triggerName + ", group=" + triggerGroup);
		TriggerPageData data = new TriggerPageData();
		data.setNextFireTimesRequested(fireTimesCount);
		data.setTrigger(getTrigger(triggerName, triggerGroup));
		data.setJobDetail(getJobDetail(data.getTrigger()));
		data.setNextFireTimes(getNextFireTimes(data.getTrigger(), fireTimesCount));
		return new ModelMap("data", data);
	}

	protected JobDetail getJobDetail(Trigger trigger) {
		String jobName = trigger.getJobName();
		String jobGroup = trigger.getJobGroup();
		return schedulerService.getJobDetail(jobName, jobGroup);
	}
	
	protected Trigger getTrigger(String triggerName, String triggerGroup) {
		return schedulerService.getTrigger(triggerName, triggerGroup);
	}

	protected List<Date> getNextFireTimes(Trigger trigger, int fireTimesCount) {
		List<Date> fireTimes = schedulerService.getNextFireTimes(trigger, new Date(), fireTimesCount);
		return fireTimes;
	}
}
