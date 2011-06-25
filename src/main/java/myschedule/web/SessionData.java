package myschedule.web;

import myschedule.service.SchedulerServiceRepository;


public class SessionData {
	protected String currentSchedulerName;
	protected SchedulerServiceRepository schedulerServiceRepository = SchedulerServiceRepository.getInstance();

	public SchedulerServiceRepository getSchedulerServiceRepository() {
		return schedulerServiceRepository;
	}
	
	public void setCurrentSchedulerName(String currentSchedulerName) {
		this.currentSchedulerName = currentSchedulerName;
	}
	
	public String getCurrentSchedulerName() {
		return currentSchedulerName;
	}
}
