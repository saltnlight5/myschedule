package myschedule.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This is a Spring lifecycles awared service that will auto populate and manage all Initable/Service
 * instances lifecycles found in the Spring context. 
 * 
 * @author Zemian Deng
 */
public class SpringServiceContainer implements ApplicationContextAware, InitializingBean, DisposableBean {
	
	private static final Logger logger = LoggerFactory.getLogger(SpringServiceContainer.class);
	
	protected ServiceContainer serviceContainer = new ServiceContainer();
	
	@Override
	public void destroy() {
		serviceContainer.stop();
		serviceContainer.destroy();
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		serviceContainer.init();
		serviceContainer.start();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// Find all the Initable/Service bean instances.
		Map<String, Initable> serviceBeans = applicationContext.getBeansOfType(Initable.class);
		logger.debug("Found " + serviceBeans.size() + " instances of Initable or Service beans.");
		for (Map.Entry<String, Initable> entry : serviceBeans.entrySet()) {
			String beanId = entry.getKey();
			Initable service = entry.getValue();
			logger.debug("Adding beanId {} to service container.", beanId);
			serviceContainer.addService(service);
		}
		logger.info("Service container has been pupolated.");
		
		// Let's find all SchedulerService beans and register them to SchedulerServiceRepository
		SchedulerServiceRepository repo = SchedulerServiceRepository.getInstance();
		@SuppressWarnings("rawtypes")
		Map<String, SchedulerService> schedulerServiceBeans = applicationContext.getBeansOfType(SchedulerService.class);
		for (@SuppressWarnings("rawtypes") Map.Entry<String, SchedulerService> entry : schedulerServiceBeans.entrySet()) {
			String beanId = entry.getKey();
			SchedulerService<?> schedulerService = entry.getValue();
			repo.addSchedulerService(schedulerService);
			logger.debug("Registering beanId {} to scheduler repository.", beanId);
		}
	}
}
