package myschedule.service;

/**
 * Lifecycle for init/destroy and start/stop a service.
 *
 * <p>
 * A Service may be use the {@link ServiceContainer} to auto manage the lifecycle invocations.
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
