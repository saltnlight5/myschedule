package myschedule.service;

import java.util.ArrayList;
import java.util.List;

/**
 * This container auto manage Initable or Service instance's lifecycle method calling together as part of Spring 
 * lifecycles. When Spring starts up, it calls all init(), then start(), and when Spring close, it calls all stop(), 
 * then destroy() in reverse order.
 * 
 * @author Zemian Deng
 */
public class ServiceContainer extends AbstractService {

	/** Service list that's registered in the application. */
	private List<Initable> services = new ArrayList<Initable>();	
	private boolean ignoreInitAndStartException = false;
	
	public void setIgnoreInitAndStartException(boolean ignoreInitAndStartException) {
		this.ignoreInitAndStartException = ignoreInitAndStartException;
	}
	
	public void setServices(List<Initable> services) {
		this.services = services;
	}
	
	public void addService(Initable service) {
		services.add(service);
	}
	
	@Override
	protected void initService() {
		for (Initable service : services) {
			logger.debug("Initializing service {}.", service);
			try {
				service.init();
			} catch (RuntimeException e) {
				if (ignoreInitAndStartException) {
					logger.error("Failed to initialize service. But will continue!", e);
				} else {
					throw e;
				}
			}
			logger.info("Service {} initialized.", service);
		}
	}
	
	@Override
	protected void startService() {
		for (Initable service : services) {
			if (service instanceof Service) {
				logger.debug("Starting service {}.", service);
				try {
					((Service)service).start();
				} catch (RuntimeException e) {
					if (ignoreInitAndStartException) {
						logger.error("Failed to stsart service. But will continue!", e);
					} else {
						throw e;
					}
				}
				logger.info("Service {} started.", service);
			}
		}
	}
	
	@Override
	protected void stopService() {
		// Stopping in reverse order.
		for (int i = services.size() - 1; i >= 0; i--) {
			Initable service = services.get(i);
			if (service instanceof Service) {
				logger.debug("Stopping service {}.", service);
				((Service)service).stop();
				logger.info("Service {} stopped.", service);
			}
		}	
	}
	
	@Override
	protected void destroyService() {
		// Destroying in reverse order.
		for (int i = services.size() - 1; i >= 0; i--) {
			Initable service = services.get(i);
			logger.debug("Destroying service {}.", service);
			service.destroy();
			logger.info("Service {} destroyed.", service);
		}
	}
	
}
