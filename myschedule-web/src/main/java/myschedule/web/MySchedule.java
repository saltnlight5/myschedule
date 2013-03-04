package myschedule.web;

import myschedule.quartz.extra.SchedulerTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is the central manager of the MySchedule application. There is only one instance of MySchedule application, 
 * and it's used to hold all config and initialization of code. User may use the {@link #getInstance()} to retrieve it.
 * @author zemian
 */
public class MySchedule {
	public static final String SETTINGS_FILE_EXT = ".properties";
	private static final Logger LOGGER = LoggerFactory.getLogger(MySchedule.class);
	private static volatile MySchedule instance;
    private AtomicBoolean inited = new AtomicBoolean(false);
	private MyScheduleSettings myScheduleSettings;
	private Map<String, SchedulerSettings> schedulerSettingsMap; //key=SettingsName
	private Map<String, SchedulerTemplate> schedulers;           //key=SettingsName
    private SchedulerSettingsStore schedulerSettingsStore;
	
	private MySchedule() {
	}
	
	public static MySchedule getInstance() {
		if (instance == null) {
			// Use double locking synchronized singleton pattern
			synchronized(MySchedule.class) {
				if (instance == null) {
					instance = new MySchedule();
                    instance.init();
				}
			}
		}
		return instance;
	}
	
	public void init() {
        if (inited.get())
            return;

		LOGGER.debug("Initializing MySchedule ...");
		initMyScheduleSettings();
        initServices();
		initSchedulerSettingsMap();
		createSchedulers();
		addDefaultSchedulerSettings();
		LOGGER.info("MySchedule initialized.");
        inited.set(true);
	}

    /**
     * If MySchedule do not have any scheduler settings, and user does provided a default one, this method will auto
     * add it into the settings map.
     */
	private void addDefaultSchedulerSettings() {
		// Do nothing if we already have some scheduler settings loaded.
		if (schedulerSettingsMap.size() > 0)
			return;

        String propsString = getUserDefaultSchedulerConfig();
        if (StringUtils.isNotEmpty(propsString)) {
            LOGGER.info("Adding a new scheduler from default config settings.");
            addSchedulerSettings(propsString);
        }
	}

    private void initServices() {
        // Init schedulerSettingsStore
        schedulerSettingsStore = new SchedulerSettingsStore(myScheduleSettings.getSchedulerSettingsDir());
        schedulerSettingsStore.init();
    }

	private void initSchedulerSettingsMap() {
		// Load up all scheduler settings from config dir files.
		schedulerSettingsMap = new HashMap<String, SchedulerSettings>();
        for (SchedulerSettings settings : schedulerSettingsStore.getAll())
            schedulerSettingsMap.put(settings.getSettingsName(), settings);
	}

	private void createSchedulers() {
		// Init the map first
		schedulers = new HashMap<String, SchedulerTemplate>();
		
		// Create and init all schedulers
		for (String name : schedulerSettingsMap.keySet()) {
			SchedulerSettings schedulerSettings = schedulerSettingsMap.get(name);
			if (schedulerSettings.isAutoCreate()) {
                createScheduler(name, schedulerSettings);
            }
		}
	}
	
	/** Create and init scheduler and add to scheduler map. */
	public void createScheduler(String settingsName, SchedulerSettings schedulerSettings) {
        try {
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
        } catch (RuntimeException e) {
            // Even if Quartz scheduler failed to load, we will allow the MySchedule to continue, so we simply log
            // the error and continue.
            LOGGER.error("Failed to init scheduler with settings {}", settingsName, e);
            schedulerSettings.setSchedulerException(e);
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

    /**
     * Retrieve user default scheduler config text. If not found it returns empty string, not null!
     */
    public String getUserDefaultSchedulerConfig() {
        String defaultSchedulerSettingsUrl = myScheduleSettings.getDefaultSchedulerSettings();
        if (StringUtils.isEmpty(defaultSchedulerSettingsUrl)) {
            LOGGER.debug("User default scheduler settings/config text is EMPTY.");
            return "";
        } else {
            LOGGER.debug("Reading user default scheduler settings/config text url={}", defaultSchedulerSettingsUrl);
            String result = getSchedulerSettingsConfig(defaultSchedulerSettingsUrl);
            return result;
        }
    }

    public String getSchedulerSettingsConfig(String configUrlName) {
        URL url = ClasspathURLStreamHandler.createURL(configUrlName);
        InputStream inStream = null;
        try {
            inStream = url.openStream();
            String result = IOUtils.toString(inStream);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed read scheduler settings url=" + configUrlName);
        } finally {
            if (inStream != null)
                IOUtils.closeQuietly(inStream);
        }
    }
	
	/** Add to schedulerSettingsMap and scheduler map, and create the file. */
	public SchedulerSettings addSchedulerSettings(String propsString) {
        SchedulerSettings settings = schedulerSettingsStore.add(propsString);
        String settingsName = settings.getSettingsName();
		schedulerSettingsMap.put(settingsName, settings);

        if (settings.isAutoCreate())
			createScheduler(settingsName, settings);

        return settings;
	}

	/** Remove from schedulerSettingsMap and scheduler map, and remove the file. */
	public void deleteSchedulerSettings(String settingsName) {
		if (schedulers.containsKey(settingsName)) {
			shutdownScheduler(settingsName);
		}
		
		SchedulerSettings schedulerSettings = schedulerSettingsMap.get(settingsName);
		if (schedulerSettings != null) {
            schedulerSettingsStore.remove(settingsName);
			schedulerSettingsMap.remove(settingsName);
		}
	}

    /** Update an existing scheduler settings config file. */
    public void updateSchedulerSettings(String settingsName, String propsString) {
        if (schedulers.containsKey(settingsName)) {
            shutdownScheduler(settingsName);
        }

        SchedulerSettings schedulerSettings = schedulerSettingsMap.get(settingsName);
        if (schedulerSettings != null) {
            // Update and reload a new settings instance.
            schedulerSettings = schedulerSettingsStore.update(settingsName, propsString);
            schedulerSettingsMap.put(settingsName, schedulerSettings);

            // Create the scheduler instance if needed
            if (schedulerSettings.isAutoCreate())
                createScheduler(settingsName, schedulerSettings);
        }
    }

	private void initMyScheduleSettings() {
		myScheduleSettings = new MyScheduleSettings();
	}

	public void destroy() {
        if (!inited.get())
            return;

		LOGGER.debug("Destroying MySchedule ...");
		shutdownAllSchedulers();
		delayShutdown();
		LOGGER.info("MySchedule destroyed.");
        inited.set(false);
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
            return SchedulerStatus.SHUTDOWN;

        else if (scheduler.isShutdown())
            return SchedulerStatus.SHUTDOWN;
        else if (scheduler.isInStandbyMode())
            return SchedulerStatus.STANDBY;
        else if (scheduler.isStarted())
            return SchedulerStatus.RUNNING;

        return SchedulerStatus.SHUTDOWN;
    }
}
