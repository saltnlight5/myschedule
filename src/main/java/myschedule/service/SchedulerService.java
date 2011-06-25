package myschedule.service;

import static myschedule.service.ErrorCode.SCHEDULER_PROBLEM;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide Quartz scheduling service to application. User should ensure init and destroy
 * are called as part of the application lifecycle.
 * 
 * <p>
 * This class needs either a configUrl that points to a Quartz config properties file, or
 * user set the quartz Scheduler instance directly. Either case, each instance 
 * should be auto registering itself into the SchedulerServiceRepository upon init().
 * 
 * @see ServiceContainer.
 * 
 * @author Zemian Deng
 */
public class SchedulerService implements Service {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected URL configUrl;
	
	protected Scheduler scheduler;
	
	protected boolean autoStart = true;

	/** Used during stop/shutdown of scheduler. */
	protected boolean waitForJobsToComplete = true;
	
	public void setConfigUrl(URL configUrl) {
		this.configUrl = configUrl;
	}
	
	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}
	
	public void setWaitForJobsToComplete(boolean waitForJobsToComplete) {
		this.waitForJobsToComplete = waitForJobsToComplete;
	}
	
	// Scheduler service methods
	// =========================
	public Scheduler getUnderlyingScheduler() {
		return scheduler;
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public String getName() {
		try {
			return scheduler.getSchedulerName();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	

	protected String getInstanceId() {
		try {
			return scheduler.getSchedulerInstanceId();
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
			String[] jobGroups = scheduler.getJobGroupNames();
			for (String jobGroup : jobGroups) {
				String[] jobNames = scheduler.getJobNames(jobGroup);
				for (String jobName : jobNames) {
					JobDetail jobDetail = scheduler.getJobDetail(jobName, jobGroup);
					jobs.add(jobDetail);
				}
			}
			return jobs;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	/** Get all the triggers in the scheduler. */
	public List<Trigger> getTriggers(JobDetail jobDetail) {
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobDetail.getName(), jobDetail.getGroup());
			return Arrays.asList(triggers);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public JobDetail getJobDetail(String jobName, String jobGroup) {	
		try {
			return scheduler.getJobDetail(jobName, jobGroup);
		} catch (Exception e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	public Trigger getTrigger(String triggerName, String triggerGroup) {	
		try {
			return scheduler.getTrigger(triggerName, triggerGroup);
		} catch (Exception e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	/**
	 * Get a list of next fire time dates up to maxCount time. If next fire time needed
	 * before maxCount, then there should be a null object in the last element of the list.
	 */
	public List<Date> getNextFireTimes(Trigger trigger, Date startTime, int maxCount) {	
		try {
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
			return list;
		} catch (Exception e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	/** Add a JobDetail with a trigger schedule when to fire. */
	public Date scheduleJob(JobDetail jobDetail, Trigger trigger) {
		try {
			Date nextFireTime = scheduler.scheduleJob(jobDetail, trigger);
			logger.info("Scheduled job=" + jobDetail.getFullName() + ", trigger=" + trigger.getFullName());
			return nextFireTime;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/** Schedule new trigger to an existing JobDetail. You need to set "trigger.setJobName()". */
	public void scheduleJob(Trigger trigger) {
		try {
			scheduler.scheduleJob(trigger);
			logger.info("Scheduled trigger=" + trigger);
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
			logger.info("Unscheduled job, triggerName=" + triggerName + ", triggerGroup=" + triggerGroup);
			return trigger;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/** Remove a JobDetail and all the triggers associated with it. */
	public List<Trigger> deleteJob(String jobName, String jobGroup) {
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobName, jobGroup);
			boolean success = scheduler.deleteJob(jobName, jobGroup);
			if (!success)
				throw new SchedulerException("Unable to delete jobName=" + jobName + ", jobGroup=" + jobGroup);
			logger.info("Deleted jobName: " + jobName + ", jobGroup" + jobGroup + ". Also removed associated " + 
					triggers.length + " triggers.");
			return Arrays.asList(triggers);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/**
	 * Load job scheduling data xml using XMLSchedulingDataProcessor.
	 * @param xml
	 * @return XMLSchedulingDataProcessor instance will contain all the jobs parse in xml.
	 */
	public XmlJobLoader loadJobs(String xml) {
		try {
			// XmlJobLoader is not only just a loader, but also use to store what's loaded!
			XmlJobLoader xmlJobLoader = XmlJobLoader.newInstance(); 
			String systemId = XmlJobLoader.XML_SYSTEM_ID;
			InputStream istream = new ByteArrayInputStream(xml.getBytes());
			xmlJobLoader.processStreamAndScheduleJobs(istream, systemId, scheduler);
			logger.info("Xml job data loaded. Loaded jobs size=" + xmlJobLoader.getLoadedJobs().size() + 
					" Loaded triggers size=" + xmlJobLoader.getLoadedTriggers().size());
			return xmlJobLoader;
		} catch (Exception e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public boolean isRemote() {
		try {
			return scheduler.getMetaData().isSchedulerRemote();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}		
	}
	
	public Properties getConfigProps() {
		try {
			InputStream inStream = configUrl.openStream();
			Properties props = new Properties();
			props.load(inStream);
			return props;
		} catch (IOException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public void pause() {
		try {
			if (!scheduler.isInStandbyMode()) {
				scheduler.standby();
				logger.info(scheduler.getSchedulerName() + " paused.");
			}
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public void resume() {
		try {
			if (scheduler.isInStandbyMode()) {
				scheduler.start();
				logger.info(scheduler.getSchedulerName() + " resumed.");
			}
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public void start() {
		try {
			if (!scheduler.isStarted() || scheduler.isInStandbyMode()) {
				scheduler.start();
				logger.info(scheduler.getSchedulerName() + " started.");
			}
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	public void stop() {
		try {
			if (!scheduler.isShutdown()) {
				scheduler.shutdown(waitForJobsToComplete);
				logger.info(scheduler.getSchedulerName() + " stopped with waitForJobsToComplete=" + waitForJobsToComplete);
			}
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	@Override
	public void init() {
		// Create and init the scheduler
		if (scheduler == null) {
			createAndInitScheduler();
		}
		
		// Register scheduler into repository
		SchedulerServiceRepository.getInstance().add(getName(), this);
		
		// Auto start a non-remote scheduler
		if (autoStart && !isRemote()) {
			start();
		}
	}

	@Override
	public void destroy() {
		if (scheduler != null) {
			String schedulerName = getName();
			
			// Auto stop a non-remote scheduler
			if (!isRemote()) {
				stop();
			}
			scheduler = null;

			// Unregister scheduler into repository
			SchedulerServiceRepository.getInstance().remove(schedulerName);
		}
	}

	protected void createAndInitScheduler() {
		if (configUrl == null) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, "SchedulerService is missing configUrl.");
		}
		
		Properties props = getConfigProps();
		try {
			StdSchedulerFactory schedulerFactory = new StdSchedulerFactory(props);
			scheduler = schedulerFactory.getScheduler();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}		
	}
}
