package myschedule.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The schedulerServiceConfigIds hold by this class are only those that loaded through the 
 * DAO layer. The SchedulerServiceRepository might have more than these because Spring config
 * might have created a direct SchedulerService that registered there.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class SchedulerConfigService extends AbstractService {

	private static final String DEFAULT_QUARTZ_SCHEDULER_NAME = "QuartzScheduler";

	private static final Logger logger = LoggerFactory.getLogger(SchedulerConfigService.class);
	
	protected SchedulerConfigDao schedulerConfigDao;
	protected Set<String> daoConfigIds = new HashSet<String>(); // Ones loaded by this service only.
	protected SchedulerServiceRepository schedulerServiceRepo = SchedulerServiceRepository.getInstance();
	
	public void setSchedulerConfigDao(SchedulerConfigDao schedulerConfigDao) {
		this.schedulerConfigDao = schedulerConfigDao;
	}
	
	public List<String> getSchedulerServiceConfigIds() {
		List<String> configIds = new ArrayList<String>(daoConfigIds);
		Collections.sort(configIds);
		return configIds;
	}
	
	public String getSchedulerConfigPropsText(String configId) {
		SchedulerConfig schedulerConfig = schedulerConfigDao.load(configId);
		return schedulerConfig.getConfigPropsText();
	}
	
	public SchedulerService<?> modifySchedulerService(String configId, String newConfigPropsText) {
		logger.debug("Stopping scheduler service {} before update config.", configId);
		QuartzSchedulerService schedulerService = (QuartzSchedulerService)schedulerServiceRepo.getSchedulerService(configId);
		if (schedulerService.isInited()) {
			schedulerService.stop();
			schedulerService.destroy();
		}
		
		logger.debug("Updating scheduler service {} config.", configId);
		SchedulerConfig sc = schedulerConfigDao.load(configId);
		sc.setConfigPropsText(newConfigPropsText);
		schedulerConfigDao.update(sc);
		schedulerService.setSchedulerConfig(sc);
		logger.info("Scheduler service {} config updated.", configId);
		
		try {
			schedulerService.init();
			schedulerService.start();
		} catch (RuntimeException e) {
			logger.error("Failed to initialize and start scheduler service {}.", configId, e);
		}
		
		return schedulerService;
	}
	
	public SchedulerService<?> createSchedulerService(String configPropsText, ExceptionHolder exceptionHolder) {
		String configId = generateConfigId();
		logger.debug("Creating new schedulerConfig with id: {}", configId);
		
		SchedulerConfig schedulerConfig = new SchedulerConfig(configId, configPropsText);
		schedulerConfigDao.save(schedulerConfig);
		
		QuartzSchedulerService schedulerService = new QuartzSchedulerService(schedulerConfig);
		addSchedulerService(schedulerService);
		try {
			schedulerService.init();
			schedulerService.start();
		} catch (RuntimeException e) {
			logger.error("Failed to initialize and start scheduler service {}.", configId, e);

			// Set the caller's holder for exception object. 
			exceptionHolder.setException(e);
		}
		return schedulerService;
	}
	
	public void removeSchedulerService(String configId) {
		logger.debug("Removing scheduler service {} has been removed from repository.", configId);
		SchedulerService<?> schedulerService = schedulerServiceRepo.removeSchedulerService(configId);
		if (schedulerService.isInited()) {
			schedulerService.stop();
			schedulerService.destroy();
		}
		
		daoConfigIds.remove(configId);
		logger.info("Scheduler service {} has been removed from repository.", configId);
		
		schedulerConfigDao.delete(configId);
		logger.info("Scheduler service {} config has been removed from DAO.");
	}
	
	protected String generateConfigId() {
		return UUID.randomUUID().toString();
	}
	
	protected void addSchedulerService(SchedulerService<?> schedulerService) {
		String configId = schedulerService.getSchedulerConfig().getConfigId();
		daoConfigIds.add(configId);
		schedulerServiceRepo.addSchedulerService(schedulerService);
		logger.info("Scheduler service {} has been added into the repository.", configId);	
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
		for (String configId : daoConfigIds) {
			SchedulerService<?> schedulerService = schedulerServiceRepo.getSchedulerService(configId);
			try {
				schedulerService.init();
				schedulerService.start();
			} catch (RuntimeException e) {
				logger.error("Failed to initialize and start scheduler service {}.", configId, e);
			}
		}
	}
	
	@Override
	protected void destroyService() {		
		// Stop and destroy loaded scheduler services
		for (String configId : daoConfigIds) {
			SchedulerService<?> schedulerService = schedulerServiceRepo.getSchedulerService(configId);
			try {
				if (schedulerService.isInited()) {
					schedulerService.stop();
					schedulerService.destroy();
				}
			} catch (RuntimeException e) {
				logger.error("Failed to stop and destroy scheduler service {}.", configId, e);
			}
		}		
	}

	/**
	 * Attempt to get the name from the config if possible, else return configId value.
	 * @param configId
	 * @return scheduler name or configId
	 */
	public String getSchedulerNameFromConfigProps(String configId) {
		QuartzSchedulerService ss = schedulerServiceRepo.getQuartzSchedulerService(configId);
		if (ss.getSchedulerConfig().getConfigProps() != null) {
			String nameKey = StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME;
			return ss.getSchedulerConfig().getConfigProps().getProperty(nameKey, DEFAULT_QUARTZ_SCHEDULER_NAME);
		} else {
			return configId;
		}
	}
}
