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
import myschedule.service.QuartzSchedulerService;
import myschedule.service.quartz.SchedulerTemplate;
import myschedule.service.quartz.XmlJobLoader;
import myschedule.web.SessionSchedulerServiceFinder;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
	
	/** List all scheudler's jobs */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public DataModelMap list(HttpSession session) {
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		return new DataModelMap(getJobListPageData(ss));
	}

	@RequestMapping(value="/list-executing-jobs", method=RequestMethod.GET)
	public DataModelMap listExecutingJobs(HttpSession session) {
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		SchedulerTemplate schedulerTemplate = new SchedulerTemplate(ss.getScheduler());
		List<JobExecutionContext> jobs = schedulerTemplate.getCurrentlyExecutingJobs();
		return new DataModelMap("jobExecutionContextList", jobs);
	}
	
	@RequestMapping(value="/list-no-trigger-jobs", method=RequestMethod.GET)
	public DataModelMap listNoTriggerJobs(HttpSession session) {
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		return new DataModelMap(getNoTriggerJobListPageData(ss));
	}
	
	@RequestMapping(value="/list-calendars", method=RequestMethod.GET)
	public DataModelMap listCalendars(HttpSession session) {
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		SchedulerTemplate schedulerTemplate = new SchedulerTemplate(ss.getScheduler());
		DataModelMap data = new DataModelMap();
		List<Object> calendars = new ArrayList<Object>();
		List<String> names = schedulerTemplate.getCalendarNames();
		Collections.sort(names);
		for (String name : names)
			calendars.add(schedulerTemplate.getCalendar(name));
		data.addData("calendarNames", names);
		data.addData("calendars", calendars);
		return data;
	}
	
	@RequestMapping(value="/unschedule", method=RequestMethod.GET)
	public DataModelMap unscheduleJob(
			@RequestParam String triggerName,
			@RequestParam String triggerGroup,
			HttpSession session) {
		logger.debug("Unscheduling trigger name=" + triggerName + ", group=" + triggerGroup);
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		SchedulerTemplate schedulerTemplate = new SchedulerTemplate(ss.getScheduler());
		DataModelMap data = new DataModelMap();
		Trigger trigger = schedulerTemplate.uncheduleJob(triggerName, triggerGroup);
		data.put("trigger", trigger);
		try {
			JobDetail jobDetail = schedulerTemplate.getJobDetail(trigger.getName(), trigger.getGroup());
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
		logger.debug("Deleting jobName=" + jobName + ", jobGroup=" + jobGroup + " and its associated triggers.");
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		SchedulerTemplate schedulerTemplate = new SchedulerTemplate(ss.getScheduler());
		JobDetail jobDetail = schedulerTemplate.getJobDetail(jobName, jobGroup);
		List<? extends Trigger> triggers = schedulerTemplate.deleteJob(jobName, jobGroup);

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
		logger.debug("Run jobName=" + jobName + ", jobGroup=" + jobGroup + " now.");
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		SchedulerTemplate schedulerTemplate = new SchedulerTemplate(ss.getScheduler());
		schedulerTemplate.runJobNow(jobName, jobGroup);		
		return "redirect:list";
	}
	
	/** Display form to load job-scheduling-data xml */
	@RequestMapping(value="/load-xml", method=RequestMethod.GET)
	public DataModelMap loadXml() {
		DataModelMap data = new DataModelMap();
		data.put("xml", "");
		return new DataModelMap(data);
	}

	/** Process from for load job-scheduling-data xml */
	@RequestMapping(value="/load-xml-action", method=RequestMethod.POST)
	public ModelAndView loadXmlAction(
			@RequestParam String xml, 
			HttpSession session) {
		logger.debug("Loading xml jobs.");
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		SchedulerTemplate schedulerTemplate = new SchedulerTemplate(ss.getScheduler());
		try {
			XmlJobLoader loader = schedulerTemplate.scheduleXmlSchedulingData(xml);
			JobLoadPageData data = new JobLoadPageData();
			data.setIgnoreDuplicates(loader.isIgnoreDuplicates());
			data.setOverWriteExistingData(loader.isOverWriteExistingData());
			data.setJobGroupsToNeverDelete(loader.getJobGroupsToNeverDelete());
			data.setTriggerGroupsToNeverDelete(loader.getTriggerGroupsToNeverDelete());
			data.setLoadedJobs(getJobDetailFullNames(loader.getLoadedJobs()));
			data.setLoadedTriggers(getTriggerFullNames(loader.getLoadedTriggers()));
			return new ModelAndView("job/load-xml-action", new DataModelMap(data));
		} catch (Exception e) {
			DataModelMap data = new DataModelMap();
			data.addData("xml", xml);
			data.addData("errorMessage", ExceptionUtils.getMessage(e));
			data.addData("fullStackTrace", ExceptionUtils.getFullStackTrace(e));
			return new ModelAndView("job/load-xml", data);
		}
	}

	/** Show a trigger and its job detail page. */
	@RequestMapping(value="/job-detail", method=RequestMethod.GET)
	public DataModelMap jobDetail(
			@RequestParam String jobName, 
			@RequestParam String jobGroup, 
			HttpSession session) {
		logger.debug("Viewing detail of jobName=" + jobName + ", jobGroup=" + jobGroup);
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		SchedulerTemplate st = new SchedulerTemplate(ss.getScheduler());
		JobDetail jobDetail = st.getJobDetail(jobName, jobGroup);
		JobTriggerDetailPageData data = new JobTriggerDetailPageData();
		data.setTriggers(st.getTriggers(jobDetail));
		data.setJobDetail(jobDetail);
		data.setJobDetailShouldRecover(jobDetail.requestsRecovery());

		List<String> triggerStatusList = new ArrayList<String>();
		for (Trigger trigger : data.getTriggers()) {
			int state = st.getTriggerState(trigger.getName(), trigger.getGroup());
			triggerStatusList.add(getTriggerStateName(state));
		}
		data.setTriggerStatusList(triggerStatusList);
		
		return new DataModelMap(data);
	}
	
	protected String getTriggerStateName(int triggerState) {
		switch (triggerState) {
		case Trigger.STATE_BLOCKED:
			return "BLOCKED";
		case Trigger.STATE_COMPLETE:
			return "COMPLETE";
		case Trigger.STATE_ERROR:
			return "ERROR";
		case Trigger.STATE_NONE:
			return "NONE";
		case Trigger.STATE_NORMAL:
			return "NORMAL";
		case Trigger.STATE_PAUSED:
			return "PAUSED";
		default:
			// This should not happen!
			return "*** UNKNOWN! ***";
		}
	}

	/** Show a trigger and its job detail page. */
	@RequestMapping(value="/trigger-detail", method=RequestMethod.GET)
	public DataModelMap triggerDetail(
			@RequestParam String triggerName,
			@RequestParam String triggerGroup,
			@RequestParam int fireTimesCount, 
			HttpSession session) {
		QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
		SchedulerTemplate st = new SchedulerTemplate(ss.getScheduler());
		Trigger trigger = st.getTrigger(triggerName, triggerGroup);
		List<Date> nextFireTimes = st.getNextFireTimes(trigger, new Date(), fireTimesCount);
		JobTriggerDetailPageData data = new JobTriggerDetailPageData();
		data.setJobDetail(st.getJobDetail(trigger.getJobName(), trigger.getJobGroup()));
		data.setFireTimesCount(fireTimesCount);
		data.setTriggers(Arrays.asList(new Trigger[]{ trigger }));
		String statusStr = getTriggerStateName(st.getTriggerState(trigger.getName(), trigger.getGroup()));
		data.setTriggerStatusList(Arrays.asList(new String[]{ statusStr }));
		data.setNextFireTimes(nextFireTimes);
		
		// Calculate excludeByCalendar
		List<String> excludeByCalendar = new ArrayList<String>(nextFireTimes.size());
		String calName = trigger.getCalendarName();
		if (calName != null) {
			try {
				Scheduler scheduler = st.getScheduler();
				Calendar cal = scheduler.getCalendar(calName);
				for (Date dt : nextFireTimes) {
					if (!cal.isTimeIncluded(dt.getTime())) {
						excludeByCalendar.add("Yes. " + calName + ": " + cal.toString());
					} else {
						excludeByCalendar.add("No");
					}
				}
			} catch (SchedulerException e) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, "Failed to calculate next fire times with Calendar " + calName, e);
			}
		} 
		data.setExcludeByCalendar(excludeByCalendar);
		
		return new DataModelMap(data);
	}
	
	@RequestMapping(value="/scheduler-down", method=RequestMethod.GET)
	public DataModelMap schedulerDown() {
	        return new DataModelMap();
	}

	protected List<String> getTriggerFullNames(List<Trigger> triggers) {
		List<String> list = new ArrayList<String>();
		for (Trigger trigger : triggers)
			list.add(trigger.getKey().toString());
		return list;
	}

	protected List<String> getJobDetailFullNames(List<JobDetail> jobDetails) {
		List<String> list = new ArrayList<String>();
		for (JobDetail jobDetail : jobDetails)
			list.add(jobDetail.getKey().toString());
		return list;
	}
	
	/** Return only jobs with trigger associated. */
	protected JobListPageData getJobListPageData(QuartzSchedulerService schedulerService) {
		SchedulerTemplate schedulerTemplate = new SchedulerTemplate(schedulerService.getScheduler());
		List<Trigger> triggers = new ArrayList<Trigger>();		
		List<JobDetail> allJobDetails = schedulerTemplate.getJobDetails();
		for (JobDetail jobDetail : allJobDetails) {
			List<? extends Trigger> jobTriggers = schedulerTemplate.getTriggers(jobDetail);
			if (jobTriggers.size() > 0) {
				triggers.addAll(jobTriggers);
			}
		}
		logger.debug("Found " + triggers.size() + " triggers.");

		// Let's sort them.
		sortJobListTriggers(triggers);
		JobListPageData data = new JobListPageData();
		data.setTriggers(triggers);
		data.setSchedulerService(schedulerService);
		return data;
	}

	/** Return only jobs without trigger associated. */
	protected Object getNoTriggerJobListPageData(QuartzSchedulerService schedulerService) {
		SchedulerTemplate schedulerTemplate = new SchedulerTemplate(schedulerService.getScheduler());
		List<JobDetail> noTriggerJobDetails = new ArrayList<JobDetail>();
		List<JobDetail> allJobDetails = schedulerTemplate.getJobDetails();
		for (JobDetail jobDetail : allJobDetails) {
			List<? extends Trigger> jobTriggers = schedulerTemplate.getTriggers(jobDetail);
			if (jobTriggers.size() == 0) {
				noTriggerJobDetails.add(jobDetail);
			}
		}
		// Let's sort them.
		sortJobListNoTriggerJobDetails(noTriggerJobDetails);
		
		JobListPageData data = new JobListPageData();
		data.setSchedulerService(schedulerService);
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
				String jobName1 = o1.getJobGroup() + o1.getJobName();
				String jobName2 = o2.getJobGroup() + o2.getJobName();
				int ret = jobName1.compareTo(jobName2);
				if (ret == 0) {
					String name1 = o1.getGroup() + o1.getName();
					String name2 = o2.getGroup() + o2.getName();
					ret = name1.compareTo(name2);
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
				String name1 = o1.getGroup() + o1.getName();
				String name2 = o2.getGroup() + o2.getName();
				return name1.compareTo(name2);
			}			
		});
	}
}
