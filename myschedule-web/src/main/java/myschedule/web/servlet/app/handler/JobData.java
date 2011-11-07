package myschedule.web.servlet.app.handler;

import java.util.Date;
import java.util.List;
import lombok.Data;
import myschedule.service.Tuple2;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.CronTrigger;
import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public class JobData {
	
	/** A wrapper to trigger object for JSP display. */
	@Data
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
		
		public boolean isSimpleTrigger() { return (this instanceof SimpleTrigger); }
		public boolean isCronTrigger() { return (this instanceof CronTrigger); }
		public boolean isCalendarIntervalTrigger() { return (this instanceof CalendarIntervalTrigger); }
		public boolean isDailyTimeIntervalTrigger() { return (this instanceof DailyTimeIntervalTrigger); }
	}
	
	@Data
	public static class JobWithTrigger implements Comparable<JobWithTrigger> {
		private Trigger trigger;
		private String triggerScheduleDesc;
		private boolean paused;
		
		@Override
		public int compareTo(JobWithTrigger other) {
			return trigger.compareTo(other.getTrigger());
		}
	}
}
