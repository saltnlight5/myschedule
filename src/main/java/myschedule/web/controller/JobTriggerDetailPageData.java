package myschedule.web.controller;

import java.util.Date;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class JobTriggerDetailPageData {
	protected int fireTimesCount;
	protected List<Date> nextFireTimes;
	protected JobDetail jobDetail;
	protected List<Trigger> triggers;
	protected boolean jobDetailShouldRecover;
	protected List<String> excludeByCalendar;
	
	public Trigger getFirstTrigger() {
		return triggers.get(0);
	}

	public boolean isJobDetailShouldRecover() {
		return jobDetailShouldRecover;
	}
	
	public void setJobDetailShouldRecover(boolean jobDetailShouldRecover) {
		this.jobDetailShouldRecover = jobDetailShouldRecover;
	}
	
	public int getFireTimesCount() {
		return fireTimesCount;
	}
	
	public void setFireTimesCount(int fireTimesCount) {
		this.fireTimesCount = fireTimesCount;
	}
	
	public List<Trigger> getTriggers() {
		return triggers;
	}
	
	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}
	
	public List<Date> getNextFireTimes() {
		return nextFireTimes;
	}
	
	public void setNextFireTimes(List<Date> nextFireTimes) {
		this.nextFireTimes = nextFireTimes;
	}
	
	public JobDetail getJobDetail() {
		return jobDetail;
	}
	
	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}
	
	public List<String> getExcludeByCalendar() {
		return excludeByCalendar;
	}
	
	public void setExcludeByCalendar(List<String> excludeByCalendar) {
		this.excludeByCalendar = excludeByCalendar;
	}
	
}