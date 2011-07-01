package myschedule.service;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;

public interface SchedulerService {

	boolean isAutoStart();

	boolean isWaitForJobsToComplete();

	// Scheduler service methods
	// =========================
	Scheduler getUnderlyingScheduler();

	String getName();

	SchedulerMetaData getSchedulerMetaData();

	/** Get all the JobDetails in the scheduler. */
	List<JobDetail> getJobDetails();

	/** Get all the triggers in the scheduler. */
	List<Trigger> getTriggers(JobDetail jobDetail);

	JobDetail getJobDetail(String jobName, String jobGroup);

	Trigger getTrigger(String triggerName, String triggerGroup);

	/**
	 * Get a list of next fire time dates up to maxCount time. If next fire time needed
	 * before maxCount, then there should be a null object in the last element of the list.
	 */
	List<Date> getNextFireTimes(Trigger trigger, Date startTime, int maxCount);

	/** Add a JobDetail with a trigger schedule when to fire. */
	Date scheduleJob(JobDetail jobDetail, Trigger trigger);

	/** Schedule new trigger to an existing JobDetail. You need to set "trigger.setJobName()". */
	void scheduleJob(Trigger trigger);

	/** Remove a trigger and its JobDetail if it's not set durable. */
	Trigger uncheduleJob(String triggerName, String triggerGroup);

	/** Remove a JobDetail and all the triggers associated with it. */
	List<Trigger> deleteJob(String jobName, String jobGroup);

	/**
	 * Load job scheduling data xml using XMLSchedulingDataProcessor.
	 * @param xml
	 * @return XMLSchedulingDataProcessor instance will contain all the jobs parse in xml.
	 */
	XmlJobLoader loadJobs(String xml);
	
	Properties getConfigProps();
	
	String getSchedulerName();

	boolean isRemote();

	void pause();

	void resume();

	void start();

	void shutdown();

	void init();

	void destroy();
	
	boolean isJobRunning();

}