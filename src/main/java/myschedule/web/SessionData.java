package myschedule.web;



public class SessionData {
	protected String currentSchedulerServiceName;
	protected boolean currentSchedulerStarted;	
	protected boolean currentSchedulerPaused;
	
	@Override
	public String toString() {
		return "SessionData[" + currentSchedulerServiceName + "]";
	}
	
	public String getCurrentSchedulerServiceName() {
		return currentSchedulerServiceName;
	}
	
	public void setCurrentSchedulerServiceName(String currentSchedulerServiceName) {
		this.currentSchedulerServiceName = currentSchedulerServiceName;
	}
	
	public boolean isCurrentSchedulerStarted() {
		return currentSchedulerStarted;
	}

	public void setCurrentSchedulerStarted(boolean currentSchedulerStarted) {
		this.currentSchedulerStarted = currentSchedulerStarted;
	}

	public boolean isCurrentSchedulerPaused() {
		return currentSchedulerPaused;
	}

	public void setCurrentSchedulerPaused(boolean currentSchedulerPaused) {
		this.currentSchedulerPaused = currentSchedulerPaused;
	}
}
