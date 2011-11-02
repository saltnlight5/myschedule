package myschedule.web.servlet.app.handler.pagedata;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import myschedule.service.SchedulerService;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public class JobListPageData {
	@Getter @Setter
	private SchedulerService schedulerService;
	@Getter @Setter
	private List<Trigger> triggers; // scheduled jobs
	@Getter @Setter
	private List<JobDetail> noTriggerJobDetails;
	@Getter @Setter
	private int showMaxFireTimesCount = 20; // default max size to show next fireTimes
	@Getter @Setter
	private String datePattern = "MM/dd/yy HH:mm:ss";
	
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
}
