package myschedule.web;

import myschedule.service.AbstractService;
import myschedule.service.ServiceContainer;

/**
 * This is a singleton space to hold global application data.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class Application extends AbstractService {
	// This class singleton instance access
	// ====================================
	protected static Application instance;
	
	protected Application() {
	}
	
	synchronized public static Application getInstance() {
		if (instance == null) {
			instance = new Application();
		}
		return instance;
	}
	
	// Application static configuration
	// ================================
	protected ServiceContainer serviceContainer = new ServiceContainer();
	
	@Override
	protected void startService() {
		serviceContainer.start();
	}
	
	@Override
	protected void stopService() {
		serviceContainer.stop();
	}
}
