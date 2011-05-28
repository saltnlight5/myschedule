package myschedule.web.controller;

import java.util.Date;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class TriggerPageData {
	private int nextFireTimesRequested;	
	private List<Date> nextFireTimes;
	private JobDetail jobDetail;
	private Trigger trigger;
		
	/**
	 * Getter.
	 * @return the nextFireTimesRequested - int
	 */
	public int getNextFireTimesRequested() {
		return nextFireTimesRequested;
	}

	/**
	 * Setter
	 * @param nextFireTimesRequested int, the nextFireTimesRequested to set
	 */
	public void setNextFireTimesRequested(int nextFireTimesRequested) {
		this.nextFireTimesRequested = nextFireTimesRequested;
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