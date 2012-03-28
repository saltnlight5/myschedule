package unit.myschedule.quartz.extra.job;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.job.OsCommandJob;

import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import unit.myschedule.quartz.extra.ResultJobListener;
import unit.myschedule.quartz.extra.util.ProcessUtilsTest;

public class OsCommandJobTest {
	@Test
	public void testJavaOsCommandJobSingleLine() {
		String pathSep = File.separator;
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + pathSep + "bin" + pathSep + "java";
		String classpath = System.getProperty("java.class.path");
		Class<?> mainClass = ProcessUtilsTest.RunBackgroundProcessMain.class;
		String cmdArg = javaBin + " -cp " + classpath + " " + mainClass.getName() + " " + "500";
		
		ResultJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addJobListener(new ResultJobListener());
		
		JobDetail job = SchedulerTemplate.createJobDetail("MyOsCommandJobTest", OsCommandJob.class);
		JobDataMap dataMap = job.getJobDataMap();
		dataMap.put(OsCommandJob.CMD_ARGS_KEY, cmdArg);
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyOsCommandJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobResults.size(), is(1));
		assertThat((Integer)ResultJobListener.result.jobResults.get(0), is(0));
	}
	
	@Test
	public void testJavaOsCommandJob() {
		String pathSep = File.separator;
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + pathSep + "bin" + pathSep + "java";
		String classpath = System.getProperty("java.class.path");
		Class<?> mainClass = ProcessUtilsTest.RunBackgroundProcessMain.class;
		String[] cmdArgs = new String[]{ javaBin, "-cp", classpath, mainClass.getName(), "500" };
		
		ResultJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addJobListener(new ResultJobListener());
		
		JobDetail job = SchedulerTemplate.createJobDetail("MyOsCommandJobTest", OsCommandJob.class);
		JobDataMap dataMap = job.getJobDataMap();
		dataMap.put(OsCommandJob.CMD_ARGS_KEY, cmdArgs);
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyOsCommandJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobResults.size(), is(1));
		assertThat((Integer)ResultJobListener.result.jobResults.get(0), is(0));
	}
	
	@Test
	public void testJavaOsCommandJobRunInBackground() {
		String pathSep = File.separator;
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + pathSep + "bin" + pathSep + "java";
		String classpath = System.getProperty("java.class.path");
		Class<?> mainClass = ProcessUtilsTest.RunBackgroundProcessMain.class;
		String[] cmdArgs = new String[]{ javaBin, "-cp", classpath, mainClass.getName(), "400" };
		
		ResultJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addJobListener(new ResultJobListener());
		
		JobDetail job = SchedulerTemplate.createJobDetail("MyOsCommandJobTest", OsCommandJob.class);
		JobDataMap dataMap = job.getJobDataMap();
		dataMap.put(OsCommandJob.CMD_ARGS_KEY, cmdArgs);
		dataMap.put(OsCommandJob.RUN_IN_BACKGROUND_KEY, "true");
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyOsCommandJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(800);
		
		assertThat(ResultJobListener.result.jobResults.size(), is(1));
		// Because we run in background, we will not able to have result set.
		assertThat(ResultJobListener.result.jobResults.get(0), nullValue());
	}
	
	@Test
	public void testJavaOsCommandJobWithTimeout() {
		String pathSep = File.separator;
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + pathSep + "bin" + pathSep + "java";
		String classpath = System.getProperty("java.class.path");
		Class<?> mainClass = ProcessUtilsTest.RunBackgroundProcessMain.class;
		String[] cmdArgs = new String[]{ javaBin, "-cp", classpath, mainClass.getName(), "3000" };
		
		ResultJobListener.resetResult();
		SchedulerTemplate st = new SchedulerTemplate();
		st.addJobListener(new ResultJobListener());
		
		JobDetail job = SchedulerTemplate.createJobDetail("MyOsCommandJobTest", OsCommandJob.class);
		JobDataMap dataMap = job.getJobDataMap();
		dataMap.put(OsCommandJob.CMD_ARGS_KEY, cmdArgs);
		dataMap.put(OsCommandJob.TIMEOUT_KEY, "500");
		Trigger trigger = SchedulerTemplate.createSimpleTrigger("MyOsCommandJobTest");
		st.scheduleJob(job, trigger);
		st.startAndShutdown(99);
		
		assertThat(ResultJobListener.result.jobResults.size(), is(1));
		assertThat(ResultJobListener.result.jobResults.get(0), nullValue());
	}
}
