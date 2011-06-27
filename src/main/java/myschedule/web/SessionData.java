package myschedule.web;

import myschedule.service.SchedulerServiceRepository;


public class SessionData {
	protected String currentMenu;
	protected String currentSubMenu;
	protected String currentSchedulerName;
	protected transient SchedulerServiceRepository schedulerServiceRepository = SchedulerServiceRepository.getInstance();
	
	public String getCurrentMenu() {
		return currentMenu;
	}

	public void setCurrentMenu(String currentMenu) {
		this.currentMenu = currentMenu;
	}

	public String getCurrentSubMenu() {
		return currentSubMenu;
	}

	public void setCurrentSubMenu(String currentSubMenu) {
		this.currentSubMenu = currentSubMenu;
	}

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
