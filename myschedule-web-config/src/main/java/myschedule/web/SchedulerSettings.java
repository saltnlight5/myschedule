package myschedule.web;

import myschedule.quartz.extra.util.Props;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A class to holder a Quartz Scheduler configuration (quartz.properties) and extra settings to be manage by 
 * MySchedule.
 * 
 * @author Zemian Deng
 * @since 2012-11-11
 *
 */
public class SchedulerSettings {
	public static final String SETTINGS_KEY_PREFIX = "myschedule.schedulerService.";
    private String settingsName;
	private String settingsUrl;
	private Props props;
    public static final String DEFAULT_SCHEDULER_NAME = "DefaultQuartzScheduler";
    public static final String DEFAULT_SCHEDULER_ID = "NON_CLUSTERED";
    private String schedulerFullName;
    private Map<String, String> pluginClassNames;

	public SchedulerSettings(String settingsName, String settingsUrl) {
        this.settingsName = settingsName;
		this.settingsUrl = settingsUrl;
		this.props = new Props(settingsUrl);
	}

    public String getSettingsName() {
        return settingsName;
    }

    /**
     * A method to return the full scheduler name. If nothing is set, it will return default values that used
     * by Quartz.
     * @return String in <code>[schedulerName]_$_[schedulerId]</code> format.
     */
    public String getSchedulerFullName() {
        if (schedulerFullName == null && props != null) {
            String name = props.getString("org.quartz.scheduler.instanceName", DEFAULT_SCHEDULER_NAME);
            String id = props.getString("org.quartz.scheduler.instanceId", DEFAULT_SCHEDULER_ID);
            schedulerFullName = name + "_$_" + id;
        }
        return schedulerFullName;
    }
	
	public String getSettingsUrl() {
		return settingsUrl;
	}
	
	/** Get Quartz properties for this scheduler. */
	public Properties getQuartzProperties() {
		return props.toProperties();
	}

	/** Auto create/(quartz calls this init) scheduler instance. Default is true. */
	public boolean isAutoCreate() {
		return props.getBoolean(SETTINGS_KEY_PREFIX + "autoInit", true);
	}
	
	/** Auto start this scheduler after MySchedule is inited. Default is true. */
	public boolean isAutoStart() {
		return props.getBoolean(SETTINGS_KEY_PREFIX + "autoStart", true);
	}
	
	/** Wait for jobs to complete after this scheduler shutdown. Default is true. */
	public boolean isWaitForJobToComplete() {
		return props.getBoolean(SETTINGS_KEY_PREFIX + "waitForJobToComplete", true);
	}

    /** If scheduler is a Quartz Remote scheduler, allow autoStart/Shutdown to be NOT called to prevent
     * unwanted side effect. Default to true. */
    public boolean isPreventAutoStartShutdownRemoteScheduler() {
        return props.getBoolean(SETTINGS_KEY_PREFIX + "preventAutoStartRemoteScheduler", true);
    }

    /**
     * @return a Map of all keys for Quartz plugin class names.
     */
    public Map<String, String> getPluginClassNames() {
        if (pluginClassNames == null) {
            pluginClassNames = new HashMap<String, String>();
            Properties props = getQuartzProperties();
            String pluginPrefix = "org.quartz.plugin.";
            for (String name : props.stringPropertyNames()) {
                if (!name.startsWith(pluginPrefix))
                    continue;

                String pluginName = name.substring(pluginPrefix.length());
                int pos = pluginName.indexOf(".");
                if (pos <= 0)
                    continue;

                pluginName = pluginName.substring(0, pos);
                if (pluginClassNames.containsKey(pluginName))
                    continue;

                String pluginClass = props.getProperty(pluginPrefix + pluginName + ".class");
                if (pluginClass == null)
                    continue;

                pluginClassNames.put(pluginName, pluginClass);
            }
        }

        return pluginClassNames;
    }

    @Override
    public String toString()
    {
        return "SchedulerSettings{" +
            "settingsName='" + settingsName + '\'' +
            ", schedulerFullName='" + getSchedulerFullName() + '\'' +
            '}';
    }
}
