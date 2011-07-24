package myschedule.service.quartz;

import static myschedule.service.ErrorCode.SCHEDULER_PROBLEM;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import myschedule.service.ErrorCodeException;

import org.quartz.Calendar;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A template to simplify quartz Scheduler class. It provides some more convenient methods to schedule
 * jobs and convert Quartz's "checked" SchedulerException into an "unchecked" ErrorCodeException.
 *
 * @author Zemian Deng
 */
public class SchedulerTemplate {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected Scheduler scheduler;
	
	public SchedulerTemplate(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public List<JobExecutionContext> getCurrentlyExecutingJobs() {
		try {
			List<JobExecutionContext> result = new ArrayList<JobExecutionContext>();
			List<?> jobs = scheduler.getCurrentlyExecutingJobs();
			for (Object job : jobs) {
				JobExecutionContext jobec = (JobExecutionContext)job;
				result.add(jobec);
			}
			logger.debug("{} jobs found.", result.size());
			return result;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public SchedulerMetaData getSchedulerMetaData() {
		try {
			return scheduler.getMetaData();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/** Get all the JobDetails in the scheduler. */
	public List<JobDetail> getJobDetails() {
		try {
			List<JobDetail> jobs = new ArrayList<JobDetail>();
			String[]  groups = scheduler.getJobGroupNames();
			for (String group : groups) {
				String[] names = scheduler.getJobNames(group);
				for (String name : names) {
					JobDetail jobDetail = scheduler.getJobDetail(name, group);
					jobs.add(jobDetail);
				}
			}
			logger.debug("{} jobs found.", jobs.size());
			return jobs;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	/** Get all the triggers in the scheduler. */
	public List<Trigger> getTriggers(JobDetail jobDetail) {
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobDetail.getName(), jobDetail.getGroup());
			logger.debug("{} triggers found.", triggers.length);
			return Arrays.asList(triggers);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public JobDetail getJobDetail(String jobName, String group) {	
		try {
			return scheduler.getJobDetail(jobName, group);
		} catch (Exception e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	public Trigger getTrigger(String triggerName, String group) {	
		try {
			return scheduler.getTrigger(triggerName, group);
		} catch (Exception e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	public Calendar getCalendar(String calName) {	
		try {
			return scheduler.getCalendar(calName);
		} catch (Exception e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	/**
	 * Get a list of next fire time dates up to maxCount time. If next fire time needed
	 * before maxCount, then there should be a null object in the last element of the list.
	 */
	public List<Date> getNextFireTimes(Trigger trigger, Date startTime, int maxCount) {	
		List<Date> list = new ArrayList<Date>();
		Date nextDate = startTime;
		int count = 0;
		while(count++ < maxCount) {
			Date fireTime = trigger.getFireTimeAfter(nextDate);
			list.add(fireTime);
			if (fireTime == null)
				break;
			nextDate = fireTime;
		}
		logger.debug("{} dates generated.", list.size());
		return list;
	}
	
	/**
	 * Get a list of next fire time dates up to maxCount time. If next fire time needed
	 * before maxCount, then there should be a null object in the last element of the list.
	 */
	public List<Date> getNextFireTimesWithCalendar(Trigger trigger, Date startTime, int maxCount) {
		List<Date> dates = getNextFireTimes(trigger, startTime, maxCount);
		String calName = trigger.getCalendarName();
		if (calName == null) {
			logger.debug("There is no calendar associated with trigger {}", trigger.getFullName());
			return dates;
		}
		
		// Else check if dates has excluded by calendar or not.
		Calendar cal = getCalendar(calName);
		List<Date> result = new ArrayList<Date>();
		for (Date dt : dates) {
			if (cal.isTimeIncluded(dt.getTime())) {
				result.add(dt);
			} else {
				logger.debug("Trigger {} has calendar {} that excluded date: {}", 
						new Object[]{ trigger.getFullName(), calName, dt });
			}
		}
		logger.debug("{} dates generated after calendar processing.", result.size());
		return result;
	}
	
	/** Update existing job with newJobDetail and return the old one. */
	public JobDetail updateJobDetail(JobDetail newJobDetail) {
		try {
			JobDetail oldJob = scheduler.getJobDetail(newJobDetail.getName(), newJobDetail.getGroup());
			logger.debug("Found existing job {}", oldJob.getFullName());
			scheduler.addJob(newJobDetail, true);
			logger.debug("Job {} replaced.", newJobDetail.getFullName());
			return oldJob;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/** Update existing trigger with newTrigger and return the old one. */
	public Trigger updateTrigger(Trigger newTrigger) {
		try {
			Trigger oldTrigger = scheduler.getTrigger(newTrigger.getName(), newTrigger.getGroup());
			logger.debug("Found existing trigger {}", oldTrigger.getFullName());
			scheduler.rescheduleJob(oldTrigger.getName(), oldTrigger.getGroup(), newTrigger);
			logger.debug("Trigger {} replaced.", newTrigger.getFullName());
			return oldTrigger;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	/** Add a JobDetail with a trigger schedule when to fire. */
	public Date scheduleJob(JobDetail jobDetail, Trigger trigger) {
		try {
			Date nextFireTime = scheduler.scheduleJob(jobDetail, trigger);
			logger.info("New job {} scheduled with trigger {}.", jobDetail.getFullName(), trigger.getFullName());
			return nextFireTime;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/** Schedule new trigger to an existing JobDetail. You need to set "trigger.setJobName()". */
	public void scheduleJob(Trigger trigger) {
		try {
			scheduler.scheduleJob(trigger);
			logger.info("New trigger {} has been scheduled for existing job {}.", trigger.getFullName(), trigger.getFullJobName());
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	/** Remove a trigger and its JobDetail if it's not set durable. */
	public Trigger uncheduleJob(String triggerName, String triggerGroup) {
		try {
			Trigger trigger = scheduler.getTrigger(triggerName, triggerGroup);
			boolean success = scheduler.unscheduleJob(triggerName, triggerGroup);
			if (!success)
				throw new SchedulerException("Failed to unschedule job. Trigger name=" + triggerName + ", group=" + triggerGroup);
			logger.info("Unscheduled trigger {} for job {}.", trigger.getFullName(), trigger.getFullJobName());
			return trigger;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public void addJob(JobDetail job, boolean replace) {
		try {
			scheduler.addJob(job, replace);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
		
	}

	/** Remove a JobDetail and all the triggers associated with it. */
	public List<Trigger> deleteJob(String jobName, String group) {
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobName, group);
			boolean success = scheduler.deleteJob(jobName, group);
			if (!success)
				throw new SchedulerException("Unable to delete job " + jobName + "." + group);
			logger.info("Deleted job {}.{} with {} associated triggers.", new Object[]{jobName, group, triggers.length});
			return Arrays.asList(triggers);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/**
	 * Load job scheduling data xml using XMLSchedulingDataProcessor.
	 * @param xml - xml content of job_scheduling_data xml.
	 * @return XMLSchedulingDataProcessor instance will contain all the jobs parse in xml.
	 */
	public XmlJobLoader scheduleXmlSchedulingData(String xml) {
		try {
			// XmlJobLoader is not only just a loader, but also use to store what's loaded!
			logger.info("Loading jobs with xml scheduling data.");
			XmlJobLoader xmlJobLoader = XmlJobLoader.newInstance(); 
			String systemId = XmlJobLoader.XML_SYSTEM_ID;
			InputStream istream = new ByteArrayInputStream(xml.getBytes());
			xmlJobLoader.processStreamAndScheduleJobs(istream, systemId, scheduler);
			logger.info("Xml job data loaded. Loaded jobs size={} and Loaded triggers size={}.",
					xmlJobLoader.getLoadedJobs().size(), xmlJobLoader.getLoadedTriggers().size());
			return xmlJobLoader;
		} catch (Exception e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/** Run a job immediately with a non-volatile trigger (remove as soon as it's finished.) */
	public void runJobNow(String name, String group) {
		try {
			scheduler.triggerJob(name, group);
			logger.debug("Job {} has been triggered.", name + "." + group);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	public static JobDetail createJobDetail(String name, Class<? extends Job> jobClass) {
		return createJobDetail(name, null, true, jobClass, null);
	}
	
	public static JobDetail createJobDetail(String name, String group, boolean durable, Class<? extends Job> jobClass, Map<String, Object> data) {
		JobDetail jobDetail = new JobDetail(name, group, jobClass);
		jobDetail.setDurability(durable);
		if (data != null)
			jobDetail.getJobDataMap().putAll(data);
		return jobDetail;
	}	

	public static CronTrigger createCronTrigger(String name, String cron) {
		return createCronTrigger(name, null, cron, null, null);
	}
	
	public static CronTrigger createCronTrigger(String name, String group, String cron, Date startTime, Date endTime) {
		if (group == null)
			group = Scheduler.DEFAULT_GROUP;
		
		if (startTime == null)
			startTime = new Date();
		
		try {
			CronTrigger trigger = new CronTrigger(name, group);
			trigger.setCronExpression(cron);
			trigger.setStartTime(startTime);
			trigger.setEndTime(endTime);
			return trigger;
		} catch (ParseException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public static SimpleTrigger createSimpleTrigger(String name, String group, int repeatTotalCount, long repeatInterval, Date startTime, Date endTime) {
		if (group == null)
			group = Scheduler.DEFAULT_GROUP;
		
		if (startTime == null)
			startTime = new Date();
		
		// Quartz's SimpleTrigger's repeatCount is one less than repeatTotalCount, so we need to adjust.
		int repeatCount = repeatTotalCount - 1;
		if (repeatTotalCount < 0)
			repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
		
		SimpleTrigger trigger = new SimpleTrigger(name, group);
		trigger.setRepeatCount(repeatCount);
		trigger.setRepeatInterval(repeatInterval);
		trigger.setStartTime(startTime);
		trigger.setEndTime(endTime);
		return trigger;
	}
	
	public Date scheduleCronJob(
			String name, String group, String cron, 
			Date startTime, Date endTime,
			Class<? extends Job> jobClass, Map<String, Object> data) {
		JobDetail job = createJobDetail(name, group, false, jobClass, data);
		Trigger trigger = createCronTrigger(name, group, cron, startTime, endTime);
		return scheduleJob(job, trigger);
	}

	public Date scheduleRepeatForeverJob(String name, long repeatInterval, Class<? extends Job> jobClass) {
		return scheduleRepeatableJob(name, null, null, null, -1, repeatInterval, jobClass, null);
	}
	
	public Date scheduleRepeatableJob(String name, int repeatCount, long repeatInterval, Class<? extends Job> jobClass) {
		return scheduleRepeatableJob(name, null, null, null, repeatCount, repeatInterval, jobClass, null);
	}
	
	public Date scheduleRepeatableJob(
			String name, String group, Date startTime, Date endTime,
			int repeatTotalCount, long repeatInterval,
			Class<? extends Job> jobClass, Map<String, Object> data) {
		JobDetail job = createJobDetail(name, group, false, jobClass, data);
		Trigger trigger = createSimpleTrigger(name, group, repeatTotalCount, repeatInterval, startTime, endTime);
		return scheduleJob(job, trigger);
	}
	
	public Date scheduleOnetimeJob(String name, Class<? extends Job> jobClass) {
		return scheduleRepeatableJob(name, 1, 0, jobClass);
	}
	
	public Date scheduleOnetimeJob(String name, String group, Date startTime, Date endTime, 
			Class<? extends Job> jobClass, Map<String, Object> data) {
		return scheduleRepeatableJob(name, group, startTime, endTime, 1, 0, jobClass, data);
	}

}
