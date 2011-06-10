package myschedule.web.controller;

import java.util.TreeMap;

public class SchedulerDetailPageData extends SchedulerSummaryPageData {
	private TreeMap<String, String> schedulerDetail;
	
	/**
	 * Getter.
	 * @return the schedulerDetail - TreeMap<String,String>
	 */
	public TreeMap<String, String> getSchedulerDetail() {
		return schedulerDetail;
	}

	/**
	 * Setter
	 * @param schedulerDetail TreeMap<String,String>, the schedulerDetail to set
	 */
	public void setSchedulerDetail(TreeMap<String, String> schedulerDetail) {
		this.schedulerDetail = schedulerDetail;
	}
	
}