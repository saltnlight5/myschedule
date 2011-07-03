package myschedule.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.SchedulerService;
import myschedule.service.SchedulerServiceFinder;
import myschedule.service.XmlJobLoader;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
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
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired @Qualifier("schedulerServiceFinder")
	protected SchedulerServiceFinder schedulerServiceFinder;
	
	/** List all scheudler's jobs */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public DataModelMap list(HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		return new DataModelMap(getJobListPageData(schedulerService));
	}

	@RequestMapping(value="/list-executing-jobs", method=RequestMethod.GET)
	public DataModelMap listExecutingJobs(HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		if (!schedulerService.isStarted() || schedulerService.isPaused())
			throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, 
					"The current scheudler is not started or in standby mode. Plese start it first.");
		List<JobExecutionContext> jobs = schedulerService.getCurrentlyExecutingJobs();
		return new DataModelMap("jobExecutionContextList", jobs);
	}
	
	@RequestMapping(value="/list-no-trigger-jobs", method=RequestMethod.GET)
	public DataModelMap listNoTriggerJobs(HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		return new DataModelMap(getNoTriggerJobListPageData(schedulerService));
	}
	
	@RequestMapping(value="/unschedule", method=RequestMethod.GET)
	public DataModelMap unscheduleJob(
			@RequestParam String triggerName,
			@RequestParam String triggerGroup,
			HttpSession session) {
		logger.info("Unscheduling trigger name=" + triggerName + ", group=" + triggerGroup);
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		DataModelMap data = new DataModelMap();
		Trigger trigger = schedulerService.uncheduleJob(triggerName, triggerGroup);
		data.put("trigger", trigger);
		try {
			JobDetail jobDetail = schedulerService.getJobDetail(trigger.getJobName(), trigger.getJobGroup());
			data.put("jobDetail", jobDetail);
		} catch (ErrorCodeException e) {
			// Job no longer exists, and we allow this scenario, so do nothing. 
		}
		return new DataModelMap(data);
	}
	
	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public DataModelMap deleteAllJobsPost(
			@RequestParam String jobName,
			@RequestParam String jobGroup,
			HttpSession session) {
		logger.info("Deleting jobName=" + jobName + ", jobGroup=" + jobGroup + " and its associated triggers.");
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		JobDetail jobDetail = schedulerService.getJobDetail(jobName, jobGroup);
		List<Trigger> triggers = schedulerService.deleteJob(jobName, jobGroup);

		DataModelMap data = new DataModelMap();
		data.put("jobDetail", jobDetail);
		data.put("triggers", triggers);
		
		return new DataModelMap(data);
	}
	
	@RequestMapping(value="/run-job", method=RequestMethod.GET)
	public String runJob(
			@RequestParam String jobName,
			@RequestParam String jobGroup,
			HttpSession session) {
		logger.info("Run jobName=" + jobName + ", jobGroup=" + jobGroup + " now.");
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		schedulerService.runJob(jobName, jobGroup);		
		return "redirect:list";
	}
	
	/** Display form to load job-scheduling-data xml */
	@RequestMapping(value="/load-xml", method=RequestMethod.GET)
	public DataModelMap load() {
		DataModelMap data = new DataModelMap();
		data.put("xml", "");
		return new DataModelMap(data);
	}

	/** Process from for load job-scheduling-data xml */
	@RequestMapping(value="/load-xml-action", method=RequestMethod.POST)
	public DataModelMap loadPost(
			@RequestParam String xml, 
			HttpSession session) {
		logger.info("Loading xml jobs.");
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		JobLoadPageData data = new JobLoadPageData();
		XmlJobLoader loader = schedulerService.loadJobs(xml);
		data.setIgnoreDuplicates(loader.isIgnoreDuplicates());
		data.setOverWriteExistingData(loader.isOverWriteExistingData());
		data.setJobGroupsToNeverDelete(loader.getJobGroupsToNeverDelete());
		data.setTriggerGroupsToNeverDelete(loader.getTriggerGroupsToNeverDelete());
		data.setLoadedJobs(getJobDetailFullNames(loader.getLoadedJobs()));
		data.setLoadedTriggers(getTriggerFullNames(loader.getLoadedTriggers()));
		return new DataModelMap(data);
	}

	/** Show a trigger and its job detail page. */
	@RequestMapping(value="/job-detail", method=RequestMethod.GET)
	public DataModelMap jobDetail(
			@RequestParam String jobName, 
			@RequestParam String jobGroup, 
			HttpSession session) {
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		logger.debug("Viewing detail of jobName=" + jobName + ", jobGroup=" + jobGroup);
		JobDetail jobDetail = schedulerService.getJobDetail(jobName, jobGroup);
		JobTriggerDetailPageData data = new JobTriggerDetailPageData();
		data.setTriggers(schedulerService.getTriggers(jobDetail));
		data.setJobDetail(jobDetail);
		data.setJobDetailShouldRecover(jobDetail.requestsRecovery());
		return new DataModelMap(data);
	}

	/** Show a trigger and its job detail page. */
	@RequestMapping(value="/trigger-detail", method=RequestMethod.GET)
	public DataModelMap triggerDetail(
			@RequestParam String triggerName,
			@RequestParam String triggerGroup,
			@RequestParam int fireTimesCount, 
			HttpSession session) {
		logger.debug("Viewing detail of triggerName=" + triggerName + ", triggerGroup=" + triggerGroup + "[fireTimesCount=" + fireTimesCount + "]");
		SchedulerService schedulerService = schedulerServiceFinder.find(session);
		Trigger trigger = schedulerService.getTrigger(triggerName, triggerGroup);
		List<Date> nextFireTimes = schedulerService.getNextFireTimes(trigger, new Date(), fireTimesCount);
		JobTriggerDetailPageData data = new JobTriggerDetailPageData();
		data.setJobDetail(schedulerService.getJobDetail(trigger.getJobName(), trigger.getJobGroup()));
		data.setFireTimesCount(fireTimesCount);
		data.setTriggers(Arrays.asList(new Trigger[]{ trigger }));
		data.setNextFireTimes(nextFireTimes);
		return new DataModelMap(data);
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
	
	/** Return only jobs with trigger associated. */
	protected JobListPageData getJobListPageData(SchedulerService schedulerService) {
		List<Trigger> triggers = new ArrayList<Trigger>();
		
		List<JobDetail> allJobDetails = schedulerService.getJobDetails();
		logger.debug("There are total " + allJobDetails.size() + " jobDetails");
		
		for (JobDetail jobDetail : allJobDetails) {
			List<Trigger> jobTriggers = schedulerService.getTriggers(jobDetail);
			if (jobTriggers.size() > 0) {
				triggers.addAll(jobTriggers);
			}
		}
		logger.debug("Found " + triggers.size() + " triggers.");

		// Let's sort them.
		sortJobListTriggers(triggers);
		
		JobListPageData data = new JobListPageData();
		data.setTriggers(triggers);
		return data;
	}

	/** Return only jobs without trigger associated. */
	protected Object getNoTriggerJobListPageData(SchedulerService schedulerService) {
		List<JobDetail> noTriggerJobDetails = new ArrayList<JobDetail>();
		
		List<JobDetail> allJobDetails = schedulerService.getJobDetails();
		logger.debug("There are total " + allJobDetails.size() + " jobDetails");
		
		for (JobDetail jobDetail : allJobDetails) {
			List<Trigger> jobTriggers = schedulerService.getTriggers(jobDetail);
			if (jobTriggers.size() == 0) {
				noTriggerJobDetails.add(jobDetail);
			}
		}
		logger.debug("Found " + noTriggerJobDetails.size() + " noTriggerJobDetails.");

		// Let's sort them.
		sortJobListNoTriggerJobDetails(noTriggerJobDetails);
		
		JobListPageData data = new JobListPageData();
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
