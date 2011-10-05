package myschedule.web.controller;

public class SchedulerServiceForm {
	protected String configProperties;
	protected String fileLocation;
	protected boolean autoStart;
	protected boolean waitForJobsToComplete;
	
	public String getConfigProperties() {
		return configProperties;
	}
	public void setConfigProperties(String configProperties) {
		this.configProperties = configProperties;
	}
	public String getFileLocation() {
		return fileLocation;
	}
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	public boolean isAutoStart() {
		return autoStart;
	}
	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}
	public boolean isWaitForJobsToComplete() {
		return waitForJobsToComplete;
	}
	public void setWaitForJobsToComplete(boolean waitForJobsToComplete) {
		this.waitForJobsToComplete = waitForJobsToComplete;
	}
}
