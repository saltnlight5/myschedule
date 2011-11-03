package myschedule.quartz.extra;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.quartz.Calendar;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;
import org.quartz.spi.MutableTrigger;
import org.quartz.spi.OperableTrigger;

/**
 * A template to simplify creation and scheduling of Quartz jobs. This class can be very handy as one stop shop for
 * simple scheduling needs, and yet you still have full power of the original org.quartz.Scheduler API equivalent 
 * available. 
 * 
 * <p>This class also shields away Quartz's "checked" SchedulerException into an "unchecked" QuartzRuntimeException, 
 * so you can be more productive.
 *
 * @author Zemian Deng
 */
public class SchedulerTemplate {

	private Scheduler scheduler;
	
	public SchedulerTemplate() {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException("Failed to create scheduler.", e);
		}
	}
	
	public SchedulerTemplate(String quartzConfigFilename) {
		try {
			StdSchedulerFactory factory = new StdSchedulerFactory(quartzConfigFilename);
			scheduler = factory.getScheduler();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException("Failed to create scheduler using config file: " + quartzConfigFilename, e);
		}
	}
	
	public SchedulerTemplate(Properties props) {
		try {
			StdSchedulerFactory factory = new StdSchedulerFactory(props);
			scheduler = factory.getScheduler();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException("Failed to create scheduler using custom properties.", e);
		}
	}
	
	public SchedulerTemplate(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	

	// ==============================================================
	// Wrap The org.quartz.Scheduler methods with "uncheck" exception
	// ==============================================================
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
	
	
	// ======================================
	// Additional methods for easy scheduling
	// ======================================
	
	@SuppressWarnings("unchecked")
	public void addListener(JobListener listener) {
		try {
			scheduler.getListenerManager().addJobListener(listener);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addListener(TriggerListener listener) {
		try {
			scheduler.getListenerManager().addTriggerListener(listener);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public void addListener(SchedulerListener listener) {
		try {
			scheduler.getListenerManager().addSchedulerListener(listener);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	/**
	 * Start scheduler, wait for some times, then shutdown scheduler to wait for all jobs to be complete.
	 * 
	 * @param waitTimeInMillis - number of milliseconds to wait before shutdown.
	 */
	public void startAndShutdown(long waitTimeInMillis) {
		start();
		if (waitTimeInMillis > 0) {
			synchronized(this) {
				try {
					this.wait(waitTimeInMillis);
				} catch (InterruptedException e) {
					throw new QuartzRuntimeException("Failed to wait after scheduler started.", e);
				}
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
	
	public Date scheduleCronJob(String name, String cron, Class<? extends Job> jobClass) {
		return scheduleCronJob(JobKey.jobKey(name), cron, jobClass, null, new Date(), null);
	}
	
	public Date scheduleCronJob(String name, String cron, Class<? extends Job> jobClass, Map<String, Object> dataMap) {
		return scheduleCronJob(JobKey.jobKey(name), cron, jobClass, dataMap, new Date(), null);
	}

	
	public Date scheduleCronJob(String name, String cron, Class<? extends Job> jobClass, Map<String, Object> dataMap, Date startTime) {
		return scheduleCronJob(JobKey.jobKey(name), cron, jobClass, dataMap, startTime, null);
	}	
	
	public Date scheduleCronJob(
			JobKey jobKey, String cron, 
			Class<? extends Job> jobClass, Map<String, Object> dataMap, 
			Date startTime, Date endTime) {
		JobDetail job = createJobDetail(jobKey, jobClass, false, dataMap);
		TriggerKey triggerKey = TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup());
		Trigger trigger = createCronTrigger(triggerKey, cron, startTime, endTime);
		return scheduleJob(job, trigger);
	}

	public Date scheduleSimpleJob(
			String name, int repeatTotalCount, long repeatInterval, Class<? extends Job> jobClass) {
		return 
			scheduleSimpleJob(JobKey.jobKey(name), repeatTotalCount, repeatInterval, jobClass, null, null, null);
	}
	
	public Date scheduleSimpleJob(
			String name, int repeatTotalCount, long repeatInterval, Class<? extends Job> jobClass,
			Map<String, Object> dataMap) {
		return 
			scheduleSimpleJob(JobKey.jobKey(name), repeatTotalCount, repeatInterval, jobClass, dataMap, null, null);
	}
	
	public Date scheduleSimpleJob(
			String name, int repeatTotalCount, long repeatInterval, Class<? extends Job> jobClass, 
			Map<String, Object> dataMap, Date startTime) {
		return 
			scheduleSimpleJob(JobKey.jobKey(name), repeatTotalCount, repeatInterval, jobClass, dataMap, startTime, null);
	}
	
	public Date scheduleSimpleJob(
			JobKey jobKey, int repeatTotalCount, long repeatInterval,
			Class<? extends Job> jobClass, Map<String, Object> dataMap, Date startTime, Date endTime) {
		JobDetail job = createJobDetail(jobKey, jobClass, false, dataMap);
		TriggerKey triggerKey = TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup());
		Trigger trigger = createSimpleTrigger(triggerKey, repeatTotalCount, repeatInterval, startTime, endTime);
		return scheduleJob(job, trigger);
	}

	/** This method is fail-safe (eg: good for toString and logging use.) that it should not throw any exception in 
	 * getting the scheduler name. If name is not available (eg: a remote scheduler conn is broken), then simply 
	 * return the underlying scheduler class name and identity hash code. */
	public String getSchedulerNameAndId() {
		String result = null;
		try {
			result = getSchedulerName() + "_$_" + getSchedulerInstanceId();
		} catch (RuntimeException e) {
			// Ignore exception, and use default toString.
			result = getScheduler().getClass().getSimpleName() + "@" + System.identityHashCode(getScheduler());
		}
		return result;
	}
	
	//
	// # Static methods to create triggers and jobs
	//
	
	public static JobDetail createJobDetail(String name, Class<? extends Job> jobClass) {
		return createJobDetail(JobKey.jobKey(name), jobClass, false, null);
	}
	
	public static JobDetail createJobDetail(JobKey jobKey, Class<? extends Job> jobClass, 
		boolean durable, Map<String, Object> dataMap) {
		JobDetail jobDetail = newJob(jobClass).withIdentity(jobKey).storeDurably(durable).build();
		if (dataMap != null)
			jobDetail.getJobDataMap().putAll(dataMap);
		return jobDetail;
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
	
	public static MutableTrigger createSimpleTrigger(
			TriggerKey triggerKey, int repeatTotalCount, long repeatInterval, Date startTime, Date endTime) {
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
	
	public static Map<String, Object> mkMap(Object ... dataArray) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (dataArray.length % 2 != 0) {
			throw new IllegalArgumentException("Data must come in pair: key and value.");
		}
		
		for (int i = 0; i < dataArray.length; i++) {
			Object keyObj = dataArray[i];
			if (!(keyObj instanceof String)) {
				throw new IllegalArgumentException("Key must be a String type, but got: " + keyObj.getClass());
			}
			String key = (String)keyObj;
			Object value = dataArray[++i];
			map.put(key, value);
		}
		return map;
	}

	@Override
	public String toString() {
		return "QuartzScheduler[" + getSchedulerNameAndId() + "]";
	}
}
