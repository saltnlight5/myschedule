package unit.myschedule.quartz.extra.job;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.job.LoggerJob;

import org.junit.Test;
import unit.myschedule.quartz.extra.ResultJobListener;

public class LoggerJobTest {
	@Test
	public void testJob() {
		ResultJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addListener(new ResultJobListener());
		
		st.scheduleSimpleJob("test", 1, 0, LoggerJob.class);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobWasExecutedTimes.size(), is(1));
	}
}
