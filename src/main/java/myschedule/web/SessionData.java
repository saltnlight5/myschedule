package myschedule.web;



public class SessionData {
	protected String currentSchedulerName;	
	protected boolean currentSchedulerStarted;	
	protected boolean currentSchedulerPaused;
	
	@Override
	public String toString() {
		return super.toString() + "[currentSchedulerName=" + currentSchedulerName + "]";
	}
		
	public void setCurrentSchedulerName(String currentSchedulerName) {
		this.currentSchedulerName = currentSchedulerName;
	}
	
	public String getCurrentSchedulerName() {
		return currentSchedulerName;
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
