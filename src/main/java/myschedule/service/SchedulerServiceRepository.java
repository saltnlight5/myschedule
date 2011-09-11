package myschedule.service;

import static myschedule.service.ErrorCode.SCHEDULER_SERIVCE_NOT_FOUND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerServiceRepository {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceRepository.class);
	
	/** A map of scheduler service per its service name. This map is synchronized map. */
	protected Map<String, SchedulerService<?>> schedulerServiceMap = 
			Collections.synchronizedMap(new HashMap<String, SchedulerService<?>>());
	
	protected static SchedulerServiceRepository instance;
	
	private SchedulerServiceRepository() {
	}
	
	synchronized public static SchedulerServiceRepository getInstance() {
		if (instance == null)
			instance = new SchedulerServiceRepository();
		return instance;		
	}
	
	public List<String> getSchedulerServiceConfigIds() {
		List<String> names = new ArrayList<String>(schedulerServiceMap.keySet());
		Collections.sort(names); // Sorted so that end user may consistently get the first service.
		return names;
	}
	
	public boolean hasSchedulerService(String configId) {
		return schedulerServiceMap.containsKey(configId);
	}
	
	public SchedulerService<?> getSchedulerService(String configId) {
		if (!schedulerServiceMap.containsKey(configId)) {
			throw new ErrorCodeException(SCHEDULER_SERIVCE_NOT_FOUND, "SchedulerService " + configId + " not found in repository.");
		}
		return schedulerServiceMap.get(configId);
	}
	
	public void addSchedulerService(SchedulerService<?> schedulerService) {
		String configId = schedulerService.getSchedulerConfig().getConfigId();
		schedulerServiceMap.put(configId, schedulerService);
		logger.info("SchedulerService {} has been added to service repository.", configId);
	}
	
	public SchedulerService<?> removeSchedulerService(String configId) {
		SchedulerService<?> schedulerService = getSchedulerService(configId);
		schedulerServiceMap.remove(configId);
		logger.info("SchedulerService " + configId + " has removed from repository.");
		return schedulerService;
	}
	
	// For convenient sake, we have a method to return Quartz impl to avoid cast everywhere.
	public QuartzSchedulerService getQuartzSchedulerService(String configId) {
		return (QuartzSchedulerService)getSchedulerService(configId);
	}
}
