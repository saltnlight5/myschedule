package myschedule.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;

/**
 * Provide Quartz scheduling service to application.
 *
 * @author Zemian Deng
 */
public class SchedulerService {
	
	private Scheduler scheduler;
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public Date scheduleJob(JobDetail jobDetail, Trigger trigger) {
		try {
			return scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	public SchedulerMetaData getSchedulerMetaData() {
		try {
			return scheduler.getMetaData();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<JobDetail> getJobDetails() {
		try {
			List<JobDetail> jobs = new ArrayList<JobDetail>();
			String[] jobGroups = scheduler.getJobGroupNames();
			for (String jobGroup : jobGroups) {
				String[] jobNames = scheduler.getJobNames(jobGroup);
				for (String jobName : jobNames) {
					JobDetail jobDetail = scheduler.getJobDetail(jobName, jobGroup);
					jobs.add(jobDetail);
				}
			}
			return jobs;
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Trigger[] getTriggers(JobDetail jobDetail) {
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobDetail.getName(), jobDetail.getGroup());
			return triggers;
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JobDetail getJobDetail(String jobName, String jobGroup)
	{	
		try {
			return scheduler.getJobDetail(jobName, jobGroup);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Trigger getTrigger(String triggerName, String triggerGroup)
	{	
		try {
			return scheduler.getTrigger(triggerName, triggerGroup);
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

	/**
	 * Delete trigger and the job associated with it.
	 * @param name
	 * @param group
	 */
	public void deleteTrigger(String triggerName, String groupName) {
		try {
			scheduler.unscheduleJob(triggerName, groupName);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Delete the job.
	 * @param name
	 * @param group
	 */
	public void deleteJob(String jobName, String groupName) {
		try {
			scheduler.deleteJob(jobName, groupName);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Load job scheduling data xml using XMLSchedulingDataProcessor.
	 * @param xml
	 * @return XMLSchedulingDataProcessor instance will contain all the jobs parse in xml.
	 */
	public XmlJobLoader loadJobs(String xml) {
		try {
			XmlJobLoader xmlJobLoader = new XmlJobLoader();
			String systemId = null;
			InputStream istream = new ByteArrayInputStream(xml.getBytes());
			xmlJobLoader.processStreamAndScheduleJobs(istream, systemId, scheduler);
			return xmlJobLoader;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
