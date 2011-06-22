package myschedule.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public class JobListPageData {
	protected List<Trigger> triggers; // scheduled jobs
	protected List<String> triggerSchedules; // trigger's schedule info
	protected List<JobDetail> noTriggerJobDetails;
	protected int showMaxFireTimesCount = 20; // default max size to show next fireTimes.
	protected String datePattern = "MM/dd/yy HH:mm:ss";
	
	/**
	 * Getter.
	 * @return the datePattern - String
	 */
	public String getDatePattern() {
		return datePattern;
	}
	
	/**
	 * Getter.
	 * @return the triggerSchedules - List<String>
	 */
	public List<String> getTriggerSchedules() {
		List<String> result = new ArrayList<String>();
		for (Trigger trigger : triggers) {
			StringBuilder sb = new StringBuilder();
			if (trigger instanceof SimpleTrigger) {
				SimpleTrigger t = (SimpleTrigger)trigger;
				if (t.getRepeatCount() == SimpleTrigger.REPEAT_INDEFINITELY)
					sb.append("Repeat=FOREVER");
				else
					sb.append("Repeat=" + t.getRepeatCount());
				sb.append(", Interval=" + t.getRepeatInterval());
			} else if (trigger instanceof CronTrigger) {
				CronTrigger t = (CronTrigger)trigger;
				sb.append("Cron=" + t.getCronExpression());				
			} else {
				sb.append(trigger.getClass().getName());
			}
			result.add(sb.toString());
		}
		return result;
	}
	/**
	 * Setter
	 * @param showMaxFireTimesCount int, the showMaxFireTimesCount to set
	 */
	public void setShowMaxFireTimesCount(int showMaxFireTimesCount) {
		this.showMaxFireTimesCount = showMaxFireTimesCount;
	}
	/**
	 * Getter.
	 * @return the showMaxFireTimesCount - int
	 */
	public int getShowMaxFireTimesCount() {
		return showMaxFireTimesCount;
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
	 * @return the noTriggerJobDetails - List<JobDetail>
	 */
	public List<JobDetail> getNoTriggerJobDetails() {
		return noTriggerJobDetails;
	}
	/**
	 * Setter
	 * @param noTriggerJobDetails List<JobDetail>, the noTriggerJobDetails to set
	 */
	public void setNoTriggerJobDetails(List<JobDetail> noTriggerJobDetails) {
		this.noTriggerJobDetails = noTriggerJobDetails;
	}
	
}
