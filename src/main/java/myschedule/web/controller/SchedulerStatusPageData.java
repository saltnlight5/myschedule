package myschedule.web.controller;

import java.util.TreeMap;

public class SchedulerStatusPageData {
	private TreeMap<String, String> schedulerInfo;
	private String schedulerName;
	private boolean schedulerInStandbyMode;
	
	/**
	 * Getter.
	 * @return the schedulerInStandbyMode - boolean
	 */
	public boolean isSchedulerInStandbyMode() {
		return schedulerInStandbyMode;
	}
	
	/**
	 * Setter
	 * @param schedulerInStandbyMode boolean, the schedulerInStandbyMode to set
	 */
	public void setSchedulerInStandbyMode(boolean schedulerInStandbyMode) {
		this.schedulerInStandbyMode = schedulerInStandbyMode;
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