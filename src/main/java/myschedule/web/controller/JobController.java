package myschedule.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import myschedule.service.SchedulerService;
import myschedule.service.ScriptingService;
import myschedule.service.XmlJobLoader;
import myschedule.web.controller.JobsPageData.JobInfo;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
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
	private static Logger logger = LoggerFactory.getLogger(JobController.class);
	
	@Resource
	private SchedulerService schedulerService;
	
	@Resource
	private ScriptingService scriptingService;
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		return "redirect:list";
	}
	
	/** List all scheudler's jobs */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelMap list() {
		JobsPageData data = new JobsPageData();
		data.setJobs(getSchedulerJobs());
		return new ModelMap("data", data);
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
		data.setFireTimesCount(fireTimesCount);
		data.setTriggers(Arrays.asList(new Trigger[]{ trigger }));
		data.setNextFireTimes(getNextFireTimes(trigger, fireTimesCount));
		return new ModelMap("data", data);
	}

	private List<String> getTriggerFullNames(List<Trigger> triggers) {
		List<String> list = new ArrayList<String>();
		for (Trigger trigger : triggers)
			list.add(trigger.getFullName());
		return list;
	}

	private List<String> getJobDetailFullNames(List<JobDetail> jobDetails) {
		List<String> list = new ArrayList<String>();
		for (JobDetail jobDetail : jobDetails)
			list.add(jobDetail.getFullName());
		return list;
	}
	
	private List<Date> getNextFireTimes(Trigger trigger, int fireTimesCount) {
		List<Date> fireTimes = schedulerService.getNextFireTimes(trigger, new Date(), fireTimesCount);
		return fireTimes;
	}
	
	private List<JobInfo> getSchedulerJobs() {
		List<JobInfo> jobs = new ArrayList<JobInfo>();
		List<JobDetail> jobDetails = schedulerService.getJobDetails();
		logger.debug("Found " + jobDetails.size() + " jobDetail objects.");
		for (JobDetail jobDetail : jobDetails) {
			JobInfo job = new JobInfo();
			jobs.add(job);
			job.setJobDetail(jobDetail);
			List<Trigger> triggers = schedulerService.getTriggers(jobDetail);
			logger.debug("JobDetail " + jobDetail.getFullName() + " has " + triggers.size() + " triggers.");
			if (triggers.size() > 0) {
				populateJobInfo(job, triggers.get(0));
				
				// For each extra trigger, we create another JobInfo for display
				for (int i = 1; i < triggers.size(); i++) {
					job = new JobInfo();
					jobs.add(job);
					job.setJobDetail(jobDetail);
					populateJobInfo(job, triggers.get(i));
				}
			}
		}
		Collections.sort(jobs);
		logger.debug("Found " + jobs.size() + " jobs");
		return jobs;
	}
	
	private void populateJobInfo(JobInfo job, Trigger trigger) {
		job.setTrigger(trigger);
		if (trigger instanceof CronTrigger) {
			job.setTriggerInfo("Cron=" + ((CronTrigger)trigger).getCronExpression());
		} else if (trigger instanceof SimpleTrigger) {
			SimpleTrigger simpleTrigger = (SimpleTrigger)trigger;
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			StringBuilder sb = new StringBuilder();
			sb.append("StartTime=" + df.format(simpleTrigger.getStartTime()));
			sb.append(", RepeatCount=" + simpleTrigger.getRepeatCount());
			if (simpleTrigger.getRepeatCount() > 0) {
				sb.append(", RepeatInterval=" + simpleTrigger.getRepeatInterval());				
			}
			if (simpleTrigger.getEndTime() != null) {
				sb.append(", StartTime=" + df.format(simpleTrigger.getEndTime()));		
			}
			job.setTriggerInfo(sb.toString());
		} 
	}

	//
	// Scripting 
	// =========

	@RequestMapping(value="/scripting/run", method=RequestMethod.GET)
	public ModelMap create() {
		ModelMap data = new ModelMap();
		return new ModelMap("data", data);
	}
	
	@RequestMapping(value="/scripting/run", method=RequestMethod.POST)
	public ModelMap createPost(@RequestParam String groovyScriptText) {
		logger.debug("Running Groovy Script Text.");
		Object ret = scriptingService.run(groovyScriptText);
		if (!(ret instanceof List) && ((List<?>)ret).size() < 2) {
			throw new RuntimeException("Groovy script did not return [jobDetail, trigger ...] list.");
		}
		
		List<?> resultList = (List<?>)ret;
		if (resultList.size() < 2)
			throw new RuntimeException("Groovy script did not return [jobDetail, trigger ...] list.");

		List<Trigger> triggers = new ArrayList<Trigger>(); // Saved list for page data display.
		
		// Add the first pair set.
		JobDetail jobDetail = (JobDetail)resultList.get(0);
		Trigger trigger = (Trigger)resultList.get(1);
		logger.info("Scheduling new trigger=" + trigger.getFullJobName() + 
				", and new job=" + trigger.getFullJobName());
		schedulerService.scheduleJob(jobDetail, trigger);
		triggers.add(trigger);
		
		// Add the rest of trigger to the same jobDetail if there are any.
		for (int i=2; i< resultList.size(); i++) {
			trigger = (Trigger)resultList.get(i);
			trigger.setJobName(jobDetail.getName());
			trigger.setJobGroup(jobDetail.getGroup());
			logger.info("Scheduling new trigger=" + trigger.getFullJobName() + 
					", with job=" + trigger.getFullJobName());
			schedulerService.scheduleJob(trigger);
			triggers.add(trigger);
		}
		
		ModelMap data = new ModelMap();
		data.put("jobDetail", jobDetail);
		data.put("triggers", triggers);
		return new ModelMap("data", data);
	}
}
