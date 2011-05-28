package myschedule.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.annotation.Resource;

import myschedule.service.ObjectUtils;
import myschedule.service.ObjectUtils.Getter;
import myschedule.service.SchedulerService;
import myschedule.web.controller.JobsPageData.JobInfo;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/** 
 * Scheduler web controller.
 *
 * @author Zemian Deng
 */
@Controller
@RequestMapping(value="/scheduler")
public class SchedulerController {
	protected static Logger logger = LoggerFactory.getLogger(SchedulerController.class);
	
	@Resource
	protected SchedulerService schedulerService;
			
	/** Show DashboardPageData. */
	@RequestMapping(value="/dashboard", method=RequestMethod.GET)
	public ModelMap dashboard() {
		DashboardPageData data = new DashboardPageData();
		data.setSchedulerInfo(getSchedulerInfo());
		return new ModelMap("data", data);
	}

	/** Show JobsPageData. */
	@RequestMapping(value="/jobs", method=RequestMethod.GET)
	public ModelMap jobs() {
		JobsPageData data = new JobsPageData();
		data.setSchedulerSummary(getSchedulerSummary());
		data.setJobs(getSchedulerJobs());
		return new ModelMap("data", data);
	}
	
	/** Show TriggerPageData. */
	@RequestMapping(value="/trigger", method=RequestMethod.GET)
	public ModelMap jobNextFireTimes(
			@RequestParam String triggerName,
			@RequestParam String triggerGroup,
			@RequestParam int nextFireTimesRequested) {
		TriggerPageData data = new TriggerPageData();
		data.setNextFireTimesRequested(nextFireTimesRequested);
		data.setTrigger(getTrigger(triggerName, triggerGroup));
		data.setJobDetail(getJobDetail(data.getTrigger()));
		data.setNextFireTimes(getNextFireTimes(data.getTrigger(), nextFireTimesRequested));
		return new ModelMap("data", data);
	}

	protected TreeMap<String, String> getSchedulerInfo() {
		SchedulerMetaData schedulerMetaData = schedulerService.getSchedulerMetaData();
		TreeMap<String, String> schedulerInfo = new TreeMap<String, String>();
		List<Getter> getters = ObjectUtils.getGetters(schedulerMetaData);
		for (Getter getter : getters) {
			String key = getter.getPropName();
			if (key.length() >= 1) {
				if (key.equals("summary")) // skip this field.
					continue;
				key = key.substring(0, 1).toUpperCase() + key.substring(1);
				String value = ObjectUtils.getGetterStrValue(getter);
				schedulerInfo.put(key, value);
			} else {
				logger.warn("Skipped a scheduler info key length less than 1 char. Key=" + key);
			}
		}
		logger.debug("Scheduler info map has " + schedulerInfo.size() + " items.");
		return schedulerInfo;
	}

	protected String getSchedulerSummary() {
		try {
			return schedulerService.getSchedulerMetaData().getSummary();
		} catch (SchedulerException e) {
			throw new RuntimeException("Failed to get scheduler summary.", e);
		}
	}
	
	protected List<JobInfo> getSchedulerJobs() {
		List<JobInfo> jobs = new ArrayList<JobInfo>();
		List<JobDetail> jobDetails = schedulerService.getJobDetails();
		logger.debug("Found " + jobDetails.size() + " jobDetials.");
		for (JobDetail jobDetail : jobDetails) {
			JobInfo job = new JobInfo();
			jobs.add(job);
			job.setJobDetail(jobDetail);
			Trigger[] triggers = schedulerService.getTriggers(jobDetail);
			logger.debug("JobDetail " + jobDetail.getFullName() + " has " + triggers.length + " triggers.");
			if (triggers.length > 0) {
				job.setTrigger(triggers[0]);
				
				// For each extra trigger, we create another JobInfo for display
				for (int i = 1; i < triggers.length; i++) {
					job = new JobInfo();
					jobs.add(job);
					job.setJobDetail(jobDetail);
					job.setTrigger(triggers[i]);
				}
			}
		}
		Collections.sort(jobs);
		logger.debug("Found " + jobs.size() + " jobs");
		return jobs;
	}
	
	protected JobDetail getJobDetail(Trigger trigger) {
		String jobName = trigger.getJobName();
		String jobGroup = trigger.getJobGroup();
		return schedulerService.getJobDetail(jobName, jobGroup);
	}
	
	protected Trigger getTrigger(String triggerName, String triggerGroup) {
		return schedulerService.getTrigger(triggerName, triggerGroup);
	}

	protected List<Date> getNextFireTimes(Trigger trigger, int nextFireTimesRequested) {
		List<Date> fireTimes = schedulerService.getNextFireTimes(trigger, new Date(), nextFireTimesRequested);
		return fireTimes;
	}
}
