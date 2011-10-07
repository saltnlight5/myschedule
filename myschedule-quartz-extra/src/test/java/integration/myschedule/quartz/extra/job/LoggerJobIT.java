package integration.myschedule.quartz.extra.job;

import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.job.LoggerJob;

import org.junit.Test;

public class LoggerJobIT {
	@Test
	public void testJob() {
		SchedulerTemplate st = new SchedulerTemplate();
		st.scheduleOnetimeJob("test", LoggerJob.class);
		st.startAndShutdown(1000);
	}
}
