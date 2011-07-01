package myschedule.service;

import static myschedule.service.ErrorCode.SCHEDULER_SERIVCE_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SchedulerServiceContainer implements ApplicationContextAware, InitializingBean, DisposableBean {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected SchedulerServiceDao schedulerServiceDao;

	/** Temp, initial list of SchedulerServices, it will be set null after this service init is completed.*/
	protected List<SchedulerService> initSchedulerServices;
	
	/** Actual storage of scheduler services in this container. Key = Unique name per SchedulerService (scheduler name). We use TreeMap to keep key sorted. */
	protected Map<String, SchedulerService> schedulerServiceMap = new TreeMap<String, SchedulerService>();
	
	public void setSchedulerServiceDao(SchedulerServiceDao schedulerServiceDao) {
		this.schedulerServiceDao = schedulerServiceDao;
	}
	
	public List<String> getSchedulerServiceNames() {
		return new ArrayList<String>(schedulerServiceMap.keySet());
	}
	
	public boolean hasSchedulerService(String schedulerServiceName) {
		return schedulerServiceMap.containsKey(schedulerServiceName);
	}
	
	public SchedulerService getSchedulerService(String schedulerServiceName) {
		if (!schedulerServiceMap.containsKey(schedulerServiceName)) {
			throw new ErrorCodeException(SCHEDULER_SERIVCE_NOT_FOUND, "SchedulerService " + schedulerServiceName + " not found in repository.");
		}
		return schedulerServiceMap.get(schedulerServiceName);
	}

	public void addAndInitSchedulerService(SchedulerService schedulerService) {
		synchronized(this) {
			// If user didn't provide "name", then we will attempt to initialize to the scheduler name.
			boolean initialized = false;
			String name = schedulerService.getName();
			if (name == null) {
				schedulerService.init(); // Since name is null, this will auto initialize the name!
				initialized = true;
				name = schedulerService.getName();
			}
			
			// Ensure we do not crash with other existing scheduler name.
			if (hasSchedulerService(name)) {
				if (initialized)
					schedulerService.destroy();
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, "SchedulerService " + name + " already exists.");
			}
			
			// Let's initialize the scheduler now.
			if (!initialized)
				schedulerService.init();
	
			// Add to container map.
			schedulerServiceMap.put(name, schedulerService);
			logger.info("SchedulerService " + schedulerService + " initialized and added to container.");
		}
	}
	
	public void removeAndDestroySchedulerService(String schedulerServiceName) {
		synchronized(this) {
			SchedulerService schedulerService = getSchedulerService(schedulerServiceName);
			schedulerService.destroy();
		}
	}
	
	protected void loadStoredSchedulerServices() {
		List<String> names = schedulerServiceDao.getSchedulerServiceNames();
		for (String name : names) {
			SchedulerService schedulerService = schedulerServiceDao.getSchedulerService(name);
			try {
				addAndInitSchedulerService(schedulerService);
			} catch (Exception e) {
				destroy(); // Let's cleanup/destroy what's initialized so far, and then throw exception up.
				throw new ErrorCodeException(ErrorCode.SERVICE_PROBLEM, 
						"Failed to initialize service " + schedulerService +	". Previous initialized services are destroyed.", e);
			}
		}
	}

	public void init() {
		// Load all stored scheduler services first.
		loadStoredSchedulerServices();
		
		// Initialize all scheduler services
		for (SchedulerService schedulerService : initSchedulerServices) {			
			try {
				addAndInitSchedulerService(schedulerService);
			} catch (Exception e) {
				destroy(); // Let's cleanup/destroy what's initialized so far, and then throw exception up.
				throw new ErrorCodeException(ErrorCode.SERVICE_PROBLEM, 
						"Failed to initialize service " + schedulerService +	". Previous initialized services are destroyed.", e);
			}
		}
	}
	
	@Override
	public void destroy() {
		// Destroying services from the map
		for (Map.Entry<String, SchedulerService> schedulerServiceEntry : schedulerServiceMap.entrySet()) {
			String schedulerServiceName = schedulerServiceEntry.getKey();
			SchedulerService schedulerService = schedulerServiceEntry.getValue();
			try {
				schedulerService.destroy();
				schedulerServiceMap.remove(schedulerServiceName);
				logger.info("Service " + schedulerServiceName + " destroyed and removed from container.");
			} catch (Exception e) {
				logger.error("Failed to destroy service " + schedulerServiceName, e);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, SchedulerService> serviceBeans = applicationContext.getBeansOfType(SchedulerService.class);
		initSchedulerServices = new ArrayList<SchedulerService>(serviceBeans.values());
		logger.info("Spring context initialized, detected " + initSchedulerServices.size() + " SchedulerService instances.");
	}

}
