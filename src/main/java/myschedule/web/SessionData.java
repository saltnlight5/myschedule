package myschedule.web;



public class SessionData {
	protected String currentMenu;
	protected String currentSubMenu;
	protected String currentSchedulerName;
	
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
	
	public void setCurrentSchedulerName(String currentSchedulerName) {
		this.currentSchedulerName = currentSchedulerName;
	}
	
	public String getCurrentSchedulerName() {
		return currentSchedulerName;
	}
}
