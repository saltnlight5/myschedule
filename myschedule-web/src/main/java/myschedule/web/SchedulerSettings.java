package myschedule.web;

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
	public static final String SETTINGS_KEY_PREFIX = "myschedule.scheduler.";
    private String settingsName;
	private String settingsUrl;
	private Props props;
	/** If scheduler failed to init, then we will save the exception, else it's null. */
	private Exception schedulerException;
    public static final String DEFAULT_SCHEDULER_NAME = "DefaultQuartzScheduler";
    public static final String DEFAULT_SCHEDULER_ID = "NON_CLUSTERED";
    private String schedulerFullName;

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

	public void setSchedulerException(Exception schedulerException) {
		this.schedulerException = schedulerException;
	}
	public Exception getSchedulerException() {
		return schedulerException;
	}
	
	public String getSettingsUrl() {
		return settingsUrl;
	}
	
	/** Get Quartz properties for this scheduler. */
	public Properties getQuartzProperties() {
		return props.toProperties();
	}

	/** Auto create scheduler instance. Default is true. */
	public boolean isAutoCreate() {
		return props.getBoolean(SETTINGS_KEY_PREFIX + "autoCreate", true);
	}
	
	/** Auto start this scheduler after MySchedule is inited. Default is true. */
	public boolean isAutoStart() {
		return props.getBoolean(SETTINGS_KEY_PREFIX + "autoStart", true);
	}
	
	/** Wait for jobs to complete after this scheduler shutdown. Default is true. */
	public boolean isWaitForJobToComplete() {
		return props.getBoolean(SETTINGS_KEY_PREFIX + "waitForJobToComplete", true);
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
