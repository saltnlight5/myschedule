package myschedule.service;

import java.util.List;


public interface SchedulerServiceDao extends Service {

	void saveSchedulerService(SchedulerService schedulerService, String origConfigPropsText, boolean update);
	
	void deleteSchedulerService(String schedulerServiceName);

	String getConfigPropsText(String schedulerServiceName);
	
	SchedulerService getSchedulerService(String schedulerServiceName);
	
	boolean hasSchedulerService(String schedulerServiceName);
	
	List<String> getSchedulerServiceNames();
}