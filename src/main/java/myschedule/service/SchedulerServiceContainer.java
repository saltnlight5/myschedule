package myschedule.service;

import static myschedule.service.ErrorCode.SCHEDULER_SERIVCE_NOT_FOUND;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
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
	
	public List<String> getInitializedSchedulerServiceNames() {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> names = new ArrayList<String>(schedulerServiceMap.keySet());
		for (String name : names) {
			SchedulerService sservice = getSchedulerService(name);
			if (sservice.isInitialized())
				result.add(name);
		}
		return result;
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
		// Quartz oddity: we can not obtain scheduler name without first initialize it first!
		synchronized(this) {
			try {
				// Init scheduler now.
				schedulerService.init();
				String schedulerServiceName = schedulerService.getName();
							
				// Ensure we do not crash with other existing scheduler name.
				if (hasSchedulerService(schedulerServiceName)) {
					shutdownNonRemoteSchedulerService(schedulerService); // Now we need proper shutdown before destroy.
					schedulerService.destroy();
					throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, "SchedulerService " + schedulerServiceName + " already exists.");
				}
	
				schedulerServiceMap.put(schedulerServiceName, schedulerService);
				logger.info("SchedulerService " + schedulerServiceName + " is initialized and added to container.");
			} catch (Exception e) {
				String schedulerServiceName = schedulerService.getConfigSchedulerName();
				
				// Ensure we have a name.
				if (schedulerServiceName == null)
					schedulerServiceName = "FAILED_INIT_" + UUID.randomUUID().toString();

				logger.info("Failed to initialize scheduler service " + schedulerService + 
						". We will name=" + schedulerServiceName + " to store this scheduler service instance.");				
				schedulerServiceMap.put(schedulerServiceName, schedulerService);
				
				throw new ErrorCodeException(e);
			}
		}
	}
	
	public void removeAndDestroySchedulerService(String schedulerServiceName) {
		synchronized(this) {
			SchedulerService schedulerService = getSchedulerService(schedulerServiceName);
			if (schedulerService.isInitialized()) {
				shutdownNonRemoteSchedulerService(schedulerService);
				schedulerService.destroy();
			}
			schedulerServiceMap.remove(schedulerServiceName);
			logger.info("SchedulerService " + schedulerServiceName + " is destroyed and removed from container.");
		}
	}
	
	protected boolean shutdownNonRemoteSchedulerService(SchedulerService schedulerService) {
		if (schedulerService.isRemote()) {
			logger.info("Auto shutdown on remote scheduler is dangerous and will be skipped automatically.");
			return false;
		}
		
		schedulerService.shutdown();
		logger.info("Scheduler service " + schedulerService.getName() + " is shutdown.");
		return true;		
	}
	
	protected void loadStoredSchedulerServices() {
		logger.info("Loading scheduler services from stored config props.");
		List<String> names = schedulerServiceDao.getSchedulerServiceNames();
		int initCount = 0;
		for (String name : names) {
			SchedulerService schedulerService = schedulerServiceDao.getSchedulerService(name);
			try {
				addAndInitSchedulerService(schedulerService);
				initCount++;
			} catch (Exception e) {
				logger.error("Failed to initialize SchedulerService " + name, e);
			}
		}
		logger.info(initCount + "/" + names.size() + " scheduler services initialized from stored config props.");
	}

	public void init() {
		// Load and init all stored scheduler services first.
		loadStoredSchedulerServices();
		
		// Now initialize all pre-configured scheduler services
		int initCount = 0;
		for (SchedulerService schedulerService : initSchedulerServices) {			
			try {
				addAndInitSchedulerService(schedulerService);
				initCount++;
			} catch (Exception e) {
				logger.error("Failed to initialize service " + schedulerService, e);
			}
		}
		logger.info(initCount + "/" + initSchedulerServices.size() + " pre-configured SchedulerService intialized.");

		// Auto start all scheduler service if possible
		int startCount = 0;
		for (Map.Entry<String, SchedulerService> schedulerServiceEntry : schedulerServiceMap.entrySet()) {
			String schedulerServiceName = schedulerServiceEntry.getKey();
			SchedulerService schedulerService = schedulerServiceEntry.getValue();
			// Auto start non-remote scheduler.
			if (schedulerService.isAutoStart()) {
				if (schedulerService.isRemote()) {
					logger.info("Auto start on remote scheduler is dangerous and will be skipped automatically.");
				} else {
					schedulerService.start();
					startCount++;
					logger.info("Scheduler service " + schedulerServiceName + " auto started.");
				}
			}
		}
		logger.info(startCount + "/" + schedulerServiceMap.size() + " SchedulerService started.");
	}
	
	@Override
	public void destroy() {		
		// Auto shutdown scheduler service if possible
		int shutdownCount = 0;
		for (Map.Entry<String, SchedulerService> schedulerServiceEntry : schedulerServiceMap.entrySet()) {
			//String schedulerServiceName = schedulerServiceEntry.getKey();
			SchedulerService schedulerService = schedulerServiceEntry.getValue();
			
			// Auto shutdown non-remote scheduler.
			if (shutdownNonRemoteSchedulerService(schedulerService)) {
				shutdownCount++;				
			}
		}
		logger.info(shutdownCount + "/" + schedulerServiceMap.size() + " SchedulerService shutdown.");
		
		// Destroying services and remove from the container map.
		int destroyCount = 0;
		int totalCount = schedulerServiceMap.size();
		Iterator<Entry<String, SchedulerService>> entryIterator = schedulerServiceMap.entrySet().iterator();
		while (entryIterator.hasNext()) {
			Map.Entry<String, SchedulerService> schedulerServiceEntry = entryIterator.next();
			String schedulerServiceName = schedulerServiceEntry.getKey();
			SchedulerService schedulerService = schedulerServiceEntry.getValue();
			try {
				schedulerService.destroy();
				entryIterator.remove();
				destroyCount++;
				logger.info("SchedulerService " + schedulerServiceName + " is destroyed and removed from container.");
			} catch (Exception e) {
				logger.error("Failed to destroy service " + schedulerServiceName, e);
			}
		}
		logger.info(destroyCount + "/" + totalCount + " SchedulerService destroyed.");		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, SchedulerService> serviceBeans = applicationContext.getBeansOfType(SchedulerService.class);
		initSchedulerServices = new ArrayList<SchedulerService>(serviceBeans.values());
		logger.info("Spring context initialized, detected " + initSchedulerServices.size() + " pre-configured SchedulerService instances.");
	}

}
