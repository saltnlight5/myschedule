package myschedule.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide Quartz scheduling service to application.
 * 
 * <p>Typically the {@link #scheduler} should be auto started and shutdown, but user may use 
 * system property flag -Dmyschedule.quartz.autoStartAndShutdown=false to turn this off.
 * (It's set by Spring's xml config file.)
 * 
 * @author Zemian Deng
 */
public class SchedulerService implements Service {

	private static Logger logger = LoggerFactory.getLogger(SchedulerService.class);
	
	protected Scheduler scheduler;
	
	protected boolean autoStart = true;

	private boolean waitForJobsToComplete = true;
	
	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}
	
	public void setWaitForJobsToComplete(boolean waitForJobsToComplete) {
		this.waitForJobsToComplete = waitForJobsToComplete;
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public Date scheduleJob(JobDetail jobDetail, Trigger trigger) {
		try {
			Date nextFireTime = scheduler.scheduleJob(jobDetail, trigger);
			logger.info("Scheduled job=" + jobDetail.getFullName() + ", trigger=" + trigger.getFullName());
			return nextFireTime;
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Schedule new trigger to existing job.
	 */
	public void scheduleJob(Trigger trigger) {
		try {
			scheduler.scheduleJob(trigger);
			logger.info("Scheduled trigger=" + trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	public SchedulerMetaData getSchedulerMetaData() {
		try {
			return scheduler.getMetaData();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
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
			throw new RuntimeException(e);
		}
	}
	
	public List<Trigger> getTriggers(JobDetail jobDetail) {
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobDetail.getName(), jobDetail.getGroup());
			return Arrays.asList(triggers);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JobDetail getJobDetail(String jobName, String jobGroup) {	
		try {
			return scheduler.getJobDetail(jobName, jobGroup);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Trigger getTrigger(String triggerName, String triggerGroup) {	
		try {
			return scheduler.getTrigger(triggerName, triggerGroup);
		} catch (Exception e) {
			throw new RuntimeException(e);
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
			throw new RuntimeException(e);
		}
	}

	/**
	 * Delete trigger and the job associated with it.
	 * @param name
	 * @param group
	 */
	public Trigger uncheduleJob(String triggerName, String triggerGroup) {
		try {
			Trigger trigger = scheduler.getTrigger(triggerName, triggerGroup);
			boolean success = scheduler.unscheduleJob(triggerName, triggerGroup);
			if (!success)
				throw new SchedulerException("Failed to unschedule job. Trigger name=" + triggerName + ", group=" + triggerGroup);
			logger.info("Unscheduled job, triggerName=" + triggerName + ", triggerGroup=" + triggerGroup);
			return trigger;
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
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
			throw new RuntimeException(e);
		}
	}
	
	public void standbyScheduler() {
		try {
			scheduler.standby();
			logger.info(scheduler.getSchedulerName() + " standby.");
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void startScheduler() {
		try {
			scheduler.start();
			logger.info(scheduler.getSchedulerName() + " started.");
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param jobDetail
	 * @return
	 */
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
			throw new RuntimeException(e);
		}
	}	

	protected String getSchedulerFullName() {
		try {
			return scheduler.getSchedulerName() + ":" + scheduler.getSchedulerInstanceId();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}	

	// Service lifecycle impl.
	// ====================================
	
	/** This is not the scheudler's init stage, but the web application's init, but we are
	 * controlling the scheduler in here. */
	@Override
	public void init() {
		if (autoStart) {
			try {
				boolean remote = scheduler.getMetaData().isSchedulerRemote();
				if (!remote) {
					scheduler.start();
					logger.info("Scheduler " + getSchedulerFullName() + " has started.");					
				}
			} catch (SchedulerException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/** This is not the scheduler's destroy/shutdown stage, but the web application destroy, but we
	 * are controlling the scheduler in here. */ 
	@Override
	public void destroy() {
		try {
			boolean remote = scheduler.getMetaData().isSchedulerRemote();
			if (!remote) {
				scheduler.shutdown(waitForJobsToComplete);
				logger.info("Scheduler " + getSchedulerFullName() + " has shutdown with waitForJobsToComplete=" + waitForJobsToComplete);
			}
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
}
