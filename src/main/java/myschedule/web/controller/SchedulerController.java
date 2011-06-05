package myschedule.web.controller;

import java.util.ArrayList;
import java.util.Collections;
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

	/** Show scheduler status page */
	@RequestMapping(value="/status", method=RequestMethod.GET)
	public ModelMap index() {
		SchedulerMetaData schedulerMetaData = schedulerService.getSchedulerMetaData();
		SchedulerStatusPageData data = new SchedulerStatusPageData();
		data.setSchedulerName(schedulerMetaData.getSchedulerName());
		data.setSchedulerStarted(schedulerMetaData.isStarted());
		if (schedulerMetaData.isStarted())
			data.setSchedulerInfo(getSchedulerInfo(schedulerMetaData));
		return new ModelMap("data", data);
	}

	protected TreeMap<String, String> getSchedulerInfo(SchedulerMetaData schedulerMetaData) {
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
	
	/** Show JobsPageData. */
	@RequestMapping(value="/jobs", method=RequestMethod.GET)
	public ModelMap jobs() {
		JobsPageData data = new JobsPageData();
		data.setSchedulerSummary(getSchedulerSummary());
		data.setJobs(getSchedulerJobs());
		return new ModelMap("data", data);
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
	
}
