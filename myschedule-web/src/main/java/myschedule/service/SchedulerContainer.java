package myschedule.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A holder of map for all configIds to SchedulerServices.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class SchedulerContainer extends ServiceContainer {
	
	// A map of key=configId, value=SchedulerService 
	private Map<String, SchedulerService> schedulerServices = new HashMap<String, SchedulerService>();
	private ConfigStore configStore;
	
	public SchedulerContainer() {
		// Always allow all schedulers to continue initializing even if one failed.
		setIgnoreInitException(true);
	}

	public void setConfigStore(ConfigStore configStore) {
		this.configStore = configStore;
	}
	
	public SchedulerService getSchedulerService(String configId) {
		return schedulerServices.get(configId);
	}
	
	public Set<String> getAllConfigIds() {
		return schedulerServices.keySet();
	}
	
	public String getSchedulerConfig(String configId) {
		return configStore.get(configId);
	}
	
	public String createScheduler(String configText) {
		String configId = configStore.create(configText);
		SchedulerService schedulerService = new SchedulerService(configId, configStore);
		addService(schedulerService);
		logger.info("Scheduler {} added.", schedulerService.getScheduler());
		return configId;
	}
	
	public void modifyScheduler(String configId, String configText) {
		configStore.update(configId, configText);
	}
	
	public void initScheduler(String configId) {
		SchedulerService schedulerService = schedulerServices.get(configId);
		
		// Init and start service
		try {
			schedulerService.init();
			schedulerService.start();
			logger.info("Scheduler {} started.", schedulerService.getScheduler());
		} catch (RuntimeException e) {
			logger.error("Failed to init and start scheduler service.", e);
		}
	}
	
	public void deleteScheduler(String configId) {
		SchedulerService schedulerService = schedulerServices.get(configId);
		
		// Stopping scheduler
		if (schedulerService.isInited()) {
			schedulerService.stop();
			schedulerService.destroy();
			logger.info("Scheduler {} destroyed.", schedulerService.getScheduler());
		}		
		configStore.delete(configId);
		schedulerServices.remove(configId);
		logger.info("Scheduler configId {} deleted.", configId);
	}
	
	public SchedulerService findFirstInitedScheduler() {
		SchedulerService scheduler = null;
		for (Map.Entry<String, SchedulerService> entry : schedulerServices.entrySet()) {
			scheduler = entry.getValue();
			if (scheduler.isInited()) {
				return scheduler;
			}
		}
		
		throw new ErrorCodeException(ErrorCode.SERVICE_PROBLEM, "No scheduler services have been initialized yet.");
	}

	@Override
	protected void initService() {
		// Read config store and load all scheduler config and add new SchedulerService to be managed.
		Set<String> configIds = configStore.getAllIds();
		for (String configId : configIds) {
			SchedulerService schedulerService = new SchedulerService(configId, configStore);
			addService(schedulerService);
		}
		
		// Ensure parent service is initialized.
		super.initService();
	}
}
