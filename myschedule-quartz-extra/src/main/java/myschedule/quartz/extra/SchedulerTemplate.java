package myschedule.quartz.extra;

import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.quartz.Calendar;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;
import org.quartz.spi.MutableTrigger;
import org.quartz.spi.OperableTrigger;

/**
 * A template to simplify creation and scheduling of Quartz jobs. This class can be very handy as one stop shop for
 * simple needs. It also shields away Quartz's "checked" SchedulerException into an "unchecked" ErrorCodeException, so
 * you can be more productive.
 *
 * @author Zemian Deng
 */
public class SchedulerTemplate {

	//
	// # A internal quartz scheduler instance holder.
	//
	protected Scheduler scheduler;
	
	public SchedulerTemplate() {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException("Failed to obtain default scheduler.", e);
		}
	}
	
	public SchedulerTemplate(String quartzConfigFilename) {
		try {
			StdSchedulerFactory factory = new StdSchedulerFactory(quartzConfigFilename);
			scheduler = factory.getScheduler();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException("Failed to obtain default scheduler.", e);
		}
	}
	
	public SchedulerTemplate(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	//
	// # These are delegate methods from org.quartz.Scheduler interface
	//
	public void addCalendar(String calName, Calendar calendar, boolean replace, boolean updateTriggers) {
		try {
			scheduler.addCalendar(calName, calendar, replace, updateTriggers);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public boolean checkExists(JobKey jobKey) {
		try {
			return scheduler.checkExists(jobKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public boolean checkExists(TriggerKey triggerKey) {
		try {
			return scheduler.checkExists(triggerKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void clear() {
		try {
			scheduler.clear();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public Calendar getCalendar(String calendarName) {	
		try {
			return scheduler.getCalendar(calendarName);
		} catch (Exception e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public boolean deleteCalendar(String calendarName) {
		try {
			return scheduler.deleteCalendar(calendarName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public boolean deleteJob(JobKey jobKey) {
		try {
			return scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public boolean deleteJobs(List<JobKey> jobKeys) {
		try {
			return scheduler.deleteJobs(jobKeys);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public SchedulerContext getContext() {
		try {
			return scheduler.getContext();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public JobDetail getJobDetail(JobKey jobKey) {
		try {
			return scheduler.getJobDetail(jobKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public List<String> getJobGroupNames() {
		try {
			return scheduler.getJobGroupNames();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public Set<JobKey> getJobKeys(GroupMatcher<JobKey> groupMatcher) {
		try {
			return scheduler.getJobKeys(groupMatcher);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public SchedulerMetaData getMetaData() {
		try {
			return scheduler.getMetaData();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public Set<String> getPausedTriggerGroups() {
		try {
			return scheduler.getPausedTriggerGroups();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public Trigger getTrigger(TriggerKey triggerKey) {
		try {
			return scheduler.getTrigger(triggerKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public List<String> getTriggerGroupNames() {
		try {
			return scheduler.getTriggerGroupNames();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public Set<TriggerKey> getTriggerKeys(GroupMatcher<TriggerKey> groupMatcher) {
		try {
			return scheduler.getTriggerKeys(groupMatcher);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public TriggerState getTriggerState(TriggerKey triggerKey) {
		try {
			return scheduler.getTriggerState(triggerKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public List<? extends Trigger> getTriggersOfJob(JobKey jobKey) {
		try {
			return scheduler.getTriggersOfJob(jobKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public boolean interrupt(JobKey jobKey) {
		try {
			return scheduler.interrupt(jobKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public boolean interrupt(String fireInstanceId) {
		try {
			return scheduler.interrupt(fireInstanceId);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void pauseAll() {
		try {
			scheduler.pauseAll();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void pauseJob(JobKey jobKey) {
		try {
			scheduler.pauseJob(jobKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void pauseJobs(GroupMatcher<JobKey> groupMatcher) {
		try {
			scheduler.pauseJobs(groupMatcher);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void pauseTrigger(TriggerKey triggerKey) {
		try {
			scheduler.pauseTrigger(triggerKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void pauseTriggers(GroupMatcher<TriggerKey> groupMatcher) {
		try {
			scheduler.pauseTriggers(groupMatcher);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public Date rescheduleJob(TriggerKey triggerKey, Trigger trigger) {
		try {
			return scheduler.rescheduleJob(triggerKey, trigger);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void resumeAll() {
		try {
			scheduler.resumeAll();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void resumeJob(JobKey jobKey) {
		try {
			scheduler.resumeJob(jobKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void resumeJobs(GroupMatcher<JobKey> groupMatcher) {
		try {
			scheduler.resumeJobs(groupMatcher);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void resumeTrigger(TriggerKey triggerKey) {
		try {
			scheduler.resumeTrigger(triggerKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void resumeTriggers(GroupMatcher<TriggerKey> groupMatcher) {
		try {
			scheduler.resumeTriggers(groupMatcher);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void scheduleJobs(Map<JobDetail, List<Trigger>> triggersAndJobs, boolean replace) {
		try {
			scheduler.scheduleJobs(triggersAndJobs, replace);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void setJobFactory(JobFactory jobFactory) {
		try {
			scheduler.setJobFactory(jobFactory);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void shutdown() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void shutdown(boolean waitForJobToComplete) {
		try {
			scheduler.shutdown(waitForJobToComplete);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void standby() {
		try {
			scheduler.standby();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void start() {
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void startDelayed(int seconds) {
		try {
			scheduler.startDelayed(seconds);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void triggerJob(JobKey jobKey, JobDataMap jobDataMap) {
		try {
			scheduler.triggerJob(jobKey, jobDataMap);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void triggerJob(JobKey jobKey) {
		try {
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public boolean unscheduleJob(TriggerKey triggerKey) {
		try {
			return scheduler.unscheduleJob(triggerKey);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public boolean unscheduleJobs(List<TriggerKey> triggerKeys) {
		try {
			return scheduler.unscheduleJobs(triggerKeys);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public List<String> getCalendarNames() {
		try {
			return scheduler.getCalendarNames();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public boolean isShutdown() {
		try {
			return scheduler.isShutdown();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	
	public boolean isInStandbyMode() {
		try {
			return scheduler.isInStandbyMode();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public boolean isStarted() {
		try {
			return scheduler.isStarted();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public SchedulerMetaData getSchedulerMetaData() {
		try {
			return scheduler.getMetaData();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public Date scheduleJob(JobDetail jobDetail, Trigger trigger) {
		try {
			return  scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void scheduleJob(Trigger trigger) {
		try {
			scheduler.scheduleJob(trigger);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public String getSchedulerName() {
		try {
			return scheduler.getSchedulerName();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public String getSchedulerInstanceId() {
		try {
			return scheduler.getSchedulerInstanceId();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void resumeAllTriggers() {
		try {
			scheduler.resumeAll();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public ListenerManager getListenerManager() {
		try {
			return scheduler.getListenerManager();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public void addJob(JobDetail job, boolean replace) {
		try {
			scheduler.addJob(job, replace);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public List<JobExecutionContext> getCurrentlyExecutingJobs() {
		try {
			return scheduler.getCurrentlyExecutingJobs();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	//
	// # These are convenient methods to easy scheduling programming.
	//
	
	/**
	 * Start scheduler, wait for some times, then shutdown scheduler to wait for all jobs to be complete.
	 * 
	 * <p>Note that you if you pass in waitTimeInMillis=0, it will wait forever!
	 * 
	 * @param waitTimeInMillis - number of milliseconds to wait before shutdown.
	 */
	public void startAndShutdown(long waitTimeInMillis) {
		start();
		synchronized(this) {
			try {
				this.wait(waitTimeInMillis);
			} catch (InterruptedException e) {
				throw new QuartzRuntimeException("Failed to wait after scheduler started.", e);
			}
		}
		// true => Wait for job to complete before shutdown.
		shutdown(true);
	}
	
	/**
	 * Start the scheduler and put this template instance into wait state until notified or interrupted.
	 * 
	 * <p>Note this method will block main thread execution!
	 */
	public void startAndWait() {
		startAndWait(0);
	}
	
	/** 
	 * Start the scheduler with a delay time, and put this template instance into wait state until notified or interrupted.
	 * 
	 * <p>Note this method will block main thread execution!
	 * 
	 * @param startDelayInSeconds
	 */
	public void startAndWait(int startDelayInSeconds) {
		if (startDelayInSeconds <= 0) {
			start();
		} else {
			startDelayed(startDelayInSeconds);
		}
		synchronized(this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				throw new QuartzRuntimeException("Failed to wait after scheduler started.", e);
			}
		}
	}

	public List<JobDetail> getAllJobDetails() {
		try {
			List<JobDetail> jobs = new ArrayList<JobDetail>();
			List<String> groups = scheduler.getJobGroupNames();
			for (String group : groups) {
				Set<JobKey> keys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group));
				for (JobKey key : keys) {
					JobDetail jobDetail = scheduler.getJobDetail(key);
					jobs.add(jobDetail);
				}
			}
			return jobs;
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
		
	/**
	 * Get a list of next fire time dates up to maxCount time. If next fire time needed
	 * before maxCount, then there should be a null object in the last element of the list.
	 */
	public List<Date> getNextFireTimes(Trigger trigger, Date startTime, int maxCount) {
		// We will clone the original trigger so we may call its triggered() to get a proper fireTime.
		OperableTrigger clonedTrigger = (OperableTrigger)((OperableTrigger)trigger).clone();
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
			JobDetail oldJob = scheduler.getJobDetail(newJobDetail.getKey());
			scheduler.addJob(newJobDetail, true);
			return oldJob;
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	/** Update existing trigger with newTrigger and return the old one. */
	public Trigger updateTrigger(Trigger newTrigger) {
		try {
			Trigger oldTrigger = scheduler.getTrigger(newTrigger.getKey());
			scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
			return oldTrigger;
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	/** Remove a trigger and its JobDetail if it's not set durable. */
	public Trigger uncheduleJob(TriggerKey triggerKey) {
		try {
			Trigger trigger = scheduler.getTrigger(triggerKey);
			boolean success = scheduler.unscheduleJob(trigger.getKey());
			if (!success)
				throw new SchedulerException("Failed to unschedule job with trigger key " + triggerKey);
			return trigger;
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public List<Trigger> getPausedTriggers() {
		try {
			List<Trigger> result = new ArrayList<Trigger>();
			List<String> groups = scheduler.getTriggerGroupNames();
			for (String group : groups) {
				Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(group));
				for (TriggerKey key : triggerKeys) {
					if (scheduler.getTriggerState(key) == TriggerState.PAUSED) {
						result.add(scheduler.getTrigger(key));
					}
				}
			}
			return result;
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	/** Remove a JobDetail and all the triggers associated with it. */
	public List<? extends Trigger> deleteJobAndGetTriggers(JobKey key) {
		try {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(key);
			boolean success = scheduler.deleteJob(key);
			if (!success)
				throw new SchedulerException("Unable to delete job " + key);
			return triggers;
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	/**
	 * Load job scheduling data xml using XMLSchedulingDataProcessor.
	 * 
	 * @param inStream - input stream of content for job_scheduling_data xml.
	 * @return XMLSchedulingDataProcessor instance will contain all the jobs parsed from xml input.
	 */
	public XmlJobLoader scheduleXmlSchedulingData(InputStream inStream) {
		try {
			// XmlJobLoader is not only just a loader, but also use to store what's loaded!
			XmlJobLoader xmlJobLoader = XmlJobLoader.newInstance(); 
			String systemId = XmlJobLoader.XML_SYSTEM_ID;
			xmlJobLoader.processStreamAndScheduleJobs(inStream, systemId, scheduler);
			return xmlJobLoader;
		} catch (Exception e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public Date scheduleCronJob(String name, String cron, Class<? extends Job> jobClass) {
		return scheduleCronJob(JobKey.jobKey(name), cron, jobClass, null, new Date(), null);
	}
	
	public Date scheduleCronJob(String name, String cron, Class<? extends Job> jobClass, Map<String, Object> dataMap) {
		return scheduleCronJob(JobKey.jobKey(name), cron, jobClass, dataMap, new Date(), null);
	}
	
	public Date scheduleCronJob(
			JobKey jobKey, String cron, 
			Class<? extends Job> jobClass, Map<String, Object> dataMap, 
			Date startTime, Date endTime) {
		JobDetail job = createJobDetail(jobKey, jobClass, dataMap);
		TriggerKey triggerKey = TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup());
		Trigger trigger = createCronTrigger(triggerKey, cron, startTime, endTime);
		return scheduleJob(job, trigger);
	}

	public Date scheduleRepeatableJob(String name, int repeatTotalCount, long repeatInterval, Class<? extends Job> jobClass) {
		return scheduleRepeatableJob(name, -1, repeatInterval, jobClass, null);
	}
	
	public Date scheduleRepeatableJob(String name, int repeatTotalCount, long repeatInterval, Class<? extends Job> jobClass, Date startTime) {
		return scheduleRepeatableJob(JobKey.jobKey(name), startTime, null, repeatTotalCount, repeatInterval, jobClass, null);
	}
	
	public Date scheduleRepeatableJob(
			JobKey jobKey, Date startTime, Date endTime,
			int repeatTotalCount, long repeatInterval,
			Class<? extends Job> jobClass, Map<String, Object> dataMap) {
		JobDetail job = createJobDetail(jobKey, jobClass, dataMap);
		TriggerKey triggerKey = TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup());
		Trigger trigger = createSimpleTrigger(triggerKey, repeatTotalCount, repeatInterval, startTime, endTime);
		return scheduleJob(job, trigger);
	}
	
	public Date scheduleOnetimeJob(String name, Class<? extends Job> jobClass) {
		return scheduleRepeatableJob(name, 1, 1, jobClass, null);
	}
	
	public Date scheduleOnetimeJob(String name, Class<? extends Job> jobClass, Date startTime) {
		return scheduleRepeatableJob(name, 1, 1, jobClass, startTime);
	}
	
	public Date scheduleOnetimeJob(JobKey jobKey, Date startTime, Date endTime, 
			Class<? extends Job> jobClass, Map<String, Object> dataMap) {
		return scheduleRepeatableJob(jobKey, startTime, endTime, 1, 1, jobClass, dataMap);
	}
	

	public String getSchedulerNameAndId() {
		return getSchedulerName() + "_$_" + getSchedulerInstanceId();
	}
	
	@Override
	public String toString() {
		return "QuartzScheduler[" + getSchedulerNameAndId() + "]";
	}
	
	//
	// # Static methods to create triggers and jobs
	//
	
	public static JobDetail createJobDetail(String name, Class<? extends Job> jobClass) {
		return createJobDetail(JobKey.jobKey(name), jobClass, null);
	}
	
	public static JobDetail createJobDetail(JobKey jobKey, Class<? extends Job> jobClass, Map<String, Object> dataMap) {
		JobDetail jobDetail = newJob(jobClass).withIdentity(jobKey).build();
		if (dataMap != null)
			jobDetail.getJobDataMap().putAll(dataMap);
		return jobDetail;
	}
	
	public static MutableTrigger createCalendarIntervalTrigger(
			String name, int interval, IntervalUnit intervalUnit) {
		return createCalendarIntervalTrigger(name, interval, intervalUnit, new Date());
	}

	public static MutableTrigger createCalendarIntervalTrigger(
			String name, int interval, IntervalUnit intervalUnit, Date startTime) {
		return createCalendarIntervalTrigger(TriggerKey.triggerKey(name), interval, intervalUnit, startTime, null);
	}
	
	public static MutableTrigger createCalendarIntervalTrigger(
			TriggerKey triggerKey,
			int interval, IntervalUnit intervalUnit,
			Date startTime, Date endTime) {
		if (startTime == null)
			startTime = new Date();
		
		CalendarIntervalTrigger trigger = newTrigger()
				.withIdentity(triggerKey)
				.startAt(startTime).endAt(endTime)
				.withSchedule(calendarIntervalSchedule().withInterval(interval, intervalUnit))
				.build();
		return (MutableTrigger)trigger;
	}
	
	public static MutableTrigger createCronTrigger(String name, String cron) {
		return createCronTrigger(TriggerKey.triggerKey(name), cron, new Date(), null);
	}

	public static MutableTrigger createCronTrigger(String name, String cron, Date startTime) {
		return createCronTrigger(TriggerKey.triggerKey(name), cron, startTime, null);
	}
	
	public static MutableTrigger createCronTrigger(TriggerKey triggerKey, String cron, Date startTime, Date endTime) {
		if (startTime == null)
			startTime = new Date();
		
		try {
			CronTrigger trigger = newTrigger()
					.withIdentity(triggerKey)
					.startAt(startTime).endAt(endTime)
					.withSchedule(cronSchedule(cron))
					.build();
			return (MutableTrigger)trigger;
		} catch (ParseException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public static MutableTrigger createSimpleTrigger(String name) {
		return createSimpleTrigger(name, 1, 0);
	}
	
	public static MutableTrigger createSimpleTrigger(String name, int repeatTotalCount, int interval) {
		return createSimpleTrigger(name, repeatTotalCount, interval, new Date());
	}

	public static MutableTrigger createSimpleTrigger(String name, int repeatTotalCount, int interval, Date startTime) {
		return createSimpleTrigger(TriggerKey.triggerKey(name), repeatTotalCount, interval, startTime, null);
	}
	
	public static MutableTrigger createSimpleTrigger(TriggerKey triggerKey, int repeatTotalCount, long repeatInterval, Date startTime, Date endTime) {
		if (startTime == null)
			startTime = new Date();
		
		// Quartz's SimpleTrigger's repeatCount is one less than repeatTotalCount, so we need to adjust.
		int repeatCount = repeatTotalCount - 1;
		if (repeatTotalCount < 0)
			repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
		
		SimpleTrigger trigger = newTrigger()
			.withIdentity(triggerKey)
			.startAt(startTime).endAt(endTime)
			.withSchedule(
					simpleSchedule()
					.withRepeatCount(repeatCount)
					.withIntervalInMilliseconds(repeatInterval))
			.build();
		return (MutableTrigger)trigger;
	}
}
