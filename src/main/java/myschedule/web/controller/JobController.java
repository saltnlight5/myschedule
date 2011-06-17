package myschedule.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import myschedule.service.SchedulerService;
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
 * Scheduler Jobs Controller.
 *
 * @author Zemian Deng
 */
@Controller
@RequestMapping(value="/job")
public class JobController {
	protected static Logger logger = LoggerFactory.getLogger(JobController.class);
	
	@Resource
	protected SchedulerService schedulerService;
		
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		return "redirect:list";
	}
	
	/** List all scheudler's jobs */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelMap list() {
		return new ModelMap("data", getJobListPageData());
	}
	
	@RequestMapping(value="/unschedule", method=RequestMethod.GET)
	public ModelMap unscheduleJob(
			@RequestParam String triggerName,
			@RequestParam String triggerGroup) {
		logger.info("Unscheduling trigger name=" + triggerName + ", group=" + triggerGroup);

		ModelMap data = new ModelMap();
		Trigger trigger = schedulerService.uncheduleJob(triggerName, triggerGroup);
		data.put("trigger", trigger);
		try {
			JobDetail jobDetail = schedulerService.getJobDetail(trigger.getJobName(), trigger.getJobGroup());
			data.put("jobDetail", jobDetail);
		} catch (RuntimeException e) {
			// Job no longer exists, do nothing.
		}
		return new ModelMap("data", data);
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public ModelMap deleteAllJobsPost(
			@RequestParam String jobName,
			@RequestParam String jobGroup) {
		logger.info("Deleting jobName=" + jobName + ", jobGroup=" + jobGroup + " and its associated triggers.");

		JobDetail jobDetail = schedulerService.getJobDetail(jobName, jobGroup);
		List<Trigger> triggers = schedulerService.deleteJob(jobName, jobGroup);

		ModelMap data = new ModelMap();
		data.put("jobDetail", jobDetail);
		data.put("triggers", triggers);
		
		return new ModelMap("data", data);
	}
	
	/** Display form to load job-scheduling-data xml */
	@RequestMapping(value="/load-xml", method=RequestMethod.GET)
	public ModelMap load() {
		ModelMap data = new ModelMap();
		data.put("xml", "");
		return new ModelMap("data", data);
	}

	/** Process from for load job-scheduling-data xml */
	@RequestMapping(value="/load-xml-action", method=RequestMethod.POST)
	public ModelMap loadPost(@RequestParam String xml) {
		JobLoadPageData data = new JobLoadPageData();
		XmlJobLoader loader = schedulerService.loadJobs(xml);
		data.setIgnoreDuplicates(loader.isIgnoreDuplicates());
		data.setOverWriteExistingData(loader.isOverWriteExistingData());
		data.setJobGroupsToNeverDelete(loader.getJobGroupsToNeverDelete());
		data.setTriggerGroupsToNeverDelete(loader.getTriggerGroupsToNeverDelete());
		data.setLoadedJobs(getJobDetailFullNames(loader.getLoadedJobs()));
		data.setLoadedTriggers(getTriggerFullNames(loader.getLoadedTriggers()));
		return new ModelMap("data", data);
	}

	/** Show a trigger and its job detail page. */
	@RequestMapping(value="/job-detail", method=RequestMethod.GET)
	public ModelMap jobDetail(@RequestParam String jobName, @RequestParam String jobGroup) {
		logger.info("Viewing detail of jobName=" + jobName + ", jobGroup=" + jobGroup);
		JobDetail jobDetail = schedulerService.getJobDetail(jobName, jobGroup);
		JobTriggerDetailPageData data = new JobTriggerDetailPageData();
		data.setTriggers(schedulerService.getTriggers(jobDetail));
		data.setJobDetail(jobDetail);
		data.setJobDetailShouldRecover(jobDetail.requestsRecovery());
		return new ModelMap("data", data);
	}

	/** Show a trigger and its job detail page. */
	@RequestMapping(value="/trigger-detail", method=RequestMethod.GET)
	public ModelMap triggerDetail(
			@RequestParam String triggerName,
			@RequestParam String triggerGroup,
			@RequestParam int fireTimesCount) {
		logger.info("Viewing detail of triggerName=" + triggerName + ", triggerGroup=" + triggerGroup + "[fireTimesCount=" + fireTimesCount + "]");
		Trigger trigger = schedulerService.getTrigger(triggerName, triggerGroup);
		JobTriggerDetailPageData data = new JobTriggerDetailPageData();
		data.setJobDetail(schedulerService.getJobDetail(trigger.getJobName(), trigger.getJobGroup()));
		data.setFireTimesCount(fireTimesCount);
		data.setTriggers(Arrays.asList(new Trigger[]{ trigger }));
		data.setNextFireTimes(getNextFireTimes(trigger, fireTimesCount));
		return new ModelMap("data", data);
	}

	protected List<String> getTriggerFullNames(List<Trigger> triggers) {
		List<String> list = new ArrayList<String>();
		for (Trigger trigger : triggers)
			list.add(trigger.getFullName());
		return list;
	}

	protected List<String> getJobDetailFullNames(List<JobDetail> jobDetails) {
		List<String> list = new ArrayList<String>();
		for (JobDetail jobDetail : jobDetails)
			list.add(jobDetail.getFullName());
		return list;
	}
	
	protected List<Date> getNextFireTimes(Trigger trigger, int fireTimesCount) {
		List<Date> fireTimes = schedulerService.getNextFireTimes(trigger, new Date(), fireTimesCount);
		return fireTimes;
	}
	
	protected JobListPageData getJobListPageData() {
		List<Trigger> triggers = new ArrayList<Trigger>();
		List<JobDetail> noTriggerJobDetails = new ArrayList<JobDetail>();
		
		List<JobDetail> allJobDetails = schedulerService.getJobDetails();
		logger.debug("Found total " + allJobDetails.size() + " jobDetails");
		
		for (JobDetail jobDetail : allJobDetails) {
			List<Trigger> jobTriggers = schedulerService.getTriggers(jobDetail);
			if (jobTriggers.size() > 0) {
				triggers.addAll(jobTriggers);
			} else {
				noTriggerJobDetails.add(jobDetail);
			}
		}
		logger.debug("Found " + triggers.size() + " triggers.");
		logger.debug("Found " + noTriggerJobDetails.size() + " noTriggerJobDetails.");

		// Let's sort them.
		sortJobListTriggers(triggers);
		sortJobListNoTriggerJobDetails(noTriggerJobDetails);
		
		JobListPageData data = new JobListPageData();
		data.setTriggers(triggers);
		data.setNoTriggerJobDetails(noTriggerJobDetails);
		return data;
	}
	
	/**
	 * Sort by Trigger's default comparator provided by Quartz.
	 */
	@SuppressWarnings("unchecked")
	protected void sortJobListTriggers(List<Trigger> triggers) {
		Collections.sort(triggers);
	}
	
	/**
	 * Sort by JobDetail full name, then Trigger full name.
	 */
	protected void sortJobListTriggersByFullName(List<Trigger> triggers) {
		Collections.sort(triggers, new Comparator<Trigger>() {

			@Override
			public int compare(Trigger o1, Trigger o2) {
				int ret = o1.getFullJobName().compareTo(o2.getFullJobName());
				if (ret == 0) {
					ret = o1.getFullName().compareTo(o2.getFullName());
				}
				return ret;
			}
			
		});
	}

	/**
	 * Sort JobDetail by full name.
	 */
	protected void sortJobListNoTriggerJobDetails(List<JobDetail> noTriggerJobDetails) {
		Collections.sort(noTriggerJobDetails, new Comparator<JobDetail>() {

			@Override
			public int compare(JobDetail o1, JobDetail o2) {
				return o1.getFullName().compareTo(o2.getFullName());
			}
			
		});
	}
	
}
