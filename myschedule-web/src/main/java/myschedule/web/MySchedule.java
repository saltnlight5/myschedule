package myschedule.web;

import myschedule.quartz.extra.SchedulerTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * This is the central manager of the MySchedule application. There is only one instance of MySchedule application, 
 * and it's used to hold all config and initialization of code. User may use the {@link #getInstance()} to retrieve it.
 * @author zemian
 */
public class MySchedule {
	public static final String SETTINGS_FILE_EXT = ".properties";
	private static final Logger LOGGER = LoggerFactory.getLogger(MySchedule.class);
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
		LOGGER.debug("Initializing MySchedule ...");
		initMyScheduleSettings();
		initSchedulerSettings();
		createSchedulers();
		addDefaultSchedulerSettings();
		LOGGER.info("MySchedule initialized.");
	}

	private void addDefaultSchedulerSettings() {
		// Do nothing if we already have some scheduler settings loaded.
		if (schedulerSettingsMap.size() > 0)
			return;
		
		// Check to see if need to load default scheduler
		String defaultSchedulerSettingsUrl = myScheduleSettings.getDefaultSchedulerSettings();
		if (StringUtils.isNotEmpty(defaultSchedulerSettingsUrl)) {
			LOGGER.info("Generating default scheduler settings from {}", defaultSchedulerSettingsUrl);
			URL url = ClasspathURLStreamHandler.createURL(defaultSchedulerSettingsUrl);
			InputStream inStream = null;
			try {
				inStream = url.openStream();
				String propsString = IOUtils.toString(inStream);
				addSchedulerSettings(propsString);
			} catch (IOException e) {
				throw new RuntimeException("Failed to load default scheduler settings " + defaultSchedulerSettingsUrl);
			} finally {
				if (inStream != null)
					IOUtils.closeQuietly(inStream);
			}
		}
	}

	private void initSchedulerSettings() {
		// Get configDir and create it if not exists
		File configDir = myScheduleSettings.getSchedulerSettingsDir();
		if (!configDir.exists()) {
			LOGGER.info("Creating scheduler settings dir {}.", configDir);
			configDir.mkdirs();
		}

		// Load up all scheduler settings from config dir files.
		schedulerSettingsMap = new HashMap<String, SchedulerSettings>();
		File[] files = configDir.listFiles();
		for (File file : files) {
            try {
                String name = file.getName();
                String settingsName = name.split(SETTINGS_FILE_EXT)[0];
                SchedulerSettings schedulerSettings = new SchedulerSettings(settingsName, file.getPath());
                LOGGER.info("Loaded {}", schedulerSettings);
                schedulerSettingsMap.put(settingsName, schedulerSettings);
            } catch (Exception e) {
                LOGGER.warn("Failed to load scheduler settings file={}", file);
            }
		}
	}

	private void createSchedulers() {
		// Init the map first
		schedulers = new HashMap<String, SchedulerTemplate>();
		
		// Create and init all schedulers
		for (String name : schedulerSettingsMap.keySet()) {
			SchedulerSettings schedulerSettings = schedulerSettingsMap.get(name);
			if (schedulerSettings.isAutoCreate()) {
                // Now create the scheduler
                try {
                    createScheduler(name, schedulerSettings);
                } catch (RuntimeException e) {
                    // Even if Quartz scheduler failed to load, we will allow the MySchedule to continue, so we simply log
                    // the error and continue.
                    LOGGER.error("Failed to load scheduler from config settings name: {}", name, e);
                    schedulerSettings.setSchedulerException(e);
                }
            }
		}
	}
	
	/** Create and init scheduler and add to scheduler map. */
	public void createScheduler(String settingsName, SchedulerSettings schedulerSettings) {
		// If scheduler already exists, shut it down first
		if (schedulers.containsKey(settingsName)) {
			LOGGER.warn("Scheduler settings {} has already been initialized. Will shutdown first.", settingsName);
			shutdownScheduler(settingsName);
		}
		
		// Now create the scheduler
        // Initialize Quartz scheduler. If configured, the Quartz will try to connect to DB upon init!
        LOGGER.info("Creating Quartz scheduler from {}", schedulerSettings.getSettingsUrl());
        SchedulerTemplate schedulerTemplate = new SchedulerTemplate(schedulerSettings.getQuartzProperties());
        schedulers.put(settingsName, schedulerTemplate);
        schedulerSettings.setSchedulerException(null);
        LOGGER.info("Quartz scheduler created with settings name {}", settingsName);

        if (schedulerSettings.isAutoStart()) {
            LOGGER.debug("Auto starting scheduler.");
            schedulerTemplate.start();
            LOGGER.info("Auto started scheduler per configured settings.");
        }
	}

	/** Shutdown scheduler and remove from scheduler map. */
	public void shutdownScheduler(String settingsName) {
		SchedulerSettings schedulerSettings = schedulerSettingsMap.get(settingsName);
		boolean waitForJobToComplete = schedulerSettings.isWaitForJobToComplete();
		LOGGER.info("Shutting down {} with waitForJobToComplete={}", schedulerSettings, waitForJobToComplete);
		SchedulerTemplate scheduler = schedulers.get(settingsName);
        if (scheduler != null) {
            if (!scheduler.isShutdown())
                scheduler.shutdown(waitForJobToComplete);
            else
                LOGGER.info("Scheduler settingsName={} has already been shutdown. No action.", settingsName);
		    schedulers.remove(settingsName);
        }
	}
	
	/** Add to schedulerSettingsMap and scheduler map, and create the file. */
	public SchedulerSettings addSchedulerSettings(String propsString) {
		String settingsName = UUID.randomUUID().toString();
		File file = getSettingsFile(settingsName);
		LOGGER.info("Adding new scheduler settings file: {}", file);
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			IOUtils.write(propsString, writer);
			writer.flush();
		} catch (Exception e) {
			LOGGER.error("Failed to save scheduler settings file {}", settingsName, e);
		} finally {
			if (writer != null)
				IOUtils.closeQuietly(writer);
		}
	
		SchedulerSettings schedulerSettings = new SchedulerSettings(settingsName, file.getPath());
		schedulerSettingsMap.put(settingsName, schedulerSettings);

        if (schedulerSettings.isAutoCreate())
			createScheduler(settingsName, schedulerSettings);

        return schedulerSettings;
	}

	/** Remove from schedulerSettingsMap and scheduler map, and remove the file. */
	public void deleteSchedulerSettings(String settingsName) {
		if (schedulers.containsKey(settingsName)) {
			shutdownScheduler(settingsName);
		}
		
		SchedulerSettings schedulerSettings = schedulerSettingsMap.get(settingsName);
		if (schedulerSettings != null) {
			File file = new File(schedulerSettings.getSettingsUrl());
			LOGGER.info("Deleting scheduler settings file: {}", file);
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
		LOGGER.debug("Destroying MySchedule ...");
		shutdownAllSchedulers();
		delayShutdown();
		LOGGER.info("MySchedule destroyed.");
	}

	private void shutdownAllSchedulers() {
		for (String settingsName : schedulerSettingsMap.keySet()) {
            try {
			    shutdownScheduler(settingsName);
            } catch (RuntimeException e) {
                LOGGER.warn("Failed to shutdown scheduler settingsName={}.", settingsName);
            }
		}
	}

	private void delayShutdown() {
		// Tomcat is known to spit out bogus error messages if we don't slow down a bit here, so we purposely delay
		// briefly. User may configured to zero it out if their server do not have this problem.
		long pauseTime = myScheduleSettings.getPauseTimeAfterShutdown();
		if (pauseTime > 0) {
			try {
				LOGGER.debug("Pausing {}ms after all schedulers shutdown to avoid web server problem.", pauseTime);
				Thread.sleep(pauseTime); 
			} catch (InterruptedException e) {
                // Can not sleep? Oh well, we are shutting down anyway, so just ignore and continue.
			}
		}
	}
	
	public List<String> getSchedulerSettingsNames() {
		return new ArrayList<String>(schedulerSettingsMap.keySet());
	}
	
	public SchedulerSettings getSchedulerSettings(String settingsName) {
		return schedulerSettingsMap.get(settingsName);
	}
	
	public SchedulerTemplate getScheduler(String settingsName) {
		return schedulers.get(settingsName);
	}

    public static SchedulerStatus getSchedulerStatus(SchedulerTemplate scheduler) {
        if (scheduler == null)
            return SchedulerStatus.UNINITIALZED;

        else if (scheduler.isShutdown())
            return SchedulerStatus.SHUTDOWN;
        else if (scheduler.isInStandbyMode())
            return SchedulerStatus.STANDBY;
        else if (scheduler.isStarted())
            return SchedulerStatus.RUNNING;

        return SchedulerStatus.UNINITIALZED;
    }
}
