package integration.myschedule.quartz.extra.job;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import integration.myschedule.quartz.extra.TestJobListener;

import java.io.File;

import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.job.ScriptingJob;

import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Trigger;

public class ScriptingJobIT {
	@Test
	public void testScriptTextJob() {
		TestJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.getListenerManager().addJobListener(new TestJobListener());
		
		JobDetail job = ScriptingJob.createJobDetail("JavaScript", "MyScriptingJobTest", "1 + 99;");
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(TestJobListener.result.jobResults.size(), is(1));
		assertThat((Double)TestJobListener.result.jobResults.get(0), is(new Double(100.0)));
	}
	
	@Test
	public void testScriptTextJobWithDefaultEngineName() {
		TestJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.getListenerManager().addJobListener(new TestJobListener());
		
		JobDetail job = SchedulerTemplate.createJobDetail("MyScriptingJobTest", ScriptingJob.class);
		job.getJobDataMap().put(ScriptingJob.SCRIPT_TEXT_KEY, "1 + 99;");
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(TestJobListener.result.jobResults.size(), is(1));
		assertThat((Double)TestJobListener.result.jobResults.get(0), is(new Double(100.0)));
	}
	
	@Test
	public void testScriptTextJobLogText() {
		TestJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.getListenerManager().addJobListener(new TestJobListener());
		
		JobDetail job = ScriptingJob.createJobDetail("JavaScript", "MyScriptingJobTest", "1 + 99;");
		job.getJobDataMap().put(ScriptingJob.LOG_SCRIPT_TEXT_KEY, "true");
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(TestJobListener.result.jobResults.size(), is(1));
		assertThat((Double)TestJobListener.result.jobResults.get(0), is(new Double(100.0)));
	}
	
	@Test
	public void testScriptFileJob() {
		TestJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.getListenerManager().addJobListener(new TestJobListener());
		
		File file = new File("src/test/resources/integration/myschedule/quartz/extra/job/ScriptingJobIT-Test1.js");
		JobDetail job = ScriptingJob.createJobDetail("JavaScript", "MyScriptingJobTest", file);
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(TestJobListener.result.jobResults.size(), is(1));
		assertThat((Double)TestJobListener.result.jobResults.get(0), is(new Double(100.0)));
	}
}
