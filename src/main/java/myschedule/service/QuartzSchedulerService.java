package myschedule.service;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.RemoteScheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide Quartz implementation of SchedulerService.
 * 
 * <p>
 * This is little more than just wrapper to Quartz scheduler. This is a MyScheduler service, so it will auto start/stop
 * as part of the web application by ServiceContainer. In these lifecycles, it will check for MySchedule configuration
 * settings to play nicely with Quartz scheduler, such as to auto start scheduler upon webapp start or not, or shutdown
 * only if it's non-remote scheduler etc.
 * 
 * @see ServiceContainer
 * 
 * @author Zemian Deng
 */
public class QuartzSchedulerService extends AbstractService implements SchedulerService<Scheduler> {

	public static final String AUTO_START_KEY = "myschedule.schedulerService.autoStart";
	public static final String WAIT_FOR_JOBS_TO_COMPLETES_KEY = "myschedule.schedulerService.waitForJobsToComplete";
	
	private static final Logger logger = LoggerFactory.getLogger(QuartzSchedulerService.class);
	
	protected Scheduler scheduler;
	protected SchedulerConfig schedulerConfig;
			
	public QuartzSchedulerService(SchedulerConfig schedulerConfig) {
		setSchedulerConfig(schedulerConfig);
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public void setSchedulerConfig(SchedulerConfig schedulerConfig) {
		this.schedulerConfig = schedulerConfig;
	}
	
	@Override
	public SchedulerConfig getSchedulerConfig() {
		return schedulerConfig;
	}
	
	@Override
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	@Override
	protected void startService() {
		startSchedulerNicely();
	}
	
	protected void startSchedulerNicely() {
		// Let's auto start the scheduler if user has configured so, and it's non-remote.
		String autoStartStr = schedulerConfig.getConfigProps().getProperty(AUTO_START_KEY);
		boolean autoStart = Boolean.parseBoolean(autoStartStr);
		if (autoStart) {
			if (scheduler instanceof RemoteScheduler) {
				logger.warn("MyScheduler application will not auto start an RemoteScheduler " +
						"because it affect remote server. Please start it manually.");
				return;
			}
			
			logger.debug("Auto starting scheduler {}.", toString());
			try {
				scheduler.start();
				logger.debug("Quartz scheduler {} has auto started.", toString());
			} catch (SchedulerException e) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, 
						"Failed to start Quartz scheduler " + toString() + " in standby mode.", e);
			}
		}
	}

	@Override
	protected void stopService() {
		// Do nothing.
	}
	
	@Override
	protected void initService() {
		if (scheduler == null && schedulerConfig != null) {
			// Quartz will auto init the scheduler when new instance is created.
			try {
				logger.debug("Initializing Quartz scheduler.");
				StdSchedulerFactory factory = new StdSchedulerFactory(schedulerConfig.getConfigProps());
				scheduler = factory.getScheduler();
				logger.info("Quartz scheduler {} initialized.", toString());
			} catch (SchedulerException e) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, 
						"Failed to initialize Quartz scheduler using configProps.", e);
			}
		}
	}
	
	public boolean isSchedulerInitialized() {
		// If quartz scheduler instance is created, then it is initialized.
		return scheduler != null;
	}
	
	protected void shutdownSchedulerNicely() {
		// Shutdown if non-remote scheduler only.
		if (scheduler != null) {
			if (scheduler instanceof RemoteScheduler) {
				logger.warn("MyScheduler application will not auto shutdown an RemoteScheduler " +
						"because it affect remote server. Please shutdown manually.");
				return;
			}
			
			String waitForJobsToCompleteStr = schedulerConfig.getConfigProps().getProperty(WAIT_FOR_JOBS_TO_COMPLETES_KEY);
			boolean waitForJobsToComplete = Boolean.parseBoolean(waitForJobsToCompleteStr);
			try {
				logger.debug("Shutting down quartz scheduler {} with waitForJobsToComplete={}", toString(), waitForJobsToComplete);
				scheduler.shutdown(waitForJobsToComplete);
				logger.info("Quartz scheduler {} has been shutdown.", toString());
			} catch (SchedulerException e) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, 
						"Failed to shutodwn quartz scheduler " + toString(), e);
			}
		}
		// Need to nullify the scheduler instance since it's useless after shutdown.
		scheduler = null;
	}

	@Override
	protected void destroyService() {
		shutdownSchedulerNicely();
	}
	
	@Override
	public String toString() {
		String desc = "null"; // uninitialized scheduler.
		if (schedulerConfig != null) 
			desc = schedulerConfig.getConfigId();
		else if (scheduler != null) {
			try {
				scheduler.getSchedulerName();
			} catch (SchedulerException e) {
				// unlikely error, but in this case, we just use the default toString of scheduler.
				desc = scheduler.toString();
			}
		}
		return "SchedulerService[" + desc + "]";
	}
}
