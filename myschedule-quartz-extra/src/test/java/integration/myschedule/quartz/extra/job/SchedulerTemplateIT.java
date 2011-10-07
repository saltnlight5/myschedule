package integration.myschedule.quartz.extra.job;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import myschedule.quartz.extra.SchedulerTemplate;

import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Integration test for SchedulerTemplate.
 *  
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class SchedulerTemplateIT {
	
	@Test
	public void testScheduleOnetimeJob() throws Exception {
		TestJob.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.scheduleOnetimeJob("test", TestJob.class);
		st.startAndShutdown(99);
		assertThat(TestJob.jobResult.executionTimes.size(), is(1));
	}
	
	public static class TestJob implements Job {		
		static Result jobResult = new Result();
		static void resetResult() {
			jobResult = new Result();
		}
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			jobResult.executionTimes.add(new Date());
		}
		
		public static class Result {
			List<Date> executionTimes = new ArrayList<Date>();
		}
	}
}
