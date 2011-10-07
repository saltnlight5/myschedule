package integration.myschedule.quartz.extra.job;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import integration.myschedule.quartz.extra.ResultJobListener;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.job.LoggerJob;

import org.junit.Test;

public class LoggerJobIT {
	@Test
	public void testJob() {
		ResultJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.getListenerManager().addJobListener(new ResultJobListener());
		
		st.scheduleOnetimeJob("test", LoggerJob.class);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobWasExecutedTimes.size(), is(1));
	}
}
