package myschedule.web.servlet.app.handler;


public class DashboardData {
	public static class SchedulerDetail {
		private String configId;
		private String initExceptionMessage;
		
		private String initialized;

		/** Problem when retrieving any of below fields */
		private Boolean schedulerProblem;
		
		private String name;
		private String started;
		private String runningSince;
		private String numOfJobs;
		public String getConfigId() {
			return configId;
		}
		public void setConfigId(String configId) {
			this.configId = configId;
		}
		public String getInitExceptionMessage() {
			return initExceptionMessage;
		}
		public void setInitExceptionMessage(String initExceptionMessage) {
			this.initExceptionMessage = initExceptionMessage;
		}
		public String getInitialized() {
			return initialized;
		}
		public void setInitialized(String initialized) {
			this.initialized = initialized;
		}
		public Boolean getSchedulerProblem() {
			return schedulerProblem;
		}
		public void setSchedulerProblem(Boolean schedulerProblem) {
			this.schedulerProblem = schedulerProblem;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getStarted() {
			return started;
		}
		public void setStarted(String started) {
			this.started = started;
		}
		public String getRunningSince() {
			return runningSince;
		}
		public void setRunningSince(String runningSince) {
			this.runningSince = runningSince;
		}
		public String getNumOfJobs() {
			return numOfJobs;
		}
		public void setNumOfJobs(String numOfJobs) {
			this.numOfJobs = numOfJobs;
		}
		
	}
}
