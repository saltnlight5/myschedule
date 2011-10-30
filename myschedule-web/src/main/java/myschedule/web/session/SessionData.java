package myschedule.web.session;



public class SessionData {

	public static final String SESSION_DATA_KEY = "sessionData";

	protected String scriptEngineName = "JavaScript"; // Default to JavaScript.
	protected String currentSchedulerName;
	protected String currentSchedulerConfigId;
	
	public void setCurrentSchedulerConfigId(String currentSchedulerConfigId) {
		this.currentSchedulerConfigId = currentSchedulerConfigId;
	}
	
	public String getCurrentSchedulerConfigId() {
		return currentSchedulerConfigId;
	}
	
	public String getCurrentSchedulerName() {
		return currentSchedulerName;
	}
	
	public void setCurrentSchedulerName(String currentSchedulerServiceName) {
		this.currentSchedulerName = currentSchedulerServiceName;
	}

	public String getScriptEngineName() {
		return scriptEngineName;
	}
	
	public void setScriptEngineName(String scriptEngineName) {
		this.scriptEngineName = scriptEngineName;
	}
	
	@Override
	public String toString() {
		return "SessionData[currentSchedulerName=" + currentSchedulerName + 
				", scriptEngineName=" + scriptEngineName + "]";
	}
}
