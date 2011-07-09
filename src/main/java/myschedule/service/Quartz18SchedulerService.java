package myschedule.service;

import static myschedule.service.ErrorCode.SCHEDULER_PROBLEM;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import myschedule.job.GroovyScriptJob;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide Quartz implementation of SchedulerService.
 * 
 * <p>
 * User may setup this class by providing either quartz Scheduler instance directly, or through #configProps. The
 * scheduler #name will only be set after init() is called, else it's null. Or user may try #getConfigSchedulerName()
 * that attempt to extract name from #configProps.
 * 
 * <p>If #configProps is used, then addition keys may be use to instruct DAO service on how to load
 * SchedulerServcie with the autoStart and waitForJobsToComplete set properly.
 * <pre>
 * 	myschedule.schedulerService.autoStart = true
 * 	myschedule.schedulerService.waitForJobsToComplete = true
 * </pre>
 * 
 * @see SchedulerServiceContainer, {@link SchedulerServiceDao}
 * 
 * @author Zemian Deng
 */
public class Quartz18SchedulerService implements SchedulerService {
	
	public static final String NAME_KEY = "org.quartz.scheduler.instanceName";
	public static final String AUTO_START_KEY = "myschedule.schedulerService.autoStart";
	public static final String WAIT_FOR_JOBS_KEY = "myschedule.schedulerService.waitForJobsToComplete";

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected AtomicBoolean paused = new AtomicBoolean(false);
		
	protected Scheduler scheduler;
	
	protected String name;
	
	protected boolean autoStart;
	
	protected boolean configModifiable = true; // default to true
	
	protected Properties configProps;
	
	/** Used during shutdown of scheduler. */
	protected boolean waitForJobsToComplete = true;
	
	@Override
	public String toString() {
		return "SchedulerService[" + name +  "]";
	}
	
	public void setConfigModifiable(boolean configModifiable) {
		this.configModifiable = configModifiable;
	}
	
	public boolean isConfigModifiable() {
		return configModifiable;
	}
	
	@Override
	public boolean isAutoStart() {
		return autoStart;
	}
	
