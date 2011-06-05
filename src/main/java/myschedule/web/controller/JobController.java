package myschedule.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import myschedule.service.SchedulerService;
import myschedule.service.ScriptService;
import myschedule.service.XmlJobLoader;

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
	
	@Resource
	protected ScriptService scriptService;
	
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
		ModelMap data = new ModelMap();
		return new ModelMap("data", data);
	}
	
	@RequestMapping(value="/create-process", method=RequestMethod.POST)
	public ModelMap createProcess(
			@RequestParam String jobDetailScript,
			@RequestParam String triggerScript) {
		logger.info("Creating new job with Groovy.");
		JobDetail jobDetail = scriptService.run(jobDetailScript);
		Trigger trigger = scriptService.run(triggerScript);
		Date fireTime = schedulerService.scheduleJob(jobDetail, trigger);
		ModelMap data = new ModelMap();
		data.put("jobDetail", jobDetail);
		data.put("trigger", trigger);
		data.put("fireTime", fireTime);
		return new ModelMap("data", data);
	}
	
	@RequestMapping(value="/load", method=RequestMethod.GET)
	public ModelMap load() {
		ModelMap data = new ModelMap();
		data.put("xml", "");
		return new ModelMap("data", data);
	}
	
	@RequestMapping(value="/load-process", method=RequestMethod.POST)
	public ModelMap loadProcess(@RequestParam String xml) {
		JobLoadPageData data = new JobLoadPageData();
		XmlJobLoader loader = schedulerService.loadJobs(xml);
		data.setIgnoreDuplicates(loader.isIgnoreDuplicates());
		data.setOverWriteExistingData(loader.isOverWriteExistingData());
		data.setJobGroupsToNeverDelete(loader.getJobGroupsToNeverDelete());
		data.setTriggerGroupsToNeverDelete(loader.getTriggerGroupsToNeverDelete());
		data.setLoadedJobs(toFullNameJobDetailList(loader.getLoadedJobs()));
		data.setLoadedTriggers(toFullNameTriggerList(loader.getLoadedTriggers()));
		return new ModelMap("data", data);
	}
	
	/**
	 * @param loadedTriggers
	 * @return
	 */
	private List<String> toFullNameTriggerList(List<Trigger> triggers) {
		List<String> list = new ArrayList<String>();
		for (Trigger trigger : triggers)
			list.add(trigger.getFullName());
		return list;
	}

	/**
	 * @param loadedJobs
	 * @return
	 */
	private List<String> toFullNameJobDetailList(List<JobDetail> jobDetails) {
		List<String> list = new ArrayList<String>();
		for (JobDetail jobDetail : jobDetails)
			list.add(jobDetail.getFullName());
		return list;
	}

	/** Show Job and Trigger and FireTimes Details */
	@RequestMapping(value="/details", method=RequestMethod.GET)
	public ModelMap details(
			@RequestParam String type,
			@RequestParam String name,
			@RequestParam String group,
			@RequestParam int fireTimesCount) {
		logger.info("Getting " + type + " name=" + name + ", group=" + group + "[fireTimesCount=" + fireTimesCount + "]");
		JobTriggerDetailsPageData data = null;
		if ("trigger".equals(type)) {
			data = getJobTriggerDetailsPageData(name, group, fireTimesCount);
		} else if ("job".equals(type)) {
			data = getJobTriggerDetailsPageDataByJobOnly(name, group);
		}
		return new ModelMap("data", data);
	}

	private JobTriggerDetailsPageData getJobTriggerDetailsPageData(String name, String group, int fireTimesCount) {
		Trigger trigger = schedulerService.getTrigger(name, group);
		String jobName = trigger.getJobName();
		String jobGroup = trigger.getJobGroup();
		JobDetail jobDetail = schedulerService.getJobDetail(jobName, jobGroup);
		
		JobTriggerDetailsPageData data = new JobTriggerDetailsPageData();
		data.setFireTimesCount(fireTimesCount);
		data.setTrigger(trigger);
		data.setTriggerListenerNames(Arrays.asList(trigger.getTriggerListenerNames()));
		data.setNextFireTimes(getNextFireTimes(data.getTrigger(), fireTimesCount));
		data.setJobDetail(jobDetail);		
		data.setJobDetailShouldRecover(jobDetail.requestsRecovery());
		data.setJobListenerNames(Arrays.asList(jobDetail.getJobListenerNames()));
		return data;
	}

	private JobTriggerDetailsPageData getJobTriggerDetailsPageDataByJobOnly(String name, String group) {
		JobDetail jobDetail = schedulerService.getJobDetail(name, group);
		JobTriggerDetailsPageData data = new JobTriggerDetailsPageData();
		data.setJobDetail(jobDetail);
		data.setJobDetailShouldRecover(jobDetail.requestsRecovery());
		data.setJobListenerNames(Arrays.asList(jobDetail.getJobListenerNames()));
		return data;
	}

	protected List<Date> getNextFireTimes(Trigger trigger, int fireTimesCount) {
		List<Date> fireTimes = schedulerService.getNextFireTimes(trigger, new Date(), fireTimesCount);
		return fireTimes;
	}
}
