package myschedule.web.servlet.app.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.MutableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.service.QuartzSchedulerService;
import myschedule.web.controller.DataModelMap;
import myschedule.web.controller.JobListPageData;
import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.ViewData;
import myschedule.web.servlet.ViewDataActionHandler;
import myschedule.web.session.SessionSchedulerServiceFinder;

public class JobHandlers {
	
	private static final Logger logger = LoggerFactory.getLogger(JobHandlers.class);
	
	@Setter
	protected SessionSchedulerServiceFinder schedulerServiceFinder;
	
	@Getter
	protected ActionHandler listHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			HttpSession session = viewData.getRequest().getSession(true);
			QuartzSchedulerService ss = schedulerServiceFinder.findSchedulerService(session);
			viewData.addData("data", new DataModelMap(getJobListPageData(ss)));
		}
	};
	

	protected List<String> getTriggerFullNames(List<MutableTrigger> triggers) {
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
		List<JobDetail> allJobDetails = schedulerTemplate.getAllJobDetails();
		for (JobDetail jobDetail : allJobDetails) {
			List<? extends Trigger> jobTriggers = schedulerTemplate.getTriggersOfJob(jobDetail.getKey());
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
		List<JobDetail> allJobDetails = schedulerTemplate.getAllJobDetails();
		for (JobDetail jobDetail : allJobDetails) {
			List<? extends Trigger> jobTriggers = schedulerTemplate.getTriggersOfJob(jobDetail.getKey());
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
				int ret = o1.getJobKey().compareTo(o2.getJobKey());
				if (ret == 0) {
					ret = o1.getKey().compareTo(o2.getKey());
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
				return o1.getKey().compareTo(o2.getKey());
			}			
		});
	}
}
