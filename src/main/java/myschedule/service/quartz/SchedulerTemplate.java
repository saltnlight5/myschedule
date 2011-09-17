package myschedule.service.quartz;

import static myschedule.service.ErrorCode.SCHEDULER_PROBLEM;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myschedule.job.GroovyScriptJob;
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

/**
 * A template to simplify creation and scheduling of Quartz jobs. This class can be very handy as one stop shop for
 * simple needs. It also shields away Quartz's "checked" SchedulerException into an "unchecked" ErrorCodeException, so
 * you can be more productive.
 *
 * @author Zemian Deng
 */
public class SchedulerTemplate {
	
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

	public List<String> getCalendarNames() {
		try {
			String[] names = scheduler.getCalendarNames();
			return Arrays.asList(names);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	public boolean isShutdown() {
		try {
			return scheduler.isShutdown();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	
	public boolean isInStandbyMode() {
		try {
			return scheduler.isInStandbyMode();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public boolean isStarted() {
		try {
			return scheduler.isStarted();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public List<JobExecutionContext> getCurrentlyExecutingJobs() {
		try {
			List<JobExecutionContext> result = new ArrayList<JobExecutionContext>();
			List<?> jobs = scheduler.getCurrentlyExecutingJobs();
			for (Object job : jobs) {
				JobExecutionContext jobec = (JobExecutionContext)job;
				result.add(jobec);
			}
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
			String[] groups = scheduler.getJobGroupNames();
			for (String group : groups) {
				for (String name : scheduler.getJobNames(group)) {
					JobDetail jobDetail = scheduler.getJobDetail(name, group);
					jobs.add(jobDetail);
				}
			}
			return jobs;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	/** Get all the triggers in the scheduler. */
	public List<? extends Trigger> getTriggers(JobDetail jobDetail) {
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobDetail.getName(), jobDetail.getGroup());
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
		// We will clone the original trigger so we may call its triggered() to get a proper fireTime.
		Trigger clonedTrigger = (Trigger)trigger.clone();
		Calendar cal = null;
		
        if (clonedTrigger.getNextFireTime() == null) {
        	clonedTrigger.computeFirstFireTime(cal);
        }
        
		List<Date> list = new ArrayList<Date>();
		Date nextDate = startTime;
		int count = 0;
		while(count++ < maxCount) {
			nextDate = clonedTrigger.getFireTimeAfter(nextDate);
			if (nextDate == null)
				break;
			list.add(nextDate);
			clonedTrigger.triggered(cal);
		}
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
			return dates;
		}
		
		// Else check if dates has excluded by calendar or not.
		Calendar cal = getCalendar(calName);
		List<Date> result = new ArrayList<Date>();
		for (Date dt : dates) {
			if (cal.isTimeIncluded(dt.getTime())) {
				result.add(dt);
			}
		}
		return result;
	}
	
	/** Update existing job with newJobDetail and return the old one. */
	public JobDetail updateJobDetail(JobDetail newJobDetail) {
		try {
			JobDetail oldJob = scheduler.getJobDetail(newJobDetail.getName(), newJobDetail.getGroup());
			scheduler.addJob(newJobDetail, true);
			return oldJob;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/** Update existing trigger with newTrigger and return the old one. */
	public Trigger updateTrigger(Trigger newTrigger) {
		try {
			Trigger oldTrigger = scheduler.getTrigger(newTrigger.getName(), newTrigger.getGroup());
			scheduler.rescheduleJob(oldTrigger.getName(), oldTrigger.getGroup(), newTrigger);
			return oldTrigger;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	/** Add a JobDetail with a trigger schedule when to fire. */
	public Date scheduleJob(JobDetail jobDetail, Trigger trigger) {
		try {
			Date nextFireTime = scheduler.scheduleJob(jobDetail, trigger);
			return nextFireTime;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/** Schedule new trigger to an existing JobDetail. You need to set "trigger.setJobName()". */
	public void scheduleJob(Trigger trigger) {
		try {
			scheduler.scheduleJob(trigger);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	/** Remove a trigger and its JobDetail if it's not set durable. */
	public Trigger uncheduleJob(String triggerName, String triggerGroup) {
		try {
			Trigger trigger = scheduler.getTrigger(triggerName, triggerGroup);
			boolean success = scheduler.unscheduleJob(trigger.getName(), trigger.getGroup());
			if (!success)
				throw new SchedulerException("Failed to unschedule job. Trigger name=" + 
						triggerName + ", group=" + triggerGroup);
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
	public List<? extends Trigger> deleteJob(String jobName, String group) {
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobName, group);
			boolean success = scheduler.deleteJob(jobName, group);
			if (!success)
				throw new SchedulerException("Unable to delete job: " + jobName + ", group=" + group);
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
			XmlJobLoader xmlJobLoader = XmlJobLoader.newInstance(); 
			String systemId = XmlJobLoader.XML_SYSTEM_ID;
			InputStream istream = new ByteArrayInputStream(xml.getBytes());
			xmlJobLoader.processStreamAndScheduleJobs(istream, systemId, scheduler);
			return xmlJobLoader;
		} catch (Exception e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/** Run a job immediately with a non-volatile trigger (remove as soon as it's finished.) */
	public void runJobNow(String name, String group) {
		try {
			scheduler.triggerJob(name, group);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	public static JobDetail createGroovyFileJob(String name, String fileName) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(GroovyScriptJob.GROOVY_SCRIPT_FILE_KEY, fileName);
		return createJobDetail(name, GroovyScriptJob.class, data);
	}
	
	public static JobDetail createGroovyJob(String name, String groovyText) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(GroovyScriptJob.GROOVY_SCRIPT_TEXT_KEY, groovyText);
		return createJobDetail(name, GroovyScriptJob.class, data);
	}

	public static JobDetail createJobDetail(String name, Class<? extends Job> jobClass) {
		return createJobDetail(name, jobClass, null);
	}
	
	public static JobDetail createJobDetail(String name, Class<? extends Job> jobClass, Map<String, Object> data) {
		return createJobDetail(name, Scheduler.DEFAULT_GROUP, jobClass, data);
	}
	
	public static JobDetail createJobDetail(String name, String group, Class<? extends Job> jobClass, Map<String, Object> data) {
		JobDetail jobDetail = new JobDetail(name, group, jobClass);
		if (data != null)
			jobDetail.getJobDataMap().putAll(data);
		return jobDetail;
	}
		
	public static CronTrigger createCronTrigger(String name, String cron) {
		return createCronTrigger(name, null, cron, new Date(), null);
	}

	public static CronTrigger createCronTrigger(String name, String cron, Date startTime) {
		return createCronTrigger(name, null, cron, startTime, null);
	}
	
	public static CronTrigger createCronTrigger(String name, String group, String cron, Date startTime, Date endTime) {
		if (group == null)
			group = Scheduler.DEFAULT_GROUP;
		
		if (startTime == null)
			startTime = new Date();
		
		try {
			CronTrigger trigger = new CronTrigger(name, group, cron);
			trigger.setStartTime(startTime);
			trigger.setEndTime(endTime);
			return trigger;
		} catch (ParseException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public static SimpleTrigger createSimpleTrigger(String name) {
		return createSimpleTrigger(name, 1, 0);
	}
	
	public static SimpleTrigger createSimpleTrigger(String name, int repeatTotalCount, int interval) {
		return createSimpleTrigger(name, repeatTotalCount, interval, new Date());
	}

	public static SimpleTrigger createSimpleTrigger(String name, int repeatTotalCount, int interval, Date startTime) {
		return createSimpleTrigger(name, Scheduler.DEFAULT_GROUP, repeatTotalCount, interval, startTime, null);
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
		
		SimpleTrigger trigger = new SimpleTrigger(name, group, startTime, endTime, repeatCount, repeatInterval); 
		return trigger;
	}
	
	public Date scheduleCronJob(String name, String cron, Class<? extends Job> jobClass) {
		return scheduleCronJob(name, cron, jobClass, new Date());
	}
	
	public Date scheduleCronJob(String name, String cron, Class<? extends Job> jobClass, Date startTime) {
		return scheduleCronJob(name, Scheduler.DEFAULT_GROUP, cron, jobClass, null, startTime, null);
	}
	
	public Date scheduleCronJob(
			String name, String group, String cron, 
			Class<? extends Job> jobClass, Map<String, Object> data, 
			Date startTime, Date endTime) {
		JobDetail job = createJobDetail(name, group, jobClass, data);
		Trigger trigger = createCronTrigger(name, group, cron, startTime, endTime);
		return scheduleJob(job, trigger);
	}

	public Date scheduleRepeatableJob(String name, int repeatTotalCount, long repeatInterval, Class<? extends Job> jobClass) {
		return scheduleRepeatableJob(name, -1, repeatInterval, jobClass, null);
	}
	
	public Date scheduleRepeatableJob(String name, int repeatTotalCount, long repeatInterval, Class<? extends Job> jobClass, Date startTime) {
		return scheduleRepeatableJob(name, Scheduler.DEFAULT_GROUP, startTime, null, repeatTotalCount, repeatInterval, jobClass, null);
	}
	
	public Date scheduleRepeatableJob(
			String name, String group, Date startTime, Date endTime,
			int repeatTotalCount, long repeatInterval,
			Class<? extends Job> jobClass, Map<String, Object> data) {
		JobDetail job = createJobDetail(name, group, jobClass, data);
		Trigger trigger = createSimpleTrigger(name, group, repeatTotalCount, repeatInterval, startTime, endTime);
		return scheduleJob(job, trigger);
	}
	
	public Date scheduleOnetimeJob(String name, Class<? extends Job> jobClass) {
		return scheduleRepeatableJob(name, 1, 0, jobClass);
	}
	
	public Date scheduleOnetimeJob(String name, Class<? extends Job> jobClass, Date startTime) {
		return scheduleRepeatableJob(name, 1, 0, jobClass, startTime);
	}
	
	public Date scheduleOnetimeJob(String name, String group, Date startTime, Date endTime, 
			Class<? extends Job> jobClass, Map<String, Object> data) {
		return scheduleRepeatableJob(name, group, startTime, endTime, 1, 0, jobClass, data);
	}
	
	public Date scheduleGroovyCronJob(String name, String cron, Date startTime, String groovyText) {
		JobDetail job = createGroovyJob(name, groovyText);
		Trigger trigger = createCronTrigger(name, cron, startTime);
		return scheduleJob(job, trigger);
	}
	
	public Date scheduleGroovyFileCronJob(String name, String cron, Date startTime, String fileName) {
		JobDetail job = createGroovyFileJob(name, fileName);
		Trigger trigger = createCronTrigger(name, cron, startTime);
		return scheduleJob(job, trigger);
	}

	public String getSchedulerName() {
		try {
			return scheduler.getSchedulerName();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public String getSchedulerInstanceId() {
		try {
			return scheduler.getSchedulerInstanceId();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	public String getSchedulerNameAndId() {
		return getSchedulerName() + "_$_" + getSchedulerInstanceId();
	}
	
	@Override
	public String toString() {
		return "QuartzScheduler[" + getSchedulerNameAndId() + "]";
	}

	public void pauseAllTriggers() {
		try {
			scheduler.pauseAll();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	public void resumeAllTriggers() {
		try {
			scheduler.resumeAll();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public List<Trigger> getPausedTriggers() {
		try {
			List<Trigger> result = new ArrayList<Trigger>();
			String[] groups = scheduler.getTriggerGroupNames();
			for (String group : groups) {
				for (String name : scheduler.getTriggerNames(group)) {
					if (scheduler.getTriggerState(name, group) == Trigger.STATE_PAUSED) {
						result.add(scheduler.getTrigger(name, group));
					}
				}
			}
			return result;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	public int getTriggerState(String name, String group) {
		try {
			return scheduler.getTriggerState(name, group);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
}
