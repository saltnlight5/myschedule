package myschedule.web;

import java.io.File;

import myschedule.quartz.extra.util.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Settings for MySchedule web application. See DEFAULT_SETTINGS_URL for all possible settings values, default
 * values and their usage description. All settings should have key prefixed with SETTINGS_KEY_PREFIX.
 * 
 * User may use Java system properties "-Dmyschedule.settings=my.propertis" to override any settings.
 * <p>
 * Or user may alternatively use Env variable "MYSCHEDULE_SETTINGS" to specify the property file.
 * </p>
 * 
 * @author zemian
 *
 */
public class MyScheduleSettings {
	public static final String SETTINGS_SYS_PROPS_KEY = "myschedule.settings";
	public static final String SETTINGS_SYS_ENV_KEY = "MYSCHEDULE_SETTINGS";
	public static final String DEFAULT_SETTINGS_URL = "classpath:///myschedule/web/myschedule-settings.properties";
	public static final String SETTINGS_KEY_PREFIX = "myschedule.web.";
	private static final Logger LOGGER = LoggerFactory.getLogger(MyScheduleSettings.class);
	private Props props;
	
	public MyScheduleSettings() {
		initProps();
	}
	
	private void initProps() {
        LOGGER.debug("Sys props " + SETTINGS_SYS_PROPS_KEY + "=" + System.getProperty(SETTINGS_SYS_PROPS_KEY));
        LOGGER.debug("Sys env " + SETTINGS_SYS_ENV_KEY + "=" + System.getenv(SETTINGS_SYS_ENV_KEY));

		// Load default settings first
		LOGGER.debug("Loading default settings {}", DEFAULT_SETTINGS_URL);
		props = new Props();
        props.load(DEFAULT_SETTINGS_URL); // Don't use the constructor to avoid early vars expansion.
		
		// Load custom settings properties
		String customSettings = System.getProperty(SETTINGS_SYS_PROPS_KEY);
		if (customSettings == null)
			customSettings = System.getenv(SETTINGS_SYS_ENV_KEY);

        // -- Auto detect OpenShift env (a workaround to their system that they failed to set env through hook.)
        if (customSettings == null && System.getenv("OPENSHIFT_DATA_DIR") != null)
            customSettings = System.getenv("OPENSHIFT_DATA_DIR") + "/myschedule-settings.properties";

        // -- Load it
		if (customSettings != null) {
			LOGGER.debug("Loadding additional custom settings {}", customSettings);
			props.load(customSettings);
		}
		
		// Expand vars if there are any.
		props.expandVariables();
		LOGGER.info("MySchedule settings are ready. myschedule.web.dataStoreDir=" + props.getString("myschedule.web.dataStoreDir"));

        // Enable debug output if it's on.
        if (LOGGER.isDebugEnabled())
            for (String key : props.keySet())
                LOGGER.debug("Settings value " + key + "=" + props.get(key));
	}

	public long getPauseTimeAfterShutdown() {
		return props.getLong(SETTINGS_KEY_PREFIX + "pauseTimeAfterShutdown");
	}

	public File getSchedulerSettingsDir() {
		return new File(props.getString(SETTINGS_KEY_PREFIX + "schedulerSettingsDir"));
	}

    public File getSchedulerTemplatesDir() {
        return new File(props.getString(SETTINGS_KEY_PREFIX + "schedulerTemplatesDir"));
    }

    public File getScriptTemplatesDir() {
        return new File(props.getString(SETTINGS_KEY_PREFIX + "scriptTemplatesDir"));
    }

    public File getXmlJobLoaderTemplatesDir() {
        return new File(props.getString(SETTINGS_KEY_PREFIX + "xmlJobLoaderTemplatesDir"));
    }
	
	public String getDefaultSchedulerSettings() {
		return props.getString(SETTINGS_KEY_PREFIX + "defaultSchedulerSettings");
	}

    public String getDefaultScriptEngineName() {
        return props.getString(SETTINGS_KEY_PREFIX + "defaultScriptEngineName");
    }

    public int getNumOfFiretimesPreview() {
        return props.getInt(SETTINGS_KEY_PREFIX + "numOfFiretimesPreview");
    }

    public String getJdbcSchedulerHistoryPluginContextKey() {
        return props.getString(SETTINGS_KEY_PREFIX + "JdbcSchedulerHistoryPluginContextKey");
    }
}
