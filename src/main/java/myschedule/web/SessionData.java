package myschedule.web;

import java.util.List;

public class SessionData {
	protected List<SchedulerNameKey> schedulerNameKeys;
	protected SchedulerNameKey currentSchedulerNameKey;
	
	public List<SchedulerNameKey> getSchedulerNameKeys() {
		return schedulerNameKeys;
	}

	public void setSchedulerNameKeys(List<SchedulerNameKey> schedulerNameKeys) {
		this.schedulerNameKeys = schedulerNameKeys;
	}

	public SchedulerNameKey getCurrentSchedulerNameKey() {
		return currentSchedulerNameKey;
	}

	public void setCurrentSchedulerNameKey(SchedulerNameKey currentSchedulerNameKey) {
		this.currentSchedulerNameKey = currentSchedulerNameKey;
	}

	public static class SchedulerNameKey {
		protected String instanceName;
		protected String instanceId;
		public SchedulerNameKey(String name, String id) {
			this.instanceName = name;
			this.instanceId = id;
		}
	}
}
