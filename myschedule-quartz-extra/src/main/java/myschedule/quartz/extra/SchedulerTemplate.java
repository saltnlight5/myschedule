package myschedule.quartz.extra;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;

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
			throw new QuartzRuntimeException("Failed to create scheduler using config file: " + 
					quartzConfigFilename, e);
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
	
	public void triggerJobWithVolatileTrigger(String jobName, String groupName, JobDataMap data) {
		try {
			scheduler.triggerJobWithVolatileTrigger(jobName, groupName, data);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public void triggerJobWithVolatileTrigger(String jobName, String groupName) {
		try {
			scheduler.triggerJobWithVolatileTrigger(jobName, groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public List<?> getSchedulerListeners() {
		try {
			return scheduler.getSchedulerListeners();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public Set<?> getJobListenerNames() {
		try {
			return scheduler.getJobListenerNames();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public Set<?> getTriggerListenerNames() {
		try {
			return scheduler.getTriggerListenerNames();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public JobListener getJobListener(String name) {
		try {
			return scheduler.getJobListener(name);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public TriggerListener getTriggerListener(String name) {
		try {
			return scheduler.getTriggerListener(name);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public List<?> getGlobalJobListeners() {
		try {
			return scheduler.getGlobalJobListeners();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
		
	}
	
	public List<?> getGlobalTriggerListeners() {
		try {
			return scheduler.getGlobalTriggerListeners();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
		
	}
	
	public boolean removeGlobalJobListener(String name) {
		try {
			return scheduler.removeGlobalJobListener(name);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public boolean removeGlobalTriggerListener(String name) {
		try {
			return scheduler.removeGlobalTriggerListener(name);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public boolean removeJobListener(String name) {
		try {
			return scheduler.removeJobListener(name);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public boolean removeTriggerListener(String name) {
		try {
			return scheduler.removeTriggerListener(name);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public TriggerListener getGlobalTriggerListener(String name) {
		try {
			return scheduler.getGlobalTriggerListener(name);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public JobListener getGlobalJobListener(String name) {
		try {
			return scheduler.getGlobalJobListener(name);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public String[] getTriggerNames(String groupName) {
		try {
			return scheduler.getTriggerNames(groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public void triggerJob(String jobName, String groupName, JobDataMap data) {
		try {
			scheduler.triggerJob(jobName, groupName, data);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public void addCalendar(String calName, Calendar calendar, boolean replace, boolean updateTriggers) {
		try {
			scheduler.addCalendar(calName, calendar, replace, updateTriggers);
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

	public boolean deleteJob(String jobName, String groupName) {
		try {
			return scheduler.deleteJob(jobName, groupName);
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

	public JobDetail getJobDetail(String jobName, String jobGroup) {
		try {
			return scheduler.getJobDetail(jobName, jobGroup);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public String[] getJobGroupNames() {
		try {
			return scheduler.getJobGroupNames();
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

	public Set<?> getPausedTriggerGroups() {
		try {
			return scheduler.getPausedTriggerGroups();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public Trigger getTrigger(String triggerName, String triggerGroup) {
		try {
			return scheduler.getTrigger(triggerName, triggerGroup);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public String[] getTriggerGroupNames() {
		try {
			return scheduler.getTriggerGroupNames();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public int getTriggerState(String triggerName, String triggerGroup) {
		try {
			return scheduler.getTriggerState(triggerName, triggerGroup);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public Trigger[] getTriggersOfJob(String jobName, String groupName) {
		try {
			return scheduler.getTriggersOfJob(jobName, groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public boolean interrupt(String jobName, String groupName) {
		try {
			return scheduler.interrupt(jobName, groupName);
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

	public void pauseJob(String jobName, String groupName) {
		try {
			scheduler.pauseJob(jobName, groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void pauseJobGroup(String groupName) {
		try {
			scheduler.pauseJobGroup(groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void pauseTrigger(String jobName, String groupName) {
		try {
			scheduler.pauseTrigger(jobName, groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void pauseTriggerGroup(String groupName) {
		try {
			scheduler.pauseTriggerGroup(groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public Date rescheduleJob(String triggerName, String groupName, Trigger newTrigger) {
		try {
			return scheduler.rescheduleJob(triggerName, groupName, newTrigger);
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

	public void resumeJob(String jobName, String groupName) {
		try {
			scheduler.resumeJob(jobName, groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void resumeJobGroup(String groupName) {
		try {
			scheduler.resumeJobGroup(groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void resumeTrigger(String triggerName, String groupName) {
		try {
			scheduler.resumeTrigger(triggerName, groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void resumeTriggerGroup(String groupName) {
		try {
			scheduler.resumeTriggerGroup(groupName);
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

	public void triggerJob(String jobName, String groupName){
		try {
			scheduler.triggerJob(jobName, groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public boolean unscheduleJob(String triggerName, String groupName) {
		try {
			return scheduler.unscheduleJob(triggerName, groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public String[] getCalendarNames() {
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
		
	public void addJob(JobDetail job, boolean replace) {
		try {
			scheduler.addJob(job, replace);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public List<?> getCurrentlyExecutingJobs() {
		try {
			return scheduler.getCurrentlyExecutingJobs();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public String[] getJobNames(String groupName) {
		try {
			return scheduler.getJobNames(groupName);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
		
	}
	
	public void addGlobalJobListener(JobListener listener) {
		try {
			scheduler.addGlobalJobListener(listener);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public void addGlobalTriggerListener(TriggerListener listener) {
		try {
			scheduler.addGlobalTriggerListener(listener);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public void addSchedulerListener(SchedulerListener schedulerListener) {
		try {
			scheduler.addSchedulerListener(schedulerListener);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public void removeSchedulerListener(SchedulerListener schedulerListener) {
		try {
			scheduler.removeSchedulerListener(schedulerListener);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public void addJobListener(JobListener listener) {
		try {
			scheduler.addJobListener(listener);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public void addTriggerListener(TriggerListener listener) {
		try {
			scheduler.addTriggerListener(listener);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
		
	// ======================================
	// Additional methods for easy scheduling
	// ======================================
	
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
	 * Start the scheduler with a delay time, and put this template instance into wait state until notified or 
	 * interrupted.
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
			String[] groups = scheduler.getJobGroupNames();
			for (String group : groups) {
				String[] names = scheduler.getJobNames(group);
				for (String name : names) {
					JobDetail jobDetail = scheduler.getJobDetail(name, group);
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
			throw new QuartzRuntimeException(e);
		}
	}

	/** Update existing trigger with newTrigger and return the old one. */
	public Trigger updateTrigger(Trigger newTrigger) {
		try {
			Trigger oldTrigger = scheduler.getTrigger(newTrigger.getName(), newTrigger.getGroup());
			scheduler.rescheduleJob(oldTrigger.getName(), oldTrigger.getGroup(), newTrigger);
			return oldTrigger;
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	/** Remove a trigger and its JobDetail if it's not set durable. */
	public Trigger uncheduleJob(String triggerName, String triggerGroup) {
		try {
			Trigger trigger = scheduler.getTrigger(triggerName, triggerGroup);
			boolean success = scheduler.unscheduleJob(triggerName, triggerGroup);
			if (!success)
				throw new SchedulerException("Failed to unschedule job with trigger " + 
						triggerName + "." + triggerGroup);
			return trigger;
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	public List<Trigger> getPausedTriggers() {
		try {
			List<Trigger> result = new ArrayList<Trigger>();
			String[] groups = scheduler.getTriggerGroupNames();
			for (String group : groups) {
				String[] names = scheduler.getTriggerNames(group);
				for (String name : names) {
					if (scheduler.getTriggerState(name, group) == Trigger.STATE_PAUSED) {
						result.add(scheduler.getTrigger(name, group));
					}
				}
			}
			return result;
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}

	/** Remove a JobDetail and all the triggers associated with it. */
	public List<? extends Trigger> deleteJobAndGetTriggers(String jobName, String jobGroup) {
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobName, jobGroup);
			boolean success = scheduler.deleteJob(jobName, jobGroup);
			if (!success)
				throw new SchedulerException("Unable to delete job " + jobName + "." + jobGroup);
			return Arrays.asList(triggers);
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
	public Date scheduleCronJob(String name, String cron, Class<? extends Job> jobClass) {
		return scheduleCronJob(name, Scheduler.DEFAULT_GROUP, cron, jobClass, null, new Date(), null);
	}
	
	public Date scheduleCronJob(String name, String cron, Class<? extends Job> jobClass, Map<String, Object> dataMap) {
		return scheduleCronJob(name, Scheduler.DEFAULT_GROUP, cron, jobClass, dataMap, new Date(), null);
	}

	
	public Date scheduleCronJob(
			String name, String cron, Class<? extends Job> jobClass, Map<String, Object> dataMap, Date startTime) {
		return scheduleCronJob(name, Scheduler.DEFAULT_GROUP, cron, jobClass, dataMap, startTime, null);
	}	
	
	public Date scheduleCronJob(
			String jobName, String jobGroup, String cron, 
			Class<? extends Job> jobClass, Map<String, Object> dataMap, 
			Date startTime, Date endTime) {
		JobDetail job = createJobDetail(jobName, jobGroup, jobClass, false, dataMap);
		Trigger trigger = createCronTrigger(jobName, jobGroup, cron, startTime, endTime);
		return scheduleJob(job, trigger);
	}

	public Date scheduleSimpleJob(
			String name, int repeatTotalCount, long repeatInterval, Class<? extends Job> jobClass) {
		return 
			scheduleSimpleJob(name, Scheduler.DEFAULT_GROUP, 
					repeatTotalCount, repeatInterval, jobClass, null, null, null);
	}
	
	public Date scheduleSimpleJob(
			String name, int repeatTotalCount, long repeatInterval, Class<? extends Job> jobClass,
			Map<String, Object> dataMap) {
		return 
			scheduleSimpleJob(name, Scheduler.DEFAULT_GROUP, 
					repeatTotalCount, repeatInterval, jobClass, dataMap, null, null);
	}
	
	public Date scheduleSimpleJob(
			String name, int repeatTotalCount, long repeatInterval, Class<? extends Job> jobClass, 
			Map<String, Object> dataMap, Date startTime) {
		return 
			scheduleSimpleJob(name, Scheduler.DEFAULT_GROUP, 
					repeatTotalCount, repeatInterval, jobClass, dataMap, startTime, null);
	}
	
	public Date scheduleSimpleJob(
			String jobName, String jobGroup, int repeatTotalCount, long repeatInterval,
			Class<? extends Job> jobClass, Map<String, Object> dataMap, Date startTime, Date endTime) {
		JobDetail job = createJobDetail(jobName, jobGroup, jobClass, false, dataMap);
		Trigger trigger = createSimpleTrigger(jobName, jobGroup, repeatTotalCount, repeatInterval, startTime, endTime);
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
		return createJobDetail(name, Scheduler.DEFAULT_GROUP, jobClass, false, null);
	}
	
	public static JobDetail createJobDetail(String jobName, String jobGroup, Class<? extends Job> jobClass, 
		boolean durable, Map<String, Object> dataMap) {
		JobDetail jobDetail = new JobDetail(jobName, jobGroup, jobClass); 
		jobDetail.setDurability(durable);
		if (dataMap != null)
			jobDetail.getJobDataMap().putAll(dataMap);
		return jobDetail;
	}
		
	public static CronTrigger createCronTrigger(String name, String cron) {
		return createCronTrigger(name, Scheduler.DEFAULT_GROUP, cron, new Date(), null);
	}

	public static CronTrigger createCronTrigger(String name, String cron, Date startTime) {
		return createCronTrigger(name, Scheduler.DEFAULT_GROUP, cron, startTime, null);
	}
	
	public static CronTrigger createCronTrigger(
			String triggerName, String triggerGroup, String cron, Date startTime, Date endTime) {
		if (startTime == null)
			startTime = new Date();
		
		try {
			CronTrigger trigger = new CronTrigger(triggerName, triggerGroup, cron);
			trigger.setStartTime(startTime);
			trigger.setEndTime(endTime);
			return trigger;
		} catch (ParseException e) {
			throw new QuartzRuntimeException(e);
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
	
	public static SimpleTrigger createSimpleTrigger(
			String triggerName, String triggerGroup, 
			int repeatTotalCount, long repeatInterval, Date startTime, Date endTime) {
		if (startTime == null)
			startTime = new Date();
		
		// Quartz's SimpleTrigger's repeatCount is one less than repeatTotalCount, so we need to adjust.
		int repeatCount = repeatTotalCount - 1;
		if (repeatTotalCount < 0)
			repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
		
		SimpleTrigger trigger = new SimpleTrigger(triggerName, triggerGroup);
		trigger.setStartTime(startTime);
		trigger.setEndTime(endTime);
		trigger.setRepeatCount(repeatCount);
		trigger.setRepeatInterval(repeatInterval);
		return trigger;
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
