package unit.myschedule.quartz.extra;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobUnscheduled(String triggerName, String triggerGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void triggerFinalized(Trigger trigger) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void triggersPaused(String triggerName, String triggerGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void triggersResumed(String triggerName, String triggerGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobAdded(JobDetail jobDetail) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobDeleted(String jobName, String groupName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobsPaused(String jobName, String jobGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobsResumed(String jobName, String jobGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		result.scheduleErrorTimes.add(new Object[]{ new Date(), msg, cause });
	}

	@Override
	public void schedulerInStandbyMode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerShutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerShuttingdown() {
		// TODO Auto-generated method stub
		
	}

}
