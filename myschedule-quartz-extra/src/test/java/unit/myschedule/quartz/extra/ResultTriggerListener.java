package unit.myschedule.quartz.extra;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

public class ResultTriggerListener implements TriggerListener {
	public static Result result = new Result();
	public static void resetResult() {
		result = new Result();
	}
	@Override
	public String getName() {
		return "TestTriggerListener";
	}

	public static class Result {
		public List<Date> triggerFiredTimes = new ArrayList<Date>();
		public List<Date> vetoJobExecutionTimes = new ArrayList<Date>();
		public List<Date> triggerMisfiredTimes = new ArrayList<Date>();
		public List<Date> triggerCompleteTimes = new ArrayList<Date>();
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		result.triggerFiredTimes.add(new Date());
	}
	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		result.vetoJobExecutionTimes.add(new Date());
		return false;
	}
	@Override
	public void triggerMisfired(Trigger trigger) {
		result.triggerMisfiredTimes.add(new Date());
	}
	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context, int triggerInstructionCode) {
		result.triggerCompleteTimes.add(new Date());
	}
	
}
