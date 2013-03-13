package myschedule.web;

import myschedule.quartz.extra.SchedulerTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the central manager of the MySchedule application. There is only one instance of MySchedule application, 
 * and it's used to hold all config and initialization of code. User may use the {@link #getInstance()} to retrieve it.
 * @author zemian
 */
public class MySchedule extends AbstractService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MySchedule.class);
	private static volatile MySchedule instance;
	private MyScheduleSettings myScheduleSettings;
	private Map<String, SchedulerSettings> schedulerSettingsMap; //key=SettingsName
	private Map<String, SchedulerTemplate> schedulersMap;        //key=SettingsName
    private SchedulerSettingsStore schedulerSettingsStore;
    private TemplatesStore schedulerTemplatesStore;
    private TemplatesStore scriptTemplatesStore;

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

    @Override
	public void initService() {
		LOGGER.debug("Initializing MySchedule ...");
		initMyScheduleSettings();
        initInternalServices();
		initSchedulerSettingsMap();
		initSchedulersMap();
        initDefaultSchedulerIfNeeded();
		LOGGER.info("MySchedule is initialized.");
	}

    @Override
    public void destroyService() {
        LOGGER.debug("Destroying MySchedule ...");
        shutdownAllSchedulers();
        delayShutdown();
        destroyInternalServices();
        LOGGER.info("MySchedule is destroyed.");
    }

    private void initMyScheduleSettings() {
        myScheduleSettings = new MyScheduleSettings();
    }

    private void initInternalServices() {
        schedulerSettingsStore = new SchedulerSettingsStore(myScheduleSettings.getSchedulerSettingsDir());
        schedulerSettingsStore.init();

        schedulerTemplatesStore = new TemplatesStore(myScheduleSettings.getSchedulerTemplatesDir(),
            ".properties",
            getDefaultSchedulerConfigsTemplates());
        schedulerTemplatesStore.init();

        scriptTemplatesStore = new TemplatesStore(myScheduleSettings.getScriptTemplatesDir(),
            "", // empty file extension for these templates set
            getDefaultScriptsTemplates());
        scriptTemplatesStore.init();
    }

    private String[] getDefaultSchedulerConfigsTemplates() {
        String resNamePrefix = "/myschedule/web/templates/schedulerconfigs/";
        String[] result = {
            resNamePrefix + "full-config-quartz.properties",
            resNamePrefix + "in-memory-quartz.properties",
            resNamePrefix + "jmx-quartz.properties",
            resNamePrefix + "mysql-clustered-quartz.properties",
            resNamePrefix + "mysql-jee-cmt-quartz.properties",
            resNamePrefix + "mysql-quartz.properties",
            resNamePrefix + "oracle-quartz.properties",
            resNamePrefix + "rmi-client-quartz.properties",
            resNamePrefix + "rmi-server-quartz.properties"
        };
        return result;
    }

    private String[] getDefaultScriptsTemplates() {
        String resNamePrefix = "/myschedule/web/templates/scripts/";
        String[] result = {
            resNamePrefix + "jobSamples.groovy",
            resNamePrefix + "simpleJobs.js",
            resNamePrefix + "cronJobs.js",
            resNamePrefix + "calendarJobs.js",
            resNamePrefix + "advanceJobs.js"
        };
        return result;
    }

    private void destroyInternalServices() {
        schedulerSettingsStore.destroy();
        schedulerTemplatesStore.destroy();
        scriptTemplatesStore.destroy();
    }

	private void initSchedulerSettingsMap() {
		// Load up all scheduler settings from config dir files.
		schedulerSettingsMap = new HashMap<String, SchedulerSettings>();
        for (SchedulerSettings settings : schedulerSettingsStore.getAll())
            schedulerSettingsMap.put(settings.getSettingsName(), settings);
	}

    private void initDefaultSchedulerIfNeeded() {
        // Check and see if we need to auto create a default scheduler settings
        if (schedulerSettingsMap.size() == 0) {
            String propsString = getUserDefaultSchedulerConfig();
            if (StringUtils.isNotBlank(propsString)) {
                LOGGER.info("Adding a new default scheduler settings.");
                addSchedulerSettings(propsString);
            }
        }
    }

	private void initSchedulersMap() {
		// Init the map first
		schedulersMap = new HashMap<String, SchedulerTemplate>();
		
		// Create and init all schedulersMap using schedulerSettingsMap
		for (SchedulerSettings settings : schedulerSettingsMap.values()) {
			if (settings.isAutoCreate()) {
                createScheduler(settings);
            }
		}
	}
	
	/** Create and init scheduler and add to scheduler map. */
	public void createScheduler(SchedulerSettings settings) {
        String settingsName = settings.getSettingsName();
        try {
            // If scheduler already exists, shut it down first
            if (schedulersMap.containsKey(settingsName)) {
                LOGGER.warn("Scheduler settings {} has already been initialized. Will shutdown first.", settingsName);
                shutdownScheduler(settingsName);
            }

            // Now create the scheduler
            // Initialize Quartz scheduler. If configured, the Quartz will try to connect to DB upon init!
            LOGGER.info("Creating new Quartz scheduler from {}", settings);
            SchedulerTemplate schedulerTemplate = new SchedulerTemplate(settings.getQuartzProperties());
            schedulersMap.put(settingsName, schedulerTemplate);
            settings.setSchedulerException(null);
            LOGGER.info("Quartz scheduler created with settings name {}", settingsName);

            if (settings.isAutoStart()) {
                LOGGER.debug("Auto starting scheduler.");
                schedulerTemplate.start();
                LOGGER.info("Auto started scheduler per configured settings.");
            }
        } catch (RuntimeException e) {
            // Even if Quartz scheduler failed to load, we will allow the MySchedule to continue, so we simply log
            // the error and continue.
            LOGGER.error("Failed to init scheduler with settings {}", settingsName, e);
            settings.setSchedulerException(e);
        }
	}

	/** Shutdown scheduler and remove from scheduler map. */
	public void shutdownScheduler(String settingsName) {
		SchedulerSettings schedulerSettings = schedulerSettingsMap.get(settingsName);
		boolean waitForJobToComplete = schedulerSettings.isWaitForJobToComplete();
		LOGGER.info("Shutting down {} with waitForJobToComplete={}", schedulerSettings, waitForJobToComplete);
		SchedulerTemplate scheduler = schedulersMap.get(settingsName);
        if (scheduler != null) {
            if (!scheduler.isShutdown())
                scheduler.shutdown(waitForJobToComplete);
            else
                LOGGER.info("Scheduler settingsName={} has already been shutdown. No action.", settingsName);
		    schedulersMap.remove(settingsName);
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
            return readText(defaultSchedulerSettingsUrl);
        }
    }

    public String readText(String urlName) {
        URL url = ClasspathURLStreamHandler.createURL(urlName);
        InputStream inStream = null;
        try {
            inStream = url.openStream();
            return IOUtils.toString(inStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed read content from url=" + urlName);
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
			createScheduler(settings);

        return settings;
	}

	/** Remove from schedulerSettingsMap and scheduler map, and remove the file. */
	public void deleteSchedulerSettings(String settingsName) {
		if (schedulersMap.containsKey(settingsName)) {
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
        if (schedulersMap.containsKey(settingsName)) {
            shutdownScheduler(settingsName);
        }

        SchedulerSettings schedulerSettings = schedulerSettingsMap.get(settingsName);
        if (schedulerSettings != null) {
            // Update and reload a new settings instance.
            schedulerSettings = schedulerSettingsStore.update(settingsName, propsString);
            schedulerSettingsMap.put(settingsName, schedulerSettings);

            // Create the scheduler instance if needed
            if (schedulerSettings.isAutoCreate())
                createScheduler(schedulerSettings);
        }
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
				LOGGER.debug("Pausing {}ms after all schedulersMap shutdown to avoid web server problem.", pauseTime);
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
		return schedulersMap.get(settingsName);
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

    public TemplatesStore getSchedulerTemplatesStore() {
        return schedulerTemplatesStore;
    }

    public TemplatesStore getScriptTemplatesStore() {
        return scriptTemplatesStore;
    }
}
