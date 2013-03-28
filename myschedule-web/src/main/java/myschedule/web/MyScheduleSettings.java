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
 * 
 * @author zemian
 *
 */
public class MyScheduleSettings {
	public static final String SETTINGS_SYS_PROPS_KEY = "myschedule.settings";
	public static final String DEFAULT_SETTINGS_URL = "classpath:///myschedule/web/myschedule-settings.properties";
	public static final String SETTINGS_KEY_PREFIX = "myschedule.web.";
	private static final Logger LOGGER = LoggerFactory.getLogger(MyScheduleSettings.class);
	private Props props;
	
	public MyScheduleSettings() {
		initProps();
	}
	
	private void initProps() {
		// Load default settings first
		LOGGER.debug("Loading default settings {}", DEFAULT_SETTINGS_URL);
		props = new Props(DEFAULT_SETTINGS_URL);
		
		// Load custom settings properties
		String customSettings = System.getProperty(SETTINGS_SYS_PROPS_KEY, null);
		if (customSettings != null) {
			LOGGER.info("Loadding custom settings {}", customSettings);
			props.load(customSettings);
		}
		
		// Expand vars if there are any.
		props.expandVariables();
		LOGGER.debug("MySchedule settings are ready.");
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
	
	public String getDefaultSchedulerSettings() {
		return props.getString(SETTINGS_KEY_PREFIX + "defaultSchedulerSettings");
	}

    public String getDefaultScriptEngineName() {
        return props.getString(SETTINGS_KEY_PREFIX + "defaultScriptEngineName");
    }
}
