package myschedule.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
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

	void setConfigProps(Properties configProps);
	
	boolean isConfigModifiable();
	
	String getConfigSchedulerName();
	
	boolean isAutoStart();

	boolean isWaitForJobsToComplete();

	Scheduler getUnderlyingScheduler();

	String getName();

	List<JobExecutionContext> getCurrentlyExecutingJobs();
	
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

	void pause();
	
	void resume();

	void standby();

	void start();

	void shutdown();

	void init();

	void destroy();

	boolean isInit();
	
	boolean isPaused();

	boolean isRemote();
		
	boolean isStarted();

	boolean isStandby();

	boolean isShutdown();

	void runJob(String jobName, String jobGroup);
	
	void createGroovyScriptCronJob(String jobName, String cron, String script);
	
	void createCronJob(String jobName, String cron, Class<? extends Job> jobClass, Map<String, Object> data);
}