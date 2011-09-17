package myschedule.service;


/**
 * A scheduler service that contains its config and the actual underlying scheduler.
 * 
 * @param T the underlying scheduler type.
 * 
 * @author Zemian Deng
 */
public interface SchedulerService<T> extends Service {

	SchedulerConfig getSchedulerConfig();
	
	T getScheduler();
		
}