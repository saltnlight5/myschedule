package myschedule.service;

import static myschedule.service.ErrorCode.SCHEDULER_PROBLEM;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
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
public class SchedulerServiceImpl implements SchedulerService {
	
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
	
	public Properties getConfigProps() {
		return configProps;
	}

	@Override
	public Scheduler getUnderlyingScheduler() {
		return scheduler;
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
}
