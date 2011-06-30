package myschedule.service;

import static myschedule.service.ErrorCode.SCHEDULER_SERIVCE_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A repository to create store, and cache SchedulerService by a unique name.
 * 
 * <p>The getInstance() is purposely non-synchronized for perf reason. The application
 * should call and init getInstance() once before web controllers (multi threads) calling
 * the getInstance() method. This should already be done init {@link SchedulerService#init()}.
 * 
 * @author Zemian Deng
 */
public class SchedulerServiceRepository {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected static SchedulerServiceRepository instance;
	
	/** Key = Unique name per SchedulerService. We use TreeMap to keep key sorted. */
	protected Map<String, SchedulerService> schedulerServices = new TreeMap<String, SchedulerService>();
	
	/** Private constructor to make this class singleton through getInstance(). */
	private SchedulerServiceRepository() {
	}
	
	public static SchedulerServiceRepository getInstance() {
		if (instance == null) {
			instance = new SchedulerServiceRepository();
			Logger logger2 = LoggerFactory.getLogger(SchedulerServiceRepository.class);
			logger2.debug("Instance created: " + instance);
		}
		return instance;
	}
	
	/** Return the sorted key (scheduler) names */
	public List<String> getNames() {
		return new ArrayList<String>(schedulerServices.keySet());
	}
	
	/** It will stop and destroy the scheduler service before removing! */
	public synchronized SchedulerService remove(String schedulerServiceName) {
		SchedulerService schedulerService = schedulerServices.remove(schedulerServiceName);
		logger.info(schedulerServiceName + " has successfully removed from repository.");		
		return schedulerService;
	}
	
	protected synchronized void add(String schedulerServiceName, SchedulerService schedulerService) {
		schedulerServices.put(schedulerServiceName, schedulerService);
		logger.info(schedulerServiceName + " added to repository.");
	}
	
	public SchedulerService getSchedulerService(String name) {
		if (!schedulerServices.containsKey(name)) {
			throw new ErrorCodeException(SCHEDULER_SERIVCE_NOT_FOUND, "SchedulerService " + name + " not found in repository.");
		}
		return schedulerServices.get(name);
	}

	public boolean hasSchedulerService(String name) {
		return schedulerServices.containsKey(name);
	}
}
