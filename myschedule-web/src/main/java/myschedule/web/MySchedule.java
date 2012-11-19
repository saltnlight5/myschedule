package myschedule.web;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import myschedule.quartz.extra.SchedulerTemplate;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the central manager of the MySchedule application. There is only one instance of MySchedule application, 
 * and it's used to hold all config and initialization of code. User may use the {@link #getInstance()} to retrieve it.
 * @author zemian
 */
public class MySchedule {
	public static final String SETTINGS_FILE_EXT = ".properties";
	private static Logger logger = LoggerFactory.getLogger(MySchedule.class);
	private static volatile MySchedule instance;
	private MyScheduleSettings myScheduleSettings;
	private Map<String, SchedulerSettings> schedulerSettingsMap; //key=SettingsName
	private Map<String, SchedulerTemplate> schedulers;           //key=SettingsName
	
	private MySchedule() {
	}
	
	public static MySchedule getInstance() {
		if (instance == null) {
			// Use double locking synchronized singleton pattern
			synchronized(MySchedule.class) {
				if (instance == null) {
					instance = new MySchedule();
				}
			}
		}
		return instance;
	}

	public void init() {
		logger.debug("Initializing MySchedule ...");
		initMyScheduleSettings();
		initSchedulerSettings();
		initSchedulers();
		logger.info("MySchedule initialized.");
	}

	private void initSchedulerSettings() {
		// Get configDir and create it if not exists
		File configDir = myScheduleSettings.getSchedulerSettingsDir();
		if (!configDir.exists()) {
			logger.info("Creating scheduler settings dir {}.", configDir);
			configDir.mkdirs();
		}

		// Load up all scheduler settings from config dir files.
		schedulerSettingsMap = new HashMap<String, SchedulerSettings>();
		File[] files = configDir.listFiles();
		for (File file : files) {
			String name = file.getName();
			SchedulerSettings schedulerSettings = new SchedulerSettings(file.getPath());
			String settingsName = name.split(SETTINGS_FILE_EXT)[0];
			schedulerSettingsMap.put(settingsName, schedulerSettings);
		}
	}

	private void initSchedulers() {
		// Init the map first
		schedulers = new HashMap<String, SchedulerTemplate>();
		
		// Init all schedulers
		for (String name : schedulerSettingsMap.keySet()) {
			initScheduler(name);
		}
	}
	
	/** Init scheduler and add to scheduler map. */
	public void initScheduler(String settingsName) {
		// If scheduler already exists, shut it down first
		if (schedulers.containsKey(settingsName)) {
			logger.warn("Scheduler settings {} has already been initiazlied. Will shutdown first.", settingsName);
			shutdownScheduler(settingsName);
		}
		
		// Now init the scheduler
		logger.info("Initializing scheduler settings: {}", settingsName);
		SchedulerSettings schedulerSettings = schedulerSettingsMap.get(settingsName);
		try {
			// Initialize Quartz scheduler. If configured, the Quartz will try to connect to DB upon init!
			logger.info("Creating Quartz scheduler from {}", schedulerSettings.getSettingsUrl());
			SchedulerTemplate schedulerTemplate = new SchedulerTemplate(schedulerSettings.getQuartzProperties());
			schedulers.put(settingsName, schedulerTemplate);
			schedulerSettings.setSchedulerException(null);
			logger.info("Quartz scheduler created with settings name {}", settingsName);
			
			if (schedulerSettings.isAutoStart()) {
				logger.debug("Auto starting scheduler.");
				schedulerTemplate.start();
				logger.info("Auto started scheduler per configured settings.");
			}
		} catch (RuntimeException e) {
			// Even if Quartz scheduler failed to load, we will allow the MySchedule to continue, so we simply log
			// the error and continue.
			logger.error("Failed to load scheduler from config settings name: {}", settingsName, e);
			schedulerSettings.setSchedulerException(e);
		}
	}

	/** Shutdown scheduler and remove from scheduler map. */
	public void shutdownScheduler(String settingsName) {
		logger.info("Shutting down scheduler for settings: {}", settingsName);
		SchedulerSettings schedulerSettings = schedulerSettingsMap.get(settingsName);
		boolean waitForJobToComplete = schedulerSettings.isWaitForJobToComplete();
		logger.debug("Shutting down scheduler with waitForJobToComplete={}", waitForJobToComplete);
		schedulers.get(settingsName).shutdown(waitForJobToComplete);
		schedulers.remove(settingsName);
	}
	
	/** Add to schedulerSettingsMap and scheduler map, and create the file. */
	public void addSchedulerSettings(String propsString) {
		String settingsName = UUID.randomUUID().toString();
		File file = getSettingsFile(settingsName);
		try {
			FileWriter writer = new FileWriter(file);
			IOUtils.write(propsString, writer);
		} catch (Exception e) {
			logger.error("Failed to save scheduler settings file {}", settingsName, e);
		}
	
		SchedulerSettings schedulerSettings = new SchedulerSettings(file.getPath());
		schedulerSettingsMap.put(settingsName, schedulerSettings);
		initScheduler(settingsName);
	}

	/** Remove from schedulerSettingsMap and scheduler map, and remove the file. */
	public void deleteSchedulerSettings(String settingsName) {
		if (schedulers.containsKey(settingsName)) {
			shutdownScheduler(settingsName);
		}
		
		SchedulerSettings schedulerSettings = schedulerSettingsMap.get(settingsName);
		if (schedulerSettings != null) {
			File file = new File(schedulerSettings.getSettingsUrl());
			file.delete();
			schedulerSettingsMap.remove(settingsName);
		}
	}
	
	private File getSettingsFile(String settingsName) {
		File configDir = myScheduleSettings.getSchedulerSettingsDir();
		return new File(configDir, settingsName + SETTINGS_FILE_EXT);
	}

	private void initMyScheduleSettings() {
		myScheduleSettings = new MyScheduleSettings();
	}

	public void destroy() {
		logger.debug("Destroying MySchedule ...");
		delayShutdown();
		logger.info("MySchedule destroyed.");
	}

	private void delayShutdown() {
		// Tomcat is known to spit out bogus error messages if we don't slow down a bit, so if user configured so, pause 
		// briefly.
		long pauseTime = myScheduleSettings.getPauseTimeAfterShutdown();
		if (pauseTime > 0) {
			try {
				logger.info("Pausing {}ms after all schedulers shutdown to avoid server problem.", pauseTime);
				Thread.sleep(pauseTime); 
			} catch (InterruptedException e) {
				//We are shutting down anyway, if failed, just ignore it.
			}
		}
	}
	
	public SchedulerTemplate getScheduler(String settingsName) {
		return schedulers.get(settingsName);
	}
}
