package integration.myschedule.quartz.extra.job;

import static myschedule.quartz.extra.SchedulerTemplate.createJobDetail;
import static myschedule.quartz.extra.SchedulerTemplate.createSimpleTrigger;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import integration.myschedule.quartz.extra.ResultJobListener;
import integration.myschedule.quartz.extra.ResultSchedulerListener;
import java.io.File;
import javax.script.ScriptException;
import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.job.ScriptingJob;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

public class ScriptingJobTest {
	@Test
	public void testScriptTextJob() {
		ResultJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addListener(new ResultJobListener());
		
		JobDetail job = createJobDetail("MyScriptingJobTest", ScriptingJob.class);
		job.getJobDataMap().put(ScriptingJob.SCRIPT_ENGINE_NAME_KEY, "JavaScript");
		job.getJobDataMap().put(ScriptingJob.SCRIPT_TEXT_KEY, "1 + 99;");
		Trigger trigger = createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobResults.size(), is(1));
		assertThat((Double)ResultJobListener.result.jobResults.get(0), is(new Double(100.0)));
		assertThat(ResultJobListener.result.jobToBeExecutedTimes.size(), is(1));
		assertThat(ResultJobListener.result.jobExecutionVetoedTimes.size(), is(0));
		assertThat(ResultJobListener.result.jobWasExecutedTimes.size(), is(1));
	}
	
	@Test
	public void testScriptTextJobWithDefaultEngineName() {
		ResultJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addListener(new ResultJobListener());
		
		JobDetail job = createJobDetail("MyScriptingJobTest", ScriptingJob.class);
		job.getJobDataMap().put(ScriptingJob.SCRIPT_TEXT_KEY, "2 + 99;");
		Trigger trigger = createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobResults.size(), is(1));
		assertThat((Double)ResultJobListener.result.jobResults.get(0), is(new Double(101.0)));
		assertThat(ResultJobListener.result.jobToBeExecutedTimes.size(), is(1));
		assertThat(ResultJobListener.result.jobExecutionVetoedTimes.size(), is(0));
		assertThat(ResultJobListener.result.jobWasExecutedTimes.size(), is(1));
	}
	
	@Test
	public void testScriptTextJobLogText() {
		ResultJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addListener(new ResultJobListener());

		JobDetail job = createJobDetail("MyScriptingJobTest", ScriptingJob.class);
		job.getJobDataMap().put(ScriptingJob.SCRIPT_ENGINE_NAME_KEY, "JavaScript");
		job.getJobDataMap().put(ScriptingJob.SCRIPT_TEXT_KEY, "3 + 99;");
		job.getJobDataMap().put(ScriptingJob.LOG_SCRIPT_TEXT_KEY, "true");
		Trigger trigger = createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobResults.size(), is(1));
		assertThat((Double)ResultJobListener.result.jobResults.get(0), is(new Double(102.0)));
		assertThat(ResultJobListener.result.jobToBeExecutedTimes.size(), is(1));
		assertThat(ResultJobListener.result.jobExecutionVetoedTimes.size(), is(0));
		assertThat(ResultJobListener.result.jobWasExecutedTimes.size(), is(1));
	}
	
	@Test
	public void testScriptFileJob() {
		ResultJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addListener(new ResultJobListener());
		
		File file = new File("src/test/resources/integration/myschedule/quartz/extra/job/ScriptingJobTest-1.js");
		JobDetail job = createJobDetail("MyScriptingJobTest", ScriptingJob.class);
		job.getJobDataMap().put(ScriptingJob.SCRIPT_ENGINE_NAME_KEY, "JavaScript");
		job.getJobDataMap().put(ScriptingJob.SCRIPT_FILE_KEY, file.getAbsolutePath());
		Trigger trigger = createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobResults.size(), is(1));
		assertThat((Double)ResultJobListener.result.jobResults.get(0), is(new Double(100.0)));
		assertThat(ResultJobListener.result.jobToBeExecutedTimes.size(), is(1));
		assertThat(ResultJobListener.result.jobExecutionVetoedTimes.size(), is(0));
		assertThat(ResultJobListener.result.jobWasExecutedTimes.size(), is(1));
	}
	
	@Test
	public void testScriptTextJobWithNullResultObject() {
		ResultJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addListener(new ResultJobListener());
		
		JobDetail job = createJobDetail("MyScriptingJobTest", ScriptingJob.class);
		job.getJobDataMap().put(ScriptingJob.SCRIPT_ENGINE_NAME_KEY, "JavaScript");
		job.getJobDataMap().put(ScriptingJob.SCRIPT_TEXT_KEY, "1 + 99; null;");
		Trigger trigger = createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobResults.size(), is(1));
		assertThat((Double)ResultJobListener.result.jobResults.get(0), nullValue());
		assertThat(ResultJobListener.result.jobToBeExecutedTimes.size(), is(1));
		assertThat(ResultJobListener.result.jobExecutionVetoedTimes.size(), is(0));
		assertThat(ResultJobListener.result.jobWasExecutedTimes.size(), is(1));
	}
	
	/** 
	 * If the ScriptpingJob were to throw anything Exception other than JobExecutionException, only then a 
	 * SchedulerListener will be notify with error.
	 */
	@Test
	public void testScriptTextJobWithException() {
		ResultJobListener.resetResult();
		ResultSchedulerListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addListener(new ResultJobListener());
		st.addListener(new ResultSchedulerListener());
		
		JobDetail job = createJobDetail("MyScriptingJobTest", ScriptingJob.class);
		job.getJobDataMap().put(ScriptingJob.SCRIPT_ENGINE_NAME_KEY, "JavaScript");
		job.getJobDataMap().put(ScriptingJob.SCRIPT_TEXT_KEY, "1 + 99; throw 'An expected error.';");
		Trigger trigger = createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobToBeExecutedTimes.size(), is(1));
		assertThat(ResultJobListener.result.jobExecutionVetoedTimes.size(), is(0));
		
		// Notice the count is one!
		assertThat(ResultJobListener.result.jobResults.size(), is(1));
		assertThat(ResultJobListener.result.jobWasExecutedTimes.size(), is(1));
		
		// Scheduler listener should have been notified.
		assertThat(ResultSchedulerListener.result.scheduleErrorTimes.size(), is(1));
		assertThat((String)ResultSchedulerListener.result.scheduleErrorTimes.get(0)[1], containsString("exception"));
		Throwable t = ((SchedulerException)
				ResultSchedulerListener.result.scheduleErrorTimes.get(0)[2]).getUnderlyingException();
		assertThat(t instanceof RuntimeException, is(true));
		assertThat(t.getMessage(), startsWith("Failed to execute job"));
		assertThat(t.getCause() instanceof ScriptException, is(true));
	}
	
	/** 
	 * Note: because we set flag to wrap any script exception into JobExecutionException, thus Quartz has no way of 
	 * notifying listener other than just log it in this case.
	 */
	@Test
	public void testScriptTextJobWithJobExecutionException() {
		ResultJobListener.resetResult();
		ResultSchedulerListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addListener(new ResultJobListener());
		st.addListener(new ResultSchedulerListener());
		
		JobDetail job = createJobDetail("MyScriptingJobTest", ScriptingJob.class);
		job.getJobDataMap().put(ScriptingJob.USE_JOB_EXECUTION_EXCEPTION_KEY, "true");
		job.getJobDataMap().put(ScriptingJob.SCRIPT_ENGINE_NAME_KEY, "JavaScript");
		job.getJobDataMap().put(ScriptingJob.SCRIPT_TEXT_KEY, 
				"2 + 99; throw Packages.org.quartz.JobExecutionException('An expected error.');");
		Trigger trigger = createSimpleTrigger("MyScriptingJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobToBeExecutedTimes.size(), is(1));
		assertThat(ResultJobListener.result.jobExecutionVetoedTimes.size(), is(0));
		
		// Notice the count is one!
		assertThat(ResultJobListener.result.jobResults.size(), is(1));
		assertThat(ResultJobListener.result.jobWasExecutedTimes.size(), is(1));
		
		// Scheduler listener should have been notified.
		assertThat(ResultSchedulerListener.result.scheduleErrorTimes.size(), is(0));
	}
}
