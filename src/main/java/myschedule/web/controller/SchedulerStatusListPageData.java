package myschedule.web.controller;

import java.util.List;

public class SchedulerStatusListPageData {
	protected List<SchedulerStatus> schedulerStatusList;
	
	public List<SchedulerStatus> getSchedulerStatusList() {
		return schedulerStatusList;
	}
	
	public void setSchedulerStatusList(List<SchedulerStatus> schedulerStatusList) {
		this.schedulerStatusList = schedulerStatusList;
	}
	
	public static class SchedulerStatus {
		protected String name;
		protected boolean running;
		protected String jobStorageType;
		protected String configPath;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean isRunning() {
			return running;
		}
		public void setRunning(boolean running) {
			this.running = running;
		}
		public String getJobStorageType() {
			return jobStorageType;
		}
		public void setJobStorageType(String jobStorageType) {
			this.jobStorageType = jobStorageType;
		}
		public String getConfigPath() {
			return configPath;
		}
		public void setConfigPath(String configPath) {
			this.configPath = configPath;
		}
	}
}
