package myschedule.web.controller;


public class SchedulerSummaryPageData {
	
	protected String schedulerSummary;
	protected String schedulerName;
	protected boolean schedulerInStandbyMode;
		
	/**
	 * Getter.
	 * @return the schedulerSummary - String
	 */
	public String getSchedulerSummary() {
		return schedulerSummary;
	}
	
	/**
	 * Setter
	 * @param schedulerSummary String, the schedulerSummary to set
	 */
	public void setSchedulerSummary(String schedulerSummary) {
		this.schedulerSummary = schedulerSummary;
	}	

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
	
}