	@Override
	public boolean isWaitForJobsToComplete() {
		return waitForJobsToComplete;
	}
	
	
	@Override
	public void setConfigProps(Properties configProps) {
		this.configProps = configProps;
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getConfigSchedulerName() {
		if (configProps == null)
			return null;
		return configProps.getProperty(NAME_KEY, "QuartzScheduler");
	}
	
	// Scheduler service methods
	// =========================
	@Override
	public Scheduler getUnderlyingScheduler() {
		return scheduler;
	}

	@Override
	public List<JobExecutionContext> getCurrentlyExecutingJobs() {
		List<JobExecutionContext> result = new ArrayList<JobExecutionContext>();
		try {
			List<?> jobs = scheduler.getCurrentlyExecutingJobs();
			for (Object job : jobs) {
				JobExecutionContext jobec = (JobExecutionContext)job;
				result.add(jobec);
			}
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
		
	@Override
	public SchedulerMetaData getSchedulerMetaData() {
		try {
			return scheduler.getMetaData();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	/** Get all the JobDetails in the scheduler. */
	@Override
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
	@Override
	public List<Trigger> getTriggers(JobDetail jobDetail) {
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobDetail.getName(), jobDetail.getGroup());
			return Arrays.asList(triggers);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	@Override
	public JobDetail getJobDetail(String jobName, String jobGroup) {	
		try {
			return scheduler.getJobDetail(jobName, jobGroup);
		} catch (Exception e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	@Override
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
	@Override
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
		return list;
	}
	
	/** Add a JobDetail with a trigger schedule when to fire. */
	@Override
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
	@Override
	public void scheduleJob(Trigger trigger) {
		try {
			scheduler.scheduleJob(trigger);
			logger.info("Scheduled trigger=" + trigger);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	/** Remove a trigger and its JobDetail if it's not set durable. */
	@Override
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
	@Override
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
	@Override
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
	
	public Properties getConfigProps() {
		return configProps;
	}
	
	@Override
	public void init() {
		if (configProps == null) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, "configProps is missing.");
		}
		try {
			// Init scheduler
			StdSchedulerFactory schedulerFactory = new StdSchedulerFactory(configProps);
			scheduler = schedulerFactory.getScheduler();
			
			// Init service
			name = scheduler.getSchedulerName();
			autoStart = Boolean.parseBoolean(configProps.getProperty(AUTO_START_KEY, "true"));
			waitForJobsToComplete = Boolean.parseBoolean(configProps.getProperty(WAIT_FOR_JOBS_KEY, "true"));
			logger.info("Scheduler service " + name + " has been initialized.");
			
			// Auto start if possible.
			if (autoStart && !isRemote()) {
				try {
					start();
					logger.info("Scheduler service " + name + " has auto started.");
				} catch (Exception e) {
					// Just log error, do not re-throw.
					logger.error("Failed to auto start scheduerl service " + name, e);
				}
			}
		} catch (SchedulerException e) {
			if (scheduler != null) {
				destroy();
			}
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
		
	@Override
	public void start() {
		try {
			scheduler.start();
			logger.info("Scheduler service " + name + " started.");
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	@Override
	public void standby() {
		try {
			scheduler.standby();
			logger.info("Scheduler service " + name + " standby.");
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	@Override
	public void pause() {
		try {
			scheduler.pauseAll();
			paused.set(true);
			logger.info("Scheduler service " + name + " standby.");
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	@Override
	public void resume() {
		try {
			scheduler.resumeAll();
			paused.set(false);
			logger.info("Scheduler service " + name + " standby.");
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	@Override
	public void shutdown() {
		try {
			scheduler.shutdown(waitForJobsToComplete);
			logger.info("Scheduler service " + name + " is shutdown. Called with waitForJobsToComplete=" + waitForJobsToComplete);
			scheduler = null;
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	@Override
	public void destroy() {
		// Try to auto shutdown if possible.
		if (isInit() && !isShutdown() && !isRemote()) {
			shutdown();
			logger.info("Scheduler service " + name + " has been auto shutdown.");
		}
	}
	
	@Override
	public boolean isPaused() {
		return paused.get();
	}
	
	@Override
	public boolean isInit() {
		return scheduler != null;
	}

	@Override
	public boolean isRemote() {
		try {
			return scheduler.getMetaData().isSchedulerRemote();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}		
	}
	
	@Override
	public boolean isShutdown() {
		try {
			return scheduler.isShutdown();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	@Override
	public boolean isStarted() {
		try {
			return scheduler.isStarted();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	@Override
	public boolean isStandby() {
		try {
			return scheduler.isInStandbyMode();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}
	
	// === Jobs creation helpers

	/** Run a job immediately with a non-volatile trigger (remove as soon as it's finished.) */
	@Override
	public void runJob(String jobName, String groupName) {
		try {
			scheduler.triggerJobWithVolatileTrigger(jobName, groupName);
		} catch (SchedulerException e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, e);
		}
	}

	@Override
	public void createGroovyScriptCronJob(String jobName, String cron, String script) {
		createCronJob(jobName, cron, GroovyScriptJob.class, 
				Utils.createMap(GroovyScriptJob.GROOVY_SCRIPT_TEXT_KEY, script));
	}

	@Override
	public void createCronJob(String jobName, String cron, Class<? extends Job> jobClass, Map<String, Object> data) {
		try {
			JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobClass);
			if (data != null)
				jobDetail.getJobDataMap().putAll(data);
			Trigger trigger = new CronTrigger(jobName, Scheduler.DEFAULT_GROUP, cron);
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (Exception e) {
			throw new ErrorCodeException(SCHEDULER_PROBLEM, "Failed to scheduler cron job: " + jobName, e);
		}
	}
}
