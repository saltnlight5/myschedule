package integration.myschedule.quartz.extra;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import integration.myschedule.quartz.extra.util.ResultFile;

import java.util.List;
import myschedule.quartz.extra.SchedulerTemplate;
import org.junit.Test;

public class ScriptingSchedulerPluginTest {
	public static ResultFile RESULT_FILE = new ResultFile("ScriptingSchedulerPluginIT.tmp");
	
	@Test
	public void testScriptingSchedulerPlugin() throws Exception {
		try {
			// We will use the script to write to result file then verify
			RESULT_FILE.resetFile();
			SchedulerTemplate st = new SchedulerTemplate("integration/myschedule/quartz/extra/ScriptingSchedulerPluginIT-quartz.properties");
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
}
