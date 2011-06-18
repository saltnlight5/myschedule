package myschedule.spring;

import java.util.Map;

import myschedule.service.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A service container that extends Spring functionality to detect any {@link Service}
 * instances in the Spring context and init/destroy them automatically.
 * 
 * @author Zemian Deng
 */
public class ServiceContainer implements InitializingBean, DisposableBean, ApplicationContextAware {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected ApplicationContext applicationContext;
	
	protected Map<String, Service> getServices() {
		Map<String, Service> services = applicationContext.getBeansOfType(Service.class);
		return services;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, Service> services = getServices();
		for (Map.Entry<String, Service> entry : services.entrySet()) {
			try {
				entry.getValue().init();
				logger.info("Service " + entry.getKey() + " initialized.");
			} catch (Exception e) {
				logger.error("Failed to initialize service " + entry.getKey(), e);
			}
		}
	}
	
	@Override
	public void destroy() throws Exception {
		Map<String, Service> services = getServices();
		for (Map.Entry<String, Service> entry : services.entrySet()) {
			try {
				entry.getValue().destroy();
				logger.info("Service " + entry.getKey() + " destroyed.");
			} catch (Exception e) {
				logger.error("Failed to destroy service " + entry.getKey(), e);
			}
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
