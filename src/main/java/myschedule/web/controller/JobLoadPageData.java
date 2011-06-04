package myschedule.web.controller;

import java.util.List;

/** JobLoadPageData
 *
 * @author Zemian Deng
 */
public class JobLoadPageData {
	private List<String> jobGroupsToNeverDelete;
	private List<String> triggerGroupsToNeverDelete;
	private List<String> loadedJobs; // full name.
	private List<String> loadedTriggers; // full name.
	private boolean ignoreDuplicates;
	private boolean overWriteExistingData;
	/**
	 * Getter.
	 * @return the jobGroupsToNeverDelete - List<String>
	 */
	public List<String> getJobGroupsToNeverDelete() {
		return jobGroupsToNeverDelete;
	}
	/**
	 * Setter
	 * @param jobGroupsToNeverDelete List<String>, the jobGroupsToNeverDelete to set
	 */
	public void setJobGroupsToNeverDelete(List<String> jobGroupsToNeverDelete) {
		this.jobGroupsToNeverDelete = jobGroupsToNeverDelete;
	}
	/**
	 * Getter.
	 * @return the triggerGroupsToNeverDelete - List<String>
	 */
	public List<String> getTriggerGroupsToNeverDelete() {
		return triggerGroupsToNeverDelete;
	}
	/**
	 * Setter
	 * @param triggerGroupsToNeverDelete List<String>, the triggerGroupsToNeverDelete to set
	 */
	public void setTriggerGroupsToNeverDelete(List<String> triggerGroupsToNeverDelete) {
		this.triggerGroupsToNeverDelete = triggerGroupsToNeverDelete;
	}
	/**
	 * Getter.
	 * @return the loadedJobs - List<String>
	 */
	public List<String> getLoadedJobs() {
		return loadedJobs;
	}
	/**
	 * Setter
	 * @param loadedJobs List<String>, the loadedJobs to set
	 */
	public void setLoadedJobs(List<String> loadedJobs) {
		this.loadedJobs = loadedJobs;
	}
	/**
	 * Getter.
	 * @return the loadedTriggers - List<String>
	 */
	public List<String> getLoadedTriggers() {
		return loadedTriggers;
	}
	/**
	 * Setter
	 * @param loadedTriggers List<String>, the loadedTriggers to set
	 */
	public void setLoadedTriggers(List<String> loadedTriggers) {
		this.loadedTriggers = loadedTriggers;
	}
	/**
	 * Getter.
	 * @return the ignoreDuplicates - boolean
	 */
	public boolean isIgnoreDuplicates() {
		return ignoreDuplicates;
	}
	/**
	 * Setter
	 * @param ignoreDuplicates boolean, the ignoreDuplicates to set
	 */
	public void setIgnoreDuplicates(boolean ignoreDuplicates) {
		this.ignoreDuplicates = ignoreDuplicates;
	}
	/**
	 * Getter.
	 * @return the overWriteExistingData - boolean
	 */
	public boolean isOverWriteExistingData() {
		return overWriteExistingData;
	}
	/**
	 * Setter
	 * @param overWriteExistingData boolean, the overWriteExistingData to set
	 */
	public void setOverWriteExistingData(boolean overWriteExistingData) {
		this.overWriteExistingData = overWriteExistingData;
	}
}
