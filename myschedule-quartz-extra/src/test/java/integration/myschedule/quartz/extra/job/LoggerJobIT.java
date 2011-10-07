package integration.myschedule.quartz.extra.job;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import integration.myschedule.quartz.extra.TestJobListener;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.job.LoggerJob;

import org.junit.Test;

public class LoggerJobIT {
	@Test
	public void testJob() {
		TestJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.getListenerManager().addJobListener(new TestJobListener());
		
		st.scheduleOnetimeJob("test", LoggerJob.class);
		st.startAndShutdown(99);
		
		assertThat(TestJobListener.result.jobWasExecutedTimes.size(), is(1));
	}
}
