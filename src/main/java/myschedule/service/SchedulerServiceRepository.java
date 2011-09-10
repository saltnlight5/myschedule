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
	
	public List<String> getSchedulerServiceNames() {
		List<String> names = new ArrayList<String>(schedulerServiceMap.keySet());
		Collections.sort(names); // Sorted so that end user may consistently get the first service.
		return names;
	}
	
	public boolean hasSchedulerService(String schedulerServiceName) {
		return schedulerServiceMap.containsKey(schedulerServiceName);
	}
	
	public SchedulerService<?> getSchedulerService(String schedulerServiceName) {
		if (!schedulerServiceMap.containsKey(schedulerServiceName)) {
			throw new ErrorCodeException(SCHEDULER_SERIVCE_NOT_FOUND, "SchedulerService " + schedulerServiceName + " not found in repository.");
		}
		return schedulerServiceMap.get(schedulerServiceName);
	}
	
	public void addSchedulerService(String schedulerServiceName, SchedulerService<?> schedulerService) {
		schedulerServiceMap.put(schedulerServiceName, schedulerService);
		logger.info("SchedulerService {} has been added to service repository.", schedulerServiceName);
	}
	
	public SchedulerService<?> removeSchedulerService(String schedulerServiceName) {
		SchedulerService<?> schedulerService = getSchedulerService(schedulerServiceName);
		schedulerServiceMap.remove(schedulerServiceName);
		logger.info("SchedulerService " + schedulerServiceName + " has removed from repository.");
		return schedulerService;
	}
	
}
