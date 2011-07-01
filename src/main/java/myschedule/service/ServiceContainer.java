package myschedule.service;

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
 * <p>This class implements Spring aware interfaces to auto detects {@link Service} by type
 * and process them.
 * 
 * @author Zemian Deng
 */
public class ServiceContainer implements ApplicationContextAware, InitializingBean, DisposableBean {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/** Service list that's registered in the application. */
	protected List<Service> services;
	
	/** Service list that's got initialized. */
	protected List<Service> initializedServices = new ArrayList<Service>();
	
	public void setServices(List<Service> services) {
		this.services = services;
	}
	
	public void init() {
		// Initialize all services
		for (Service service : services) {			
			try {
				service.init();
				initializedServices.add(service);
				logger.info("Service " + service + " initialized.");
			} catch (Exception e) {
				destroy();
				throw new ErrorCodeException(ErrorCode.SERVICE_PROBLEM, 
						"Failed to initialize service " + service +	". Previous initialized services are destroyed.", e);
			}
		}
	}
	
	@Override
	public void destroy() {
		// Destroying services in reverse order.
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
		init();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, Service> serviceBeans = applicationContext.getBeansOfType(Service.class);
		services = new ArrayList<Service>(serviceBeans.values());
		logger.info("Spring context initialized, detected " + services.size() + " Service instances.");
	}
}
