package myschedule.web.servlet.app.handler;

import lombok.Data;

public class DashboardData {
	@Data
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
	}
}
