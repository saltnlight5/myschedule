package myschedule.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
		verifyScheudlerNameIsUnique(configText);
		
		String configId = configStore.create(configText);
		SchedulerService schedulerService = new SchedulerService(configId, configStore);
		schedulerServices.put(configId, schedulerService);
		addService(schedulerService);
		logger.info("Scheduler service {} added.", schedulerService);
		
		schedulerService.init();
		schedulerService.start();
		logger.info("Scheduler service {} auto initialized and started.", schedulerService);
		
		return configId;
	}
	
	private void verifyScheudlerNameIsUnique(String configText) {
		Properties props = ServiceUtils.textToProps(configText);
		String isClusteredStr = props.getProperty("org.quartz.jobStore.isClustered", "false");
		boolean isClustered = Boolean.parseBoolean(isClusteredStr);
		// We only worry non-clustered schedulers because their name must be unique in JVM.
		if (isClustered) {
			return;
		}

		String schedulerName = props.getProperty("org.quartz.scheduler.instanceName", "QuartzScheduler");
		logger.debug("Verifying unique scheduler name {}", schedulerName);
		for (String configId : configStore.getAllIds()) {
			String text = configStore.get(configId);
			Properties configProps = ServiceUtils.textToProps(text);
			String existingName = configProps.getProperty("org.quartz.scheduler.instanceName", "QuartzScheduler");
			if (schedulerName.equals(existingName)) {
				throw new ErrorCodeException(ErrorCode.SCHEDULER_PROBLEM, "A non-clustered scheduler name " + 
						schedulerName + " already exists.");
			}
		}
	}

	public void modifyScheduler(String configId, String configText) {
		SchedulerService schedulerService = schedulerServices.get(configId);
		
		schedulerService.stop();
		schedulerService.destroy();
		logger.info("scheduler service {} shutdown for configuration modification.", schedulerService);
		
		configStore.update(configId, configText);
		logger.info("ConfigId {} modified", configId, schedulerService);
		
		schedulerService.loadConfig(); // Ensure to reload all the auto fields!
		schedulerService.init();
		schedulerService.start();
		logger.info("Scheduler service {} started after configuration modification.", schedulerService);
	}
	
	public void deleteScheduler(String configId) {
		SchedulerService schedulerService = schedulerServices.get(configId);
		
		// Stopping scheduler
		if (schedulerService.isSchedulerInitialized()) {
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
			if (scheduler.isSchedulerInitialized()) {
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
			schedulerServices.put(configId, schedulerService);
			logger.info("Scheduler service {} added.", schedulerService);
		}
		
		// Ensure parent service is initialized.
		super.initService();
	}
}
