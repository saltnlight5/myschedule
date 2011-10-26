package unit.myschedule.quartz.extra;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

public class ResultSchedulerListener implements SchedulerListener {
	public static Result result = new Result();
	
	public static void resetResult() {
		result = new Result();
	}
		
	public static class Result {
		public List<Object[]> scheduleErrorTimes = new ArrayList<Object[]>();
	}

	@Override
	public void jobScheduled(Trigger trigger) {
		
		
	}
	@Override
	public void jobUnscheduled(TriggerKey triggerKey) {
		
		
	}
	@Override
	public void triggerFinalized(Trigger trigger) {
		
		
	}
	@Override
	public void triggerPaused(TriggerKey triggerKey) {
		
		
	}
	@Override
	public void triggersPaused(String triggerGroup) {
		
		
	}
	@Override
	public void triggerResumed(TriggerKey triggerKey) {
		
		
	}
	@Override
	public void triggersResumed(String triggerGroup) {
		
		
	}
	@Override
	public void jobAdded(JobDetail jobDetail) {
		
		
	}
	@Override
	public void jobDeleted(JobKey jobKey) {
		
		
	}
	@Override
	public void jobPaused(JobKey jobKey) {
		
		
	}
	@Override
	public void jobsPaused(String jobGroup) {
		
		
	}
	@Override
	public void jobResumed(JobKey jobKey) {
		
		
	}
	@Override
	public void jobsResumed(String jobGroup) {
		
		
	}
	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		result.scheduleErrorTimes.add(new Object[]{ new Date(), msg, cause });
	}
	@Override
	public void schedulerInStandbyMode() {
		
		
	}
	@Override
	public void schedulerStarted() {
		
		
	}
	@Override
	public void schedulerShutdown() {
		
		
	}
	@Override
	public void schedulerShuttingdown() {
		
		
	}
	@Override
	public void schedulingDataCleared() {
		
		
	}
	
}
