package myschedule.web.servlet.app.handler.pagedata;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.quartz.SchedulerMetaData;

public class SchedulerStatusListPageData {
	@Getter @Setter
	private List<SchedulerStatus> schedulerStatusList;
	
	public static class SchedulerStatus {
		@Getter @Setter
		private String configId;
		@Getter @Setter
		private String name;
		@Getter @Setter
		private boolean initialized;
		@Getter @Setter
		private boolean started;
		@Getter @Setter
		private boolean standby;
		@Getter @Setter
		private boolean shutdown;
		@Getter @Setter
		private SchedulerMetaData schedulerMetaData;
		@Getter @Setter
		private int jobCount;
	}
}
