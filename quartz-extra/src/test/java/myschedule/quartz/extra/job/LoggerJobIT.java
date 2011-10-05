package myschedule.quartz.extra.job;

import myschedule.quartz.extra.SchedulerTemplate;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

public class LoggerJobIT {
	@Test
	public void testJob() throws Exception {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		SchedulerTemplate st = new SchedulerTemplate(scheduler);
		st.scheduleOnetimeJob("test", LoggerJob.class);
		Thread.sleep(5000);
	}
}
