package myschedule.web.controller;

import java.util.Date;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class JobFireTimesPageData {
	private int fireTimesCount;	
	private List<Date> nextFireTimes;
	private JobDetail jobDetail;
	private Trigger trigger;
		
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