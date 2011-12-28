package myschedule.web.servlet.app.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import myschedule.quartz.extra.JdbcSchedulerHistoryPlugin;
import myschedule.quartz.extra.QuartzExtraUtils;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.XmlJobLoader;
import myschedule.service.ErrorCode;
import myschedule.service.ErrorCodeException;
import myschedule.service.QuartzUtils;
import myschedule.service.SchedulerContainer;
import myschedule.service.SchedulerService;
import myschedule.service.Tuple2;
import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.UrlRequestActionHandler;
import myschedule.web.servlet.ViewData;
import myschedule.web.session.SessionData;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.spi.MutableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobHandlers {
	
	private static final Logger logger = LoggerFactory.getLogger(JobHandlers.class);
	
	@Setter
	private SchedulerContainer schedulerContainer;
	@Setter
	private int defaultFireTimesCount;
	
	@Getter
	private ActionHandler pauseTriggerHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			String triggerName = viewData.findData("triggerName");
			String triggerGroup = viewData.findData("triggerGroup");

			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			SchedulerTemplate scheduler = schedulerService.getScheduler();
			scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
			
			viewData.setViewName("redirect:/job/list");
		}
	};
	
	@Getter
	private ActionHandler resumeTriggerHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			String triggerName = viewData.findData("triggerName");
			String triggerGroup = viewData.findData("triggerGroup");

			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			SchedulerTemplate scheduler = schedulerService.getScheduler();
			scheduler.resumeTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
			
			viewData.setViewName("redirect:/job/list");
		}
	};
	
	@Getter
	private ActionHandler listHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			SchedulerTemplate st = schedulerService.getScheduler();
			
			List<JobData.JobWithTrigger> jobWithTriggerList = new ArrayList<JobData.JobWithTrigger>();
			List<JobDetail> allJobDetails = st.getAllJobDetails();
			for (JobDetail jobDetail : allJobDetails) {
				List<? extends Trigger> triggers = st.getTriggersOfJob(jobDetail.getKey());
				for (Trigger trigger : triggers) {
					String triggerScheduleDesc = trigger.getClass().getName();
					if (trigger instanceof SimpleTrigger) {
						SimpleTrigger t = (SimpleTrigger)trigger;
						if (t.getRepeatCount() == SimpleTrigger.REPEAT_INDEFINITELY)
							triggerScheduleDesc = "Repeat=FOREVER";
						else
							triggerScheduleDesc = "Repeat=" + t.getRepeatCount();
						triggerScheduleDesc += ", Interval=" + t.getRepeatInterval();
					} else if (trigger instanceof CronTrigger) {
						CronTrigger t = (CronTrigger)trigger;
						triggerScheduleDesc = "Cron=" + t.getCronExpression();				
					}
					
					boolean paused = QuartzUtils.isTriggerPaused(trigger, st);
					
					JobData.JobWithTrigger jobWithTrigger = new JobData.JobWithTrigger();
					jobWithTrigger.setTrigger(trigger);
					jobWithTrigger.setTriggerScheduleDesc(triggerScheduleDesc);
					jobWithTrigger.setPaused(paused);
					
					jobWithTriggerList.add(jobWithTrigger);
				}
			}

			// Let's sort them.
			Collections.sort(jobWithTriggerList);
			
			viewData.addData("data", 
					ViewData.mkMap("jobWithTriggerList", jobWithTriggerList, 
							"scheduler", st, 
							"fireTimesCount", defaultFireTimesCount));
		}
	};
	
	@Getter
	private ActionHandler listExecutingJobsHandler = new UrlRequestActionHandler() {
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
	private ActionHandler listNoTriggerJobsHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			SchedulerTemplate st = schedulerService.getScheduler();
			
			List<JobDetail> jobWithoutTriggerList = new ArrayList<JobDetail>();
			List<JobDetail> allJobDetails = st.getAllJobDetails();
			for (JobDetail jobDetail : allJobDetails) {
				List<? extends Trigger> triggers = st.getTriggersOfJob(jobDetail.getKey());
				if (triggers.size() == 0) {
					jobWithoutTriggerList.add(jobDetail);
				}
			}

			// Let's sort them.
			Collections.sort(jobWithoutTriggerList, new Comparator<JobDetail>() {
				@Override
				public int compare(JobDetail o1, JobDetail o2) {
					return o1.getKey().compareTo(o2.getKey());
				}			
			});
			
			viewData.addData("data", 
					ViewData.mkMap("jobWithoutTriggerList", jobWithoutTriggerList));
		}
	};
	
	@Getter
	private ActionHandler listCalendarsHandler = new UrlRequestActionHandler() {
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
	private ActionHandler unscheduleHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			String triggerName = viewData.findData("triggerName");
			String triggerGroup = viewData.findData("triggerGroup");
			logger.debug("Unscheduling trigger name=" + triggerName + ", group=" + triggerGroup);
			SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
			Trigger trigger = schedulerTemplate.getTrigger(triggerKey);
			if (trigger == null) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, "Trigger " + triggerKey + " not found.");
			}
			schedulerTemplate.uncheduleJob(TriggerKey.triggerKey(triggerName, triggerGroup));
			Map<String, Object> map = ViewData.mkMap("trigger", trigger);
			
			// After we unscheduled the trigger, we want to see if there is more job that associated to the trigger.
			// The view is using the 'jobDetail' to check as empty or not to display a message.
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
	private ActionHandler deleteHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			String jobName = viewData.findData("jobName");
			String jobGroup = viewData.findData("jobGroup");
			logger.debug("Deleting jobName=" + jobName + ", jobGroup=" + jobGroup + " and its associated triggers.");
			SchedulerTemplate schedulerTemplate = schedulerService.getScheduler();
			JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
			JobDetail jobDetail = schedulerTemplate.getJobDetail(jobKey);
			if (jobDetail == null) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, "Job " + jobKey + " not found.");
			}
			List<? extends Trigger> triggers = schedulerTemplate.deleteJobAndGetTriggers(JobKey.jobKey(jobName, jobGroup));
			viewData.addData("data", ViewData.mkMap("jobDetail", jobDetail, "triggers", triggers));
		}
	};
	
	@Getter
	private ActionHandler runJobHandler = new UrlRequestActionHandler() {
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
	private ActionHandler loadXmlHandler = new UrlRequestActionHandler();
	
	@Getter
	private ActionHandler loadXmlActionHandler = new UrlRequestActionHandler() {
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
				JobData.XmlLoadedJobList xmlLoadedJobList = new JobData.XmlLoadedJobList();
				xmlLoadedJobList.setIgnoreDuplicates(loader.isIgnoreDuplicates());
				xmlLoadedJobList.setOverWriteExistingData(loader.isOverWriteExistingData());
				xmlLoadedJobList.setJobGroupsToNeverDelete(loader.getJobGroupsToNeverDelete());
				xmlLoadedJobList.setTriggerGroupsToNeverDelete(loader.getTriggerGroupsToNeverDelete());
				xmlLoadedJobList.setLoadedJobs(getJobDetailFullNames(loader.getLoadedJobs()));
				xmlLoadedJobList.setLoadedTriggers(getTriggerFullNames(loader.getLoadedTriggers()));
				viewData.addData("data", ViewData.mkMap("xmlLoadedJobList", xmlLoadedJobList));
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
	private ActionHandler jobDetailHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			String jobName = viewData.findData("jobName");
			String jobGroup = viewData.findData("jobGroup");
			SchedulerTemplate st = schedulerService.getScheduler();
			JobDetail jobDetail = st.getJobDetail(JobKey.jobKey(jobName, jobGroup));
			
			List<JobData.TriggerDetail> triggerWrappersList = new ArrayList<JobData.TriggerDetail>();
			List<? extends Trigger> triggers = st.getTriggersOfJob(jobDetail.getKey());
			int index = 0;
			for (Trigger trigger : triggers) {
				String misfireInstructionName = QuartzUtils.getMisfireInstructionName(trigger);
				String triggerStatusName = st.getTriggerState(trigger.getKey()).toString();
				String jobClassName = jobDetail.getJobClass().getName();
				
				JobData.TriggerDetail triggerWrapper = new JobData.TriggerDetail(trigger);
				triggerWrapper.setListIndex(index++);
				triggerWrapper.setTriggerStatusName(triggerStatusName);
				triggerWrapper.setMisfireInstructionName(misfireInstructionName);
				triggerWrapper.setJobClassName(jobClassName);
				triggerWrappersList.add(triggerWrapper);
			}
			viewData.addData("data", 
					ViewData.mkMap("jobDetail", jobDetail, "triggerWrappersList", triggerWrappersList));
		}
	};
	
	@Getter
	private ActionHandler triggerDetailHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			String triggerName = viewData.findData("triggerName");
			String triggerGroup = viewData.findData("triggerGroup");
			int fireTimesCount = Integer.parseInt(viewData.findData("fireTimesCount", "" + defaultFireTimesCount));
			SchedulerTemplate st = schedulerService.getScheduler();
			Trigger trigger = st.getTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));

			List<Tuple2<Date, String>> nextFireTimes = 
					QuartzUtils.getNextFireTimesWithExclusionDesc(st, trigger, fireTimesCount);
			String misfireInstructionName = QuartzUtils.getMisfireInstructionName(trigger);
			String triggerStatusName = st.getTriggerState(trigger.getKey()).toString();
			String jobClassName = st.getJobDetail(trigger.getJobKey()).getJobClass().getName();
			
			JobData.TriggerDetail triggerWrapper = new JobData.TriggerDetail(trigger);
			triggerWrapper.setListIndex(0);
			triggerWrapper.setTriggerStatusName(triggerStatusName);
			triggerWrapper.setNextFireTimes(nextFireTimes);
			triggerWrapper.setMisfireInstructionName(misfireInstructionName);
			triggerWrapper.setJobClassName(jobClassName);
			
			viewData.addData("data", 
					ViewData.mkMap("triggerWrapper", triggerWrapper, "fireTimesCount", fireTimesCount));
		}
	};
	
	@Getter
	private ActionHandler jobHistory = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			SessionData sessionData = viewData.findData(SessionData.SESSION_DATA_KEY);
			String configId = sessionData.getCurrentSchedulerConfigId();
			SchedulerService schedulerService = schedulerContainer.getSchedulerService(configId);
			
			// Verify plugin exists.
			// TODO: We should let this key be configurable!
			String key = JdbcSchedulerHistoryPlugin.DEFAULT_SCHEDULER_CONTEXT_KEY;			
			JdbcSchedulerHistoryPlugin plugin = 
					(JdbcSchedulerHistoryPlugin)schedulerService.getScheduler().getContext().get(key);
			if (plugin == null) {
				viewData.addData("data", ViewData.mkMap("jobHistoryPluginNotFound", true));
				return;
			}
			
			// Get the history table
			List<List<Object>> jobHistoryTable = plugin.getJobHistoryData();
			viewData.addData("data", ViewData.mkMap("jobHistoryTable", jobHistoryTable));
		}
	};
	
	@Getter
	private ActionHandler cronTool = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {			
			String validate = viewData.getRequest().getParameter("validate");
			String cronStr = viewData.getRequest().getParameter("cron");
			String afterTimeStr = viewData.getRequest().getParameter("afterTime");
			String fireTimesCountStr = viewData.getRequest().getParameter("fireTimesCount");
			int fireTimesCount = 20;
			Date afterTime = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			
			Map<String, Object> dataMap = ViewData.mkMap(
					"afterTimeStr", dateFormat.format(afterTime),
					"fireTimesCount", fireTimesCount,
					"cron", cronStr);
			viewData.addData("data", dataMap);

			if (validate == null) {
				dataMap.put("cron", "* * * * * ?");
				return;
			}
			if (fireTimesCountStr != null) {
				fireTimesCount = Integer.parseInt(fireTimesCountStr);
				dataMap.put("fireTimesCount", fireTimesCount);
			}
			if (afterTimeStr != null) {
				try {
					afterTime = dateFormat.parse(afterTimeStr);
					dataMap.put("afterTimeStr", dateFormat.format(afterTime));
				} catch (ParseException e) {
					dataMap.put("invalidAfterTime", true);
					dataMap.put("exceptionStr", e.getMessage());
					dataMap.put("afterTimeStr", afterTimeStr);
					return;
				}
			}
			try {
				CronExpression cronExp = new CronExpression(cronStr);
				List<Date> fireTimes = new ArrayList<Date>();
				for (int i = 0; i < fireTimesCount; i++) {
					afterTime = cronExp.getTimeAfter(afterTime);
					fireTimes.add(afterTime);
				}
				dataMap.put("cronExp", cronExp);
				dataMap.put("fireTimes", fireTimes);
			} catch (ParseException e) {
				dataMap.put("invalidCron", true);
				dataMap.put("exceptionStr", e.getMessage());
			}
		}
	};
	
	@Getter
	private ActionHandler schedulerDownHandler = new UrlRequestActionHandler();
	
	private List<String> getTriggerFullNames(List<MutableTrigger> triggers) {
		List<String> list = new ArrayList<String>();
		for (Trigger trigger : triggers)
			list.add(trigger.getKey().toString());
		return list;
	}

	private List<String> getJobDetailFullNames(List<JobDetail> jobDetails) {
		List<String> list = new ArrayList<String>();
		for (JobDetail jobDetail : jobDetails)
			list.add(jobDetail.getKey().toString());
		return list;
	}
}
