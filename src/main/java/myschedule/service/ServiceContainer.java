package myschedule.service;

import static myschedule.service.ErrorCode.SERVICE_PROBLEM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A service container that will auto init/destroy all of {@link Service} registered.
 * 
 * <p>We will implments Spring aware interfaces to auto detects {@link Service} by type
 * and process them.
 * 
 * @author Zemian Deng
 */
public class ServiceContainer implements Service, ApplicationContextAware, InitializingBean, DisposableBean {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected List<Service> services;
	
	protected boolean failFast = true;
	
	/** A list to hold services that has successfully initialized. */
	protected List<Service> initializedServices = new ArrayList<Service>();

	protected ApplicationContext applicationContext;
		
	public void setFailFast(boolean failFast) {
		this.failFast = failFast;
	}
	
	public void setServices(List<Service> services) {
		this.services = services;
	}
	
	public boolean isFailFast() {
		return failFast;
	}
	
	public List<Service> getInitializedServices() {
		return initializedServices;
	}
	
	public List<Service> getServices() {
		return services;
	}
	
	protected void initService(Service service) {		
		// Let's init it.
		try {
			service.init();
			initializedServices.add(service);
			logger.info("Service " + service + " initialized.");
		} catch (Exception e) {
			if (failFast) {
				destroy();
				throw new ErrorCodeException(SERVICE_PROBLEM, "Failed to initialize service " + service, e);
			} else {
				logger.error("Failed to initialize service " + service, e);
			}
		}
	}
	
	public void addAndInitService(Service service) {
		services.add(service);
		initService(service);
	}
	
	@Override
	public void init() {
		for (Service service : services) {
			// Ensured we don't init itself.
			if (service == this)
				continue;
			initService(service);
		}
	}
	
	@Override
	public void destroy() {
		// Destroy initialized services in reverse order
		for (int i = initializedServices.size() - 1; i > 0; i--) {
			Service service = initializedServices.get(i);
			try {
				service.destroy();
				logger.info("Service " + service + " destroyed.");
			} catch (Exception e) {
				logger.error("Failed to destroy service " + service, e);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, Service> serviceBeans = applicationContext.getBeansOfType(Service.class);
		services = new ArrayList<Service>(serviceBeans.values());
		init();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
