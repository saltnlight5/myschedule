package myschedule.web.controller;

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
		protected String name;
		protected boolean initialized;
		protected boolean paused;
		protected boolean started;
		protected SchedulerMetaData schedulerMetaData;
		protected int jobCount;
		
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
		public boolean isPaused() {
			return paused;
		}
		public void setPaused(boolean paused) {
			this.paused = paused;
		}
		public SchedulerMetaData getSchedulerMetaData() {
			return schedulerMetaData;
		}
		public void setSchedulerMetaData(SchedulerMetaData schedulerMetaData) {
			this.schedulerMetaData = schedulerMetaData;
		}
	}
}
