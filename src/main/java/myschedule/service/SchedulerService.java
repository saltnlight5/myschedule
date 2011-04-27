package myschedule.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.Scheduler;
import org.quartz.Trigger;

/**
 * Provide scheduling service to application using Quartz scheduler.
 *
 * @author Zemian Deng
 */
public class SchedulerService {
	
	private Scheduler scheduler;
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	/**
	 * Get all trigger names in a list that contains group and name pair of array object.
	 */
	public List<String[]> getTriggerNames()
	{
		try {
			List<String[]> list = new ArrayList<String[]>();
			String[] triggerGroups = scheduler.getTriggerGroupNames();
			for (String triggerGroup : triggerGroups) {
				String[] triggerNames = scheduler.getTriggerNames(triggerGroup);
				for (String triggerName : triggerNames) {
					String[] pair = new String[2];
					pair[0] = triggerName;
					pair[1] = triggerGroup;
					list.add(pair);
				}
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Get a list of next fire time dates up to maxCount time. If next fire time needed
	 * before maxCount, then there should be a null object in the last element of the list.
	 */
	public List<Date> getNextFireTimes(Trigger trigger, Date startTime, int maxCount)
	{	
		try {
			List<Date> list = new ArrayList<Date>();
			Date nextDate = startTime;
			int count = 0;
			while(count++ < maxCount) {
				Date fireTime = trigger.getFireTimeAfter(nextDate);
				list.add(fireTime);
				if (fireTime == null)
					break;
				nextDate = fireTime;
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Date> getNextFireTimes(String triggerName, String triggerGroup, Date startTime, int maxCount)
	{	
		try {
			Trigger trigger = scheduler.getTrigger(triggerName, triggerGroup);
			return getNextFireTimes(trigger, startTime, maxCount);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
