package myschedule.quartz.extra.job;

import myschedule.quartz.extra.ResultJobListener;
import myschedule.quartz.extra.SchedulerTemplate;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LoggerJobTest {
    @Test
    public void testJob() {
        ResultJobListener.resetResult();
        SchedulerTemplate st = new SchedulerTemplate();
        st.addJobListener(new ResultJobListener());

        st.scheduleSimpleJob("test", 1, 0, LoggerJob.class);
        st.startAndShutdown(99);

        assertThat(ResultJobListener.result.jobWasExecutedTimes.size(), is(1));
    }
}
