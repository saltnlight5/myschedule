package myschedule.service;

import java.util.Properties;
import myschedule.quartz.extra.QuartzRuntimeException;
import myschedule.quartz.extra.SchedulerTemplate;
import org.quartz.impl.RemoteScheduler;

/**
 * A service that wrap and extend quartz scheduler using SchedulerTemplate. This service will associate a configuration 
 * to control how to init, start and shutdown the scheduler.
 * 
 * <p>
 * Unlike the direct Quartz Scheduler, this service will lazy load the scheduler, but yet still able to maintain the 
 * configuration after a shutdown.
 * 
 * <p>
 * In addition to load Quartz config properties, this service will load and use these additional properties
 * to control how to manage the scheduler. Below are it's default values if omitted.
 * <pre>
 * myschedule.schedulerService.autoInit = true
 * myschedule.schedulerService.autoStart = false
 * myschedule.schedulerService.preventAutoStartRemoteScheduler = false
 * myschedule.schedulerService.waitForJobsToComplete = false
 * </pre>
 *  
 * @author Zemian Deng
 */
public class SchedulerService extends AbstractService {

	public static final String AUTO_INIT_KEY = "myschedule.schedulerService.autoInit";
	public static final String AUTO_START_KEY = "myschedule.schedulerService.autoStart";
	public static final String PREVENT_AUTO_START_REMOTE_SCHEDULER_KEY = "myschedule.schedulerService.preventAutoStartRemoteScheduler";
	public static final String WAIT_FOR_JOBS_TO_COMPLETES_KEY = "myschedule.schedulerService.waitForJobsToComplete";

	private String configId;
	private ConfigStore configStore;
	
	// Fields to control scheduler
	private boolean autoInit; // default to true
	private boolean autoStart; // default to false
	private boolean preventAutoStartRemoteScheduler; // default to false
	private boolean waitForJobsToComplete; // default to false
	private SchedulerTemplate scheduler;
	private Exception initException;
	private String schedulerNameAndId;
	private boolean schedulerInitialized;
	
	public SchedulerService(String configId, ConfigStore configStore) {
		this.configId = configId;
		this.configStore = configStore;
		loadConfig();
	}
			
	public Properties loadConfig() {
		// Initialize these control fields regardless of settings.
		String configPropsText = configStore.get(configId);
		Properties props = ServiceUtils.textToProps(configPropsText);
		
		autoInit = Boolean.parseBoolean(props.getProperty(AUTO_INIT_KEY, "true"));
		autoStart = Boolean.parseBoolean(props.getProperty(AUTO_START_KEY, "false"));		
		preventAutoStartRemoteScheduler = Boolean.parseBoolean(props.getProperty(PREVENT_AUTO_START_REMOTE_SCHEDULER_KEY, "false"));
		waitForJobsToComplete = Boolean.parseBoolean(props.getProperty(WAIT_FOR_JOBS_TO_COMPLETES_KEY, "false"));
		
		return props;
	}
	
	public boolean isAutoStart() {
		return autoStart;
	}
	
	/**
	 * This flag is represent the scheduler state has initialized or not. This is different to this.isInit() that 
	 * indicate whether the this Service is initialized or not.
	 */
	public boolean isSchedulerInitialized() {
		return schedulerInitialized;
	}
	
	public String getSchedulerNameAndId() {
		if (schedulerNameAndId == null) {
			return "ConfigId=" + configId;
		}
		return schedulerNameAndId;
	}
	
	public String getConfigId() {
		return configId;
	}

	public SchedulerTemplate getScheduler() {
		return scheduler;
	}
	
	public Exception getInitException() {
		return initException;
	}
		
	@Override
	protected void initService() {		
		// Check to see if we need to init scheduler.
		if (autoInit) {
			initScheduler();
		}
	}
	
	public void initScheduler() {
		// Init scheduler.
		try {
			logger.debug("Initializing scheduler with configId {}", configId);
			Properties props = loadConfig();
			scheduler = new SchedulerTemplate(props);
			logger.info("Scheduler with configId {} initialized. Scheduler name: {}", 
					configId, scheduler.getSchedulerName());
			schedulerNameAndId = scheduler.getSchedulerNameAndId();
		} catch (QuartzRuntimeException e) {
			initException = (Exception)e.getCause(); // save the exception for display use.
			throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, 
					"Failed to initialize Quartz scheduler using configProps.", e.getCause());
		}
		schedulerInitialized = true;
	}
	
	@Override
	protected void startService() {
		if (schedulerInitialized && autoStart) {
			startScheduler();
		}
	}

	public void startScheduler() {
		if (!schedulerInitialized) {
			throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, 
					"Quartz scheduler with configId " + configId + " has not been initialized yet.");
		}
		
		if (preventAutoStartRemoteScheduler && scheduler.getScheduler() instanceof RemoteScheduler) {
			logger.warn("MyScheduler application will not auto start an RemoteScheduler " +
					"because it will affect remote server. Please start it manually.");
			return;
		}
		
		logger.debug("Auto starting scheduler {}.", scheduler.getSchedulerName());
		try {
			scheduler.start();
			logger.info("Quartz scheduler {} has auto started.", scheduler.getSchedulerName());
		} catch (QuartzRuntimeException e) {
			throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, 
					"Failed to start Quartz scheduler " + scheduler.getSchedulerName(), e);
		}
	}

	@Override
	protected void stopService() {
		// Do nothing.
	}
	
	@Override
	protected void destroyService() {
		shutdownScheduler();
	}
	
	public void shutdownScheduler() {
		if (scheduler == null) {
			// scheduler has never been initialzed.
			return;
		}
		if (preventAutoStartRemoteScheduler && scheduler.getScheduler() instanceof RemoteScheduler) {
			logger.warn("MyScheduler application will not auto shutdown an RemoteScheduler " +
					"because it will affect remote server. Please shutdown manually.");
			scheduler = null;
			schedulerInitialized = false;
			return;
		}
		
		try {
			String name = scheduler.getSchedulerNameAndId();
			logger.debug("Shutting down quartz scheduler {} with waitForJobsToComplete={}", 
					name, waitForJobsToComplete);
			scheduler.shutdown(waitForJobsToComplete);
			logger.info("Quartz scheduler config {} has been shutdown.", name);
			scheduler = null;
			schedulerInitialized = false;
		} catch (QuartzRuntimeException e) {
			throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, 
					"Failed to shutodwn quartz scheduler " + scheduler.getSchedulerName(), e);
		}
	}

	@Override
	public String toString() {
		String schedulerName = (scheduler == null) ? null : scheduler.toString();
		return "SchedulerService[configId=" + configId + ", schedulerName=" + schedulerName + "]";
	}
}
