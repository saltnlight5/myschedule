package myschedule.web.servlet.app.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import myschedule.quartz.extra.QuartzExtraUtils;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.XmlJobLoader;
import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.SchedulerContainer;
import myschedule.service.SchedulerService;
import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.ViewData;
import myschedule.web.servlet.ViewDataActionHandler;
import myschedule.web.servlet.app.handler.pagedata.JobListPageData;
import myschedule.web.servlet.app.handler.pagedata.JobLoadPageData;
import myschedule.web.servlet.app.handler.pagedata.JobTriggerDetailPageData;
import myschedule.web.session.SessionData;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.spi.MutableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobHandlers {
	
	private static final Logger logger = LoggerFactory.getLogger(JobHandlers.class);
	
	@Setter
	private SchedulerContainer schedulerContainer;
	
	@Getter
	protected ActionHandler listHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			viewData.addData("data", getJobListPageData(schedulerService));
		}
	};
	
	@Getter
	protected ActionHandler listExecutingJobsHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
			List<JobExecutionContext> jobs = schedulerTemplate.getCurrentlyExecutingJobs();
			viewData.addData("data", ViewData.mkMap("jobExecutionContextList", jobs));
		}
	};
	
	@Getter
	protected ActionHandler listNoTriggerJobsHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			viewData.addData("data", getNoTriggerJobListPageData(schedulerService));
		}
	};
	
	@Getter
	protected ActionHandler listCalendarsHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
			List<Object> calendars = new ArrayList<Object>();
			List<String> names = schedulerTemplate.getCalendarNames();
			Collections.sort(names);
			for (String name : names) {
				calendars.add(schedulerTemplate.getCalendar(name));
			}
			viewData.addData("data", ViewData.mkMap("calendarNames", names, "calendars", calendars));
		}
	};

	@Getter
	protected ActionHandler unscheduleHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			String triggerName = viewData.findData("triggerName");
			String triggerGroup = viewData.findData("triggerGroup");
			logger.debug("Unscheduling trigger name=" + triggerName + ", group=" + triggerGroup);
			SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
			Trigger trigger = schedulerTemplate.uncheduleJob(TriggerKey.triggerKey(triggerName, triggerGroup));
			Map<String, Object> map = ViewData.mkMap("trigger", trigger);
			try {
				JobKey key = trigger.getJobKey();
				JobDetail jobDetail = schedulerTemplate.getJobDetail(key);
				map.put("jobDetail", jobDetail);
			} catch (ErrorCodeException e) {
				// Job no longer exists, and we allow this scenario, so do nothing. 
			}
			viewData.addData("data", map);
		}
	};

	@Getter
	protected ActionHandler deleteHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			String jobName = viewData.findData("jobName");
			String jobGroup = viewData.findData("jobGroup");
			logger.debug("Deleting jobName=" + jobName + ", jobGroup=" + jobGroup + " and its associated triggers.");
			SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
			JobDetail jobDetail = schedulerTemplate.getJobDetail(JobKey.jobKey(jobName, jobGroup));
			List<? extends Trigger> triggers = schedulerTemplate.deleteJobAndGetTriggers(JobKey.jobKey(jobName, jobGroup));
			viewData.addData("data", ViewData.mkMap("jobDetail", jobDetail, "triggers", triggers));
		}
	};
	
	@Getter
	protected ActionHandler runJobHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			String jobName = viewData.findData("jobName");
			String jobGroup = viewData.findData("jobGroup");
			logger.debug("Run jobName=" + jobName + ", jobGroup=" + jobGroup + " now.");
			SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
			schedulerTemplate.triggerJob(JobKey.jobKey(jobName, jobGroup));
			viewData.setViewName("redirect:/job/list");
		}
	};
	
	@Getter
	protected ActionHandler loadXmlHandler = new ViewDataActionHandler();
	
	@Getter
	protected ActionHandler loadXmlActionHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			String xml = viewData.findData("xml");
			logger.debug("Loading xml jobs.");
			InputStream inStream = null;
			try {
				inStream = new ByteArrayInputStream(xml.getBytes());
				Scheduler quartzScheduler = schedulerService.getScheduler().getScheduler();
				XmlJobLoader loader = QuartzExtraUtils.scheduleXmlSchedulingData(inStream, quartzScheduler);
				JobLoadPageData data = new JobLoadPageData();
				data.setIgnoreDuplicates(loader.isIgnoreDuplicates());
				data.setOverWriteExistingData(loader.isOverWriteExistingData());
				data.setJobGroupsToNeverDelete(loader.getJobGroupsToNeverDelete());
				data.setTriggerGroupsToNeverDelete(loader.getTriggerGroupsToNeverDelete());
				data.setLoadedJobs(getJobDetailFullNames(loader.getLoadedJobs()));
				data.setLoadedTriggers(getTriggerFullNames(loader.getLoadedTriggers()));
				viewData.setViewName("/job/load-xml-action");
				viewData.addData("data", data);
			} catch (Exception e) {
				viewData.setViewName("/job/load-xml");
				viewData.addData("data", ViewData.mkMap( 
						"xml", xml,
						"errorMessage", ExceptionUtils.getMessage(e),
						"fullStackTrace", ExceptionUtils.getFullStackTrace(e)));
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
					} catch (IOException e) {
						throw new ErrorCodeException(ErrorCode.WEB_UI_PROBLEM, "Failed to read job data xml input stream.", e);
					}
				}
			}
		}
	};
	
	/** Show a trigger and its job detail page. */	
	@Getter
	protected ActionHandler jobDetailHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			String jobName = viewData.findData("jobName");
			String jobGroup = viewData.findData("jobGroup");
			logger.debug("Viewing detail of jobName=" + jobName + ", jobGroup=" + jobGroup);
			SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
			JobDetail jobDetail = schedulerTemplate.getJobDetail(JobKey.jobKey(jobName, jobGroup));
			JobTriggerDetailPageData data = new JobTriggerDetailPageData();
			data.setTriggers(schedulerTemplate.getTriggersOfJob(jobDetail.getKey()));
			data.setJobDetail(jobDetail);
			data.setJobDetailShouldRecover(jobDetail.requestsRecovery());

			List<String> triggerStatusList = new ArrayList<String>();
			for (Trigger trigger : data.getTriggers()) {
				TriggerKey tk = trigger.getKey();
				triggerStatusList.add(schedulerTemplate.getTriggerState(tk).toString());
			}
			data.setTriggerStatusList(triggerStatusList);
			viewData.addData("data", data);
		}
	};
	
	@Getter
	protected ActionHandler triggerDetailHandler = new ViewDataActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			String triggerName = viewData.findData("triggerName");
			String triggerGroup = viewData.findData("triggerGroup");
			int fireTimesCount = Integer.parseInt(viewData.findData("fireTimesCount", "20"));
			SchedulerTemplate st = schedulerService.getScheduler();
			Trigger trigger = st.getTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
			List<Date> nextFireTimes = st.getNextFireTimes(trigger, new Date(), fireTimesCount);
			JobTriggerDetailPageData data = new JobTriggerDetailPageData();
			JobKey jobKey = trigger.getJobKey();
			TriggerKey triggerKey = trigger.getKey();
			data.setJobDetail(st.getJobDetail(jobKey));
			data.setFireTimesCount(fireTimesCount);
			data.setTriggers(Arrays.asList(new Trigger[]{ trigger }));
			String statusStr = st.getTriggerState(triggerKey).toString();
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
			viewData.addData("data", data);
		}
	};
	
	@Getter
	protected ActionHandler schedulerDownHandler = new ViewDataActionHandler();
	

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
	protected JobListPageData getJobListPageData(SchedulerService schedulerService) {
		SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
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
	protected Object getNoTriggerJobListPageData(SchedulerService schedulerService) {
		SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
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
