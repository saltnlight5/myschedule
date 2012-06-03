package myschedule.web.servlet.app.handler;

import java.util.Date;
import java.util.List;

import myschedule.service.Tuple2;

import org.quartz.CalendarIntervalTrigger;
import org.quartz.CronTrigger;
import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public class JobData {
	
	/** A wrapper to trigger object for JSP display. */
	public static class TriggerDetail {
		private Trigger trigger;
		private Integer listIndex;
		private String jobClassName;
		/** A list of next fire time and a desc of whether it will be excluded by calendar or not. */
		private List<Tuple2<Date, String>> nextFireTimes;
		private String triggerStatusName;
		private String misfireInstructionName;
		
		public TriggerDetail(Trigger trigger) {
			this.trigger = trigger;
		}
		
		// JSTL/JSP will not allow 'trigger.class' to be used?, so we have this wrapper.
		public String getClassName() { return trigger.getClass().getName(); }
		
		public Boolean getIsSimpleTrigger() { return (trigger instanceof SimpleTrigger); }
		public Boolean getIsCronTrigger() { return (trigger instanceof CronTrigger); }
		public Boolean getIsCalendarIntervalTrigger() { return (trigger instanceof CalendarIntervalTrigger); }
		public Boolean getIsDailyTimeIntervalTrigger() { return (trigger instanceof DailyTimeIntervalTrigger); }

		public Trigger getTrigger() {
			return trigger;
		}

		public void setTrigger(Trigger trigger) {
			this.trigger = trigger;
		}

		public Integer getListIndex() {
			return listIndex;
		}

		public void setListIndex(Integer listIndex) {
			this.listIndex = listIndex;
		}

		public String getJobClassName() {
			return jobClassName;
		}

		public void setJobClassName(String jobClassName) {
			this.jobClassName = jobClassName;
		}

		public List<Tuple2<Date, String>> getNextFireTimes() {
			return nextFireTimes;
		}

		public void setNextFireTimes(List<Tuple2<Date, String>> nextFireTimes) {
			this.nextFireTimes = nextFireTimes;
		}

		public String getTriggerStatusName() {
			return triggerStatusName;
		}

		public void setTriggerStatusName(String triggerStatusName) {
			this.triggerStatusName = triggerStatusName;
		}

		public String getMisfireInstructionName() {
			return misfireInstructionName;
		}

		public void setMisfireInstructionName(String misfireInstructionName) {
			this.misfireInstructionName = misfireInstructionName;
		}
	}
	
	public static class JobWithTrigger implements Comparable<JobWithTrigger> {
		private Trigger trigger;
		private String triggerScheduleDesc;
		private Boolean paused;
		
		@Override
		public int compareTo(JobWithTrigger other) {
			return trigger.compareTo(other.getTrigger());
		}

		public Trigger getTrigger() {
			return trigger;
		}

		public void setTrigger(Trigger trigger) {
			this.trigger = trigger;
		}

		public String getTriggerScheduleDesc() {
			return triggerScheduleDesc;
		}

		public void setTriggerScheduleDesc(String triggerScheduleDesc) {
			this.triggerScheduleDesc = triggerScheduleDesc;
		}

		public Boolean getPaused() {
			return paused;
		}

		public void setPaused(Boolean paused) {
			this.paused = paused;
		}
		
	}

	public static class XmlLoadedJobList {
		private List<String> jobGroupsToNeverDelete;
		private List<String> triggerGroupsToNeverDelete;
		private List<String> loadedJobs; // full name.
		private List<String> loadedTriggers; // full name.
		private Boolean ignoreDuplicates;
		private Boolean overWriteExistingData;
		public List<String> getJobGroupsToNeverDelete() {
			return jobGroupsToNeverDelete;
		}
		public void setJobGroupsToNeverDelete(List<String> jobGroupsToNeverDelete) {
			this.jobGroupsToNeverDelete = jobGroupsToNeverDelete;
		}
		public List<String> getTriggerGroupsToNeverDelete() {
			return triggerGroupsToNeverDelete;
		}
		public void setTriggerGroupsToNeverDelete(List<String> triggerGroupsToNeverDelete) {
			this.triggerGroupsToNeverDelete = triggerGroupsToNeverDelete;
		}
		public List<String> getLoadedJobs() {
			return loadedJobs;
		}
		public void setLoadedJobs(List<String> loadedJobs) {
			this.loadedJobs = loadedJobs;
		}
		public List<String> getLoadedTriggers() {
			return loadedTriggers;
		}
		public void setLoadedTriggers(List<String> loadedTriggers) {
			this.loadedTriggers = loadedTriggers;
		}
		public Boolean getIgnoreDuplicates() {
			return ignoreDuplicates;
		}
		public void setIgnoreDuplicates(Boolean ignoreDuplicates) {
			this.ignoreDuplicates = ignoreDuplicates;
		}
		public Boolean getOverWriteExistingData() {
			return overWriteExistingData;
		}
		public void setOverWriteExistingData(Boolean overWriteExistingData) {
			this.overWriteExistingData = overWriteExistingData;
		}
	}
}
