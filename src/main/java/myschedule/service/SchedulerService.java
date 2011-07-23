package myschedule.service;

import java.util.Properties;

import org.quartz.Scheduler;

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
	
}