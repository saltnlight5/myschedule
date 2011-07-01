package myschedule.service;

/**
 * Lifecycle for starting and stopping a service.
 *
 * @author Zemian Deng
 */
public interface Service {
	
	void init();
	
	void destroy();
	
	void start();
	
	void stop();
	
	boolean isStarted();
	
	boolean isInitialized();
	
}
