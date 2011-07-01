package myschedule.service;

import java.util.List;


public interface SchedulerServiceDao extends Service {

	void saveSchedulerService(SchedulerService schedulerService);

	void deleteSchedulerService(String schedulerServiceName);
	
	SchedulerService getSchedulerService(String schedulerServiceName);
	
	boolean hasSchedulerService(String schedulerServiceName);
	
	List<String> getSchedulerServiceNames();
}