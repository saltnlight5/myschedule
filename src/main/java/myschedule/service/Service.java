package myschedule.service;

/**
 * Lifecycle for starting and stopping a service.
 *
 * @author Zemian Deng
 */
public interface Service {
	
	public void init();
	public void destroy();
	
}
