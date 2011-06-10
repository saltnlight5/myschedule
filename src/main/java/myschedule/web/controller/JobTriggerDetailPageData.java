package myschedule.web.controller;

import java.util.Date;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class JobTriggerDetailPageData {
	private int fireTimesCount;
	private List<Date> nextFireTimes;
	private JobDetail jobDetail;
	private List<Trigger> triggers;
	private boolean jobDetailShouldRecover;
	
	/**
	 * Get the first trigger in the list.
	 */
	public Trigger getFirstTrigger() {
		return triggers.get(0);
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
	 * @return the triggers - List<Trigger>
	 */
	public List<Trigger> getTriggers() {
		return triggers;
	}
	
	/**
	 * Setter
	 * @param triggers List<Trigger>, the triggers to set
	 */
	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
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