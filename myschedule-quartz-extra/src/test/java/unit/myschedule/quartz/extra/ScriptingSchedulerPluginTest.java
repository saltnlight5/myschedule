package unit.myschedule.quartz.extra;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import myschedule.quartz.extra.QuartzRuntimeException;
import myschedule.quartz.extra.SchedulerTemplate;

import org.junit.Assert;
import org.junit.Test;
import org.quartz.JobDetail;

import unit.myschedule.quartz.extra.util.ResultFile;

public class ScriptingSchedulerPluginTest {
	public static ResultFile RESULT_FILE = new ResultFile(ScriptingSchedulerPluginTest.class.getSimpleName() + ".tmp");
	
	@Test
	public void testScriptingSchedulerPlugin() throws Exception {
		try {
			// We will use the script to write to result file then verify
			RESULT_FILE.resetFile();
			SchedulerTemplate st = new SchedulerTemplate(
					"unit/myschedule/quartz/extra/ScriptingSchedulerPluginTest-quartz.properties");
			st.startAndShutdown(700);
			
			List<String> result = RESULT_FILE.readLines();
			assertThat(result.size(), is(4));
			assertThat(result.get(0), is("name: MyScriptingPlugin"));
			assertThat(result.get(1), containsString("initialize:"));
			assertThat(result.get(2), containsString("start:"));
			assertThat(result.get(3), containsString("shutdown:"));
		} finally {
			RESULT_FILE.delete();
		}
	}
	
	@Test
	public void testScriptingSchedulerPluginFileNotFound() throws Exception {
		try {
			RESULT_FILE.resetFile();
			SchedulerTemplate st = new SchedulerTemplate(
					"unit/myschedule/quartz/extra/ScriptingSchedulerPluginTest-quartz-filenotfound.properties");
			st.startAndShutdown(700);
			Assert.fail("We should faile with file not found.");
		} catch (QuartzRuntimeException e) {
			assertThat(e.getCause() instanceof FileNotFoundException, is(true));
		} finally {
			RESULT_FILE.delete();
		}
	}
	
	@Test
	public void testScriptingSchedulerPluginOneClasspathFile() throws Exception {
		try {
			// We will use the script to write to result file then verify
			RESULT_FILE.resetFile();
			SchedulerTemplate st = new SchedulerTemplate(
					"unit/myschedule/quartz/extra/ScriptingSchedulerPluginTest-quartz-classpath.properties");
			st.startAndShutdown(700);
			
			List<String> result = RESULT_FILE.readLines();
			assertThat(result.size(), is(2));
			assertThat(result.get(0), is("name: MyScriptingPlugin"));
			assertThat(result.get(1), containsString("initialize:"));
		} finally {
			RESULT_FILE.delete();
		}
	}
	
	@Test
	public void testScriptingSchedulerPluginMultiFiles() throws Exception {
		SchedulerTemplate st = new SchedulerTemplate(
				"unit/myschedule/quartz/extra/ScriptingSchedulerPluginTest-quartz-multifiles.properties");
		List<JobDetail> jobs = st.getAllJobDetails();
		assertThat(jobs.size(), is(2));
		List<String> names = new ArrayList<String>();
		names.add(jobs.get(0).getKey().getName());
		names.add(jobs.get(1).getKey().getName());
		assertThat(names, hasItems("hourlyJob1", "hourlyJob2"));
		st.shutdown(true);
	}
}
