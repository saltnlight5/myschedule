package myschedule.service;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;

/**
 * Lifecycle for a scheduler service. This is not the same as the {@link Service} due to special
 * needs of the scheduler.
 *
 * <p>
 * A SchedulerService may be use the {@link SchedulerServiceContainer} to auto manage the lifecycle invocations.
 * 
 * @author Zemian Deng
 */
public interface SchedulerService {

	boolean isInitialized();
	
	String getConfigSchedulerName();
	
	boolean isAutoStart();

	boolean isWaitForJobsToComplete();

	Scheduler getUnderlyingScheduler();

	String getName();

	SchedulerMetaData getSchedulerMetaData();

	List<JobDetail> getJobDetails();

	List<Trigger> getTriggers(JobDetail jobDetail);

	JobDetail getJobDetail(String jobName, String jobGroup);

	Trigger getTrigger(String triggerName, String triggerGroup);

	List<Date> getNextFireTimes(Trigger trigger, Date startTime, int maxCount);

	Date scheduleJob(JobDetail jobDetail, Trigger trigger);

	void scheduleJob(Trigger trigger);

	Trigger uncheduleJob(String triggerName, String triggerGroup);

	List<Trigger> deleteJob(String jobName, String jobGroup);
	
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
	
	boolean isStarted();

	boolean isPaused();

}