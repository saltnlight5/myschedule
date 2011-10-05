package myschedule.web;



public class SessionData {
	
	protected String currentSchedulerName;
	protected String currentSchedulerConfigId;
	
	public void setCurrentSchedulerConfigId(String currentSchedulerConfigId) {
		this.currentSchedulerConfigId = currentSchedulerConfigId;
	}
	
	public String getCurrentSchedulerConfigId() {
		return currentSchedulerConfigId;
	}
	
	@Override
	public String toString() {
		return "SessionData[" + currentSchedulerName + "]";
	}
	
	public String getCurrentSchedulerName() {
		return currentSchedulerName;
	}
	
	public void setCurrentSchedulerName(String currentSchedulerServiceName) {
		this.currentSchedulerName = currentSchedulerServiceName;
	}
	
}
