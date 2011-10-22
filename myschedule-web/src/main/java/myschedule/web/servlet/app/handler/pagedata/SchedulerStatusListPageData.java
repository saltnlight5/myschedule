package myschedule.web.servlet.app.handler.pagedata;

import java.util.List;

import org.quartz.SchedulerMetaData;

public class SchedulerStatusListPageData {
	protected List<SchedulerStatus> schedulerStatusList;
	
	public List<SchedulerStatus> getSchedulerStatusList() {
		return schedulerStatusList;
	}
	
	public void setSchedulerStatusList(List<SchedulerStatus> schedulerStatusList) {
		this.schedulerStatusList = schedulerStatusList;
	}
	
	public static class SchedulerStatus {
		
		protected String configId;
		protected String name;
		protected boolean initialized;
		protected boolean started;
		protected boolean standby;
		protected boolean shutdown;
		protected SchedulerMetaData schedulerMetaData;
		protected int jobCount;
		
		public void setConfigId(String configId) {
			this.configId = configId;
		}
		public String getConfigId() {
			return configId;
		}
		public void setShutdown(boolean shutdown) {
			this.shutdown = shutdown;
		}
		public boolean isShutdown() {
			return shutdown;
		}
		public boolean isStandby() {
			return standby;
		}
		public void setStandby(boolean standby) {
			this.standby = standby;
		}
		public boolean isStarted() {
			return started;
		}
		public void setStarted(boolean started) {
			this.started = started;
		}
		public void setJobCount(int jobCount) {
			this.jobCount = jobCount;
		}
		public int getJobCount() {
			return jobCount;
		}		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean isInitialized() {
			return initialized;
		}
		public void setInitialized(boolean initialized) {
			this.initialized = initialized;
		}
		public SchedulerMetaData getSchedulerMetaData() {
			return schedulerMetaData;
		}
		public void setSchedulerMetaData(SchedulerMetaData schedulerMetaData) {
			this.schedulerMetaData = schedulerMetaData;
		}
	}
}
