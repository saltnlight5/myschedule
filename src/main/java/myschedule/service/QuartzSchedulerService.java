package myschedule.service;

import myschedule.service.quartz.SchedulerTemplate;

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
		this.schedulerConfig = schedulerConfig;
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
		autoStartScheduler();
	}
	
	protected void autoStartScheduler() {
		// Let's auto start the scheduler if user has configured so.
		String autoStartStr = schedulerConfig.getConfigProps().getProperty(AUTO_START_KEY);
		boolean autoStart = Boolean.parseBoolean(autoStartStr);
		if (autoStart) {
			logger.debug("Auto starting scheduler {}.", getSchedulerNameAndId());
			try {
				scheduler.start();
				logger.debug("Quartz scheduler {} has auto started.", getSchedulerNameAndId());
			} catch (SchedulerException e) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, 
						"Failed to start Quartz scheduler " + getSchedulerNameAndId() + " in standby mode.", e);
			}
		}
	}

	@Override
	protected void stopService() {
		// Quartz way of stopping the scheduler.
		try {
			scheduler.standby();
		} catch (SchedulerException e) {
			throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, 
					"Failed to put Quartz scheduler " + getSchedulerNameAndId() + " in standby mode.", e);
		}
	}
	
	@Override
	protected void initService() {
		if (scheduler == null && schedulerConfig != null) {
			// Quartz will auto init the scheduler when new instance is created.
			try {
				logger.debug("Initializing Quartz scheduler.");
				StdSchedulerFactory factory = new StdSchedulerFactory(schedulerConfig.getConfigProps());
				scheduler = factory.getScheduler();
				logger.info("Quartz scheduler {} initialized.", getSchedulerNameAndId());
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
		if (scheduler != null && !(scheduler instanceof RemoteScheduler)) {
			String waitForJobsToCompleteStr = schedulerConfig.getConfigProps().getProperty(WAIT_FOR_JOBS_TO_COMPLETES_KEY);
			boolean waitForJobsToComplete = Boolean.parseBoolean(waitForJobsToCompleteStr);
			try {
				logger.debug("Shutting down quartz scheduler {} with waitForJobsToComplete={}", getSchedulerNameAndId(), waitForJobsToComplete);
				scheduler.shutdown(waitForJobsToComplete);
				logger.info("Quartz scheduler {} has been shutdown.", getSchedulerNameAndId());
			} catch (SchedulerException e) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, 
						"Failed to shutodwn quartz scheduler " + getSchedulerNameAndId(), e);
			}
		}
		// Need to nullify the scheduler instance since it's useless after shutdown.
		scheduler = null;
	}

	protected String getSchedulerNameAndId() {
		SchedulerTemplate st = new SchedulerTemplate(scheduler);
		return st.getSchedulerNameAndId();
	}

	@Override
	protected void destroyService() {
		shutdownSchedulerNicely();
	}

	@Override
	public String getServiceName() {
		String name = null;
		if (schedulerConfig == null)
			name = schedulerConfig.getConfigId();
		else if (scheduler != null) {
			try {
				name = scheduler.getSchedulerName();
			} catch (SchedulerException e) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, "Failed to get scheduler name.", e);
			}
		}
		
		if (name == null) {
			throw new ErrorCodeException(ErrorCode.SERVICE_PROBLEM, 
					"Unable to determine scheduler service name. Please intialize SchedulerConfig#configId.");	
		}
		return name;
	}
}
