package myschedule.web.controller;

import java.util.TreeMap;

public class SchedulerStatusPageData {
	private TreeMap<String, String> schedulerInfo;
	private boolean schedulerStarted;
	private String schedulerName;
	
	
	/**
	 * Getter.
	 * @return the schedulerStarted - boolean
	 */
	public boolean isSchedulerStarted() {
		return schedulerStarted;
	}

	/**
	 * Setter
	 * @param schedulerStarted boolean, the schedulerStarted to set
	 */
	public void setSchedulerStarted(boolean schedulerStarted) {
		this.schedulerStarted = schedulerStarted;
	}

	/**
	 * Getter.
	 * @return the schedulerName - String
	 */
	public String getSchedulerName() {
		return schedulerName;
	}

	/**
	 * Setter
	 * @param schedulerName String, the schedulerName to set
	 */
	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	/**
	 * Getter.
	 * @return the schedulerInfo - TreeMap<String,String>
	 */
	public TreeMap<String, String> getSchedulerInfo() {
		return schedulerInfo;
	}

	/**
	 * Setter
	 * @param schedulerInfo TreeMap<String,String>, the schedulerInfo to set
	 */
	public void setSchedulerInfo(TreeMap<String, String> schedulerInfo) {
		this.schedulerInfo = schedulerInfo;
	}
	
	
}