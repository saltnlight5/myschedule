package myschedule.web.servlet.app.handler.pagedata;

import java.util.Date;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class JobTriggerDetailPageData {
	protected int fireTimesCount;
	protected List<Date> nextFireTimes;
	protected JobDetail jobDetail;
	protected List<? extends Trigger> triggers;
	protected List<String> triggerStatusList;
	protected boolean jobDetailShouldRecover;
	protected List<String> excludeByCalendar;
	
	public List<String> getTriggerStatusList() {
		return triggerStatusList;
	}
	public void setTriggerStatusList(List<String> triggerStatusList) {
		this.triggerStatusList = triggerStatusList;
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
	
	public List<? extends Trigger> getTriggers() {
		return triggers;
	}
	
	public void setTriggers(List<? extends Trigger> triggers) {
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