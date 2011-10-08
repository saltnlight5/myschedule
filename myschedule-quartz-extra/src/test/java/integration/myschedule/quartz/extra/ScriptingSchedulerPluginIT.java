package integration.myschedule.quartz.extra;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import myschedule.quartz.extra.SchedulerTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ScriptingSchedulerPluginIT {
	public static File PLUGIN_RESULT_FILE = createTempFile("ScriptingSchedulerPluginIT.tmp");
	
	public static File createTempFile(String filename) {
		return new File(System.getProperty("java.io.tmpdir") + "/" + filename);
	}

	public static void resetResult() {
		// Reset file content
		try {
			FileUtils.writeStringToFile(PLUGIN_RESULT_FILE, "");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void writeResult(String text) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(PLUGIN_RESULT_FILE, true);
			IOUtils.write(text, writer);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	@Test
	public void testScriptingSchedulerPlugin() throws Exception {
		try {
			// We will use the script to write to result file then verify
			resetResult();
			SchedulerTemplate st = new SchedulerTemplate("integration/myschedule/quartz/extra/ScriptingSchedulerPluginIT-quartz.properties");
			st.startAndShutdown(700);
			
			List<String> result = FileUtils.readLines(PLUGIN_RESULT_FILE);
			assertThat(result.size(), is(4));
			assertThat(result.get(0), is("name: MyScriptingPlugin"));
			assertThat(result.get(1), containsString("initialize:"));
			assertThat(result.get(2), containsString("start:"));
			assertThat(result.get(3), containsString("shutdown:"));
		} finally {
			PLUGIN_RESULT_FILE.delete();
		}
	}
}
