package integration.myschedule.quartz.extra.job;

import java.io.File;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.job.ScriptingJob;

import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Trigger;

public class ScriptingJobIT {
	@Test
	public void testScriptTextJob() {
		SchedulerTemplate st = new SchedulerTemplate();
		JobDetail job = ScriptingJob.createJobDetail("JavaScript", "MyScriptingJobTest", "logger.info('testScriptTextJob');");
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(1000);
	}
	
	@Test
	public void testScriptTextJobWithDefaultEngineName() {
		SchedulerTemplate st = new SchedulerTemplate();
		JobDetail job = SchedulerTemplate.createJobDetail("MyScriptingJobTest", ScriptingJob.class);
		job.getJobDataMap().put(ScriptingJob.SCRIPT_TEXT_KEY, "logger.info('testScriptTextJobWithDefaultEngineName');");
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(1000);
	}
	
	@Test
	public void testScriptTextJobLogText() {
		SchedulerTemplate st = new SchedulerTemplate();
		JobDetail job = ScriptingJob.createJobDetail("JavaScript", "MyScriptingJobTest", "logger.info('testScriptTextJobLogText');");
		job.getJobDataMap().put(ScriptingJob.LOG_SCRIPT_TEXT_KEY, "true");
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(1000);
	}
	
	@Test
	public void testScriptFileJob() {
		SchedulerTemplate st = new SchedulerTemplate();
		File file = new File("src/test/resources/integration/myschedule/quartz/extra/job/ScriptingJobIT-Test1.js");
		JobDetail job = ScriptingJob.createJobDetail("JavaScript", "MyScriptingJobTest", file);
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(1000);
	}
}
