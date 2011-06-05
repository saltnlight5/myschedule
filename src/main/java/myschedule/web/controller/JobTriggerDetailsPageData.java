package myschedule.web.controller;

import java.util.Date;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class JobTriggerDetailsPageData {
	private int fireTimesCount;	
	private List<Date> nextFireTimes;
	private JobDetail jobDetail;
	private Trigger trigger;
	private boolean jobDetailShouldRecover;
	private List<String> jobListenerNames;
	private List<String> triggerListenerNames;
		
	/**
	 * Getter.
	 * @return the jobListenerNames - List<String>
	 */
	public List<String> getJobListenerNames() {
		return jobListenerNames;
	}

	/**
	 * Setter
	 * @param jobListenerNames List<String>, the jobListenerNames to set
	 */
	public void setJobListenerNames(List<String> jobListenerNames) {
		this.jobListenerNames = jobListenerNames;
	}

	/**
	 * Getter.
	 * @return the triggerListenerNames - List<String>
	 */
	public List<String> getTriggerListenerNames() {
		return triggerListenerNames;
	}

	/**
	 * Setter
	 * @param triggerListenerNames List<String>, the triggerListenerNames to set
	 */
	public void setTriggerListenerNames(List<String> triggerListenerNames) {
		this.triggerListenerNames = triggerListenerNames;
	}

	/**
	 * Getter.
	 * @return the jobDetailShouldRecover - boolean
	 */
	public boolean isJobDetailShouldRecover() {
		return jobDetailShouldRecover;
	}
	
	/**
	 * Setter
	 * @param jobDetailShouldRecover boolean, the jobDetailShouldRecover to set
	 */
	public void setJobDetailShouldRecover(boolean jobDetailShouldRecover) {
		this.jobDetailShouldRecover = jobDetailShouldRecover;
	}
	
	/**
	 * Getter.
	 * @return the fireTimesCount - int
	 */
	public int getFireTimesCount() {
		return fireTimesCount;
	}
	
	/**
	 * Setter
	 * @param fireTimesCount int, the fireTimesCount to set
	 */
	public void setFireTimesCount(int fireTimesCount) {
		this.fireTimesCount = fireTimesCount;
	}
	
	/**
	 * Getter.
	 * @return the trigger - Trigger
	 */
	public Trigger getTrigger() {
		return trigger;
	}

	/**
	 * Setter
	 * @param trigger Trigger, the trigger to set
	 */
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
	
	/**
	 * Getter.
	 * @return the nextFireTimes - List<Date>
	 */
	public List<Date> getNextFireTimes() {
		return nextFireTimes;
	}
	
	/**
	 * Setter
	 * @param nextFireTimes List<Date>, the nextFireTimes to set
	 */
	public void setNextFireTimes(List<Date> nextFireTimes) {
		this.nextFireTimes = nextFireTimes;
	}
	
	/**
	 * Getter.
	 * @return the jobDetail - JobDetail
	 */
	public JobDetail getJobDetail() {
		return jobDetail;
	}
	
	/**
	 * Setter
	 * @param jobDetail JobDetail, the jobDetail to set
	 */
	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}
	
}