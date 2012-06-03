package myschedule.service;

/**
 * A service that provide init/start/stop/destroy interface.
 *
 * <p>
 * Use {@link ServiceContainer} to auto manage the lifecycles.
 * 
 * @author Zemian Deng
 */
public interface Service extends Initable {	
	void start();	
	void stop();
	boolean isStarted();	
}
