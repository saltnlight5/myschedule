package myschedule.web.controller;

import java.util.TreeMap;

public class SchedulerStatusPageData {
	private TreeMap<String, String> schedulerInfo;

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