package myschedule.service;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerConfigService extends AbstractService {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerConfigService.class);
	
	protected SchedulerConfigDao schedulerConfigDao;
	protected Set<String> schedulerServiceNames; // Ones loaded by this service only.
	protected SchedulerServiceRepository schedulerServiceRepo = SchedulerServiceRepository.getInstance();
	
	public Set<String> getSchedulerServiceNames() {
		return schedulerServiceNames;
	}
	
	public String getSchedulerConfigPropsText(String schedulerServiceName) {
		SchedulerConfig schedulerConfig = schedulerConfigDao.load(schedulerServiceName);
		return schedulerConfig.getConfigPropsText();
	}
	
	public SchedulerService<?> modifySchedulerService(String schedulerServiceName, String newConfigPropsText) {
		logger.debug("Stopping scheduler service {} before update config.", schedulerServiceName);
		QuartzSchedulerService schedulerService = (QuartzSchedulerService)schedulerServiceRepo.getSchedulerService(schedulerServiceName);
		schedulerService.stop();
		schedulerService.destroy();
		
		logger.debug("Updating scheduler service {} config.", schedulerServiceName);
		SchedulerConfig sc = schedulerConfigDao.load(schedulerServiceName);
		sc.setConfigPropsText(newConfigPropsText);
		schedulerConfigDao.update(sc);
		schedulerService.setSchedulerConfig(sc);
		logger.info("Scheduler service {} config updated.", schedulerServiceName);
		
		try {
			schedulerService.init();
			schedulerService.start();
		} catch (RuntimeException e) {
			logger.error("Failed to initialize and start scheduler service {}.", schedulerServiceName, e);
		}
		
		return schedulerService;
	}
	
	public SchedulerService<?> createSchedulerService(String configPropsText) {
		String configId = generateConfigId();
		SchedulerConfig schedulerConfig = new SchedulerConfig(configId, configPropsText);
		QuartzSchedulerService schedulerService = new QuartzSchedulerService(schedulerConfig);
		addSchedulerService(schedulerService);
		try {
			schedulerService.init();
			schedulerService.start();
		} catch (RuntimeException e) {
			logger.error("Failed to initialize and start scheduler service {}.", configId, e);
		}
		return schedulerService;
	}
	
	public void removeSchedulerService(String schedulerServiceName) {
		logger.debug("Removing scheduler service {} has been removed from repository.", schedulerServiceName);
		SchedulerService<?> schedulerService = schedulerServiceRepo.removeSchedulerService(schedulerServiceName);
		schedulerService.stop();
		schedulerService.destroy();
		
		schedulerServiceNames.remove(schedulerServiceName);
		logger.info("Scheduler service {} has been removed from repository.", schedulerServiceName);
	}
	
	protected String generateConfigId() {
		return UUID.randomUUID().toString();
	}
	
	protected void addSchedulerService(SchedulerService<?> schedulerService) {
		String schedulerServiceName = schedulerService.getServiceName();
		schedulerServiceNames.add(schedulerServiceName);
		schedulerServiceRepo.addSchedulerService(schedulerServiceName, schedulerService);
		logger.info("Scheduler service {} has been added into the repository.", schedulerServiceName);	
	}

	protected void loadStoredSchedulerServices() {
		logger.debug("Loading scheduler services from DAO into repository.");
		Collection<String> configIds = schedulerConfigDao.getAllSchedulerConfigIds();
		for (String configId : configIds) {
			SchedulerConfig schedulerConfig = schedulerConfigDao.load(configId);
			QuartzSchedulerService schedulerService = new QuartzSchedulerService(schedulerConfig);
			addSchedulerService(schedulerService);
		}
	}
	
	@Override
	protected void initService() {
		// Load and init all stored scheduler services first.
		loadStoredSchedulerServices();
		
		// Now initialize these loaded scheduler services
		for (String schedulerServiceName : schedulerServiceNames) {
			SchedulerService<?> schedulerService = schedulerServiceRepo.getSchedulerService(schedulerServiceName);
			try {
				schedulerService.init();
				schedulerService.start();
			} catch (RuntimeException e) {
				logger.error("Failed to initialize and start scheduler service {}.", schedulerServiceName, e);
			}
		}
	}
	
	@Override
	protected void destroyService() {		
		// Stop and destroy loaded scheduler services
		for (String schedulerServiceName : schedulerServiceNames) {
			SchedulerService<?> schedulerService = schedulerServiceRepo.getSchedulerService(schedulerServiceName);
			try {
				schedulerService.stop();
				schedulerService.destroy();
			} catch (RuntimeException e) {
				logger.error("Failed to stop and destroy scheduler service {}.", schedulerServiceName, e);
			}
		}		
	}
}
