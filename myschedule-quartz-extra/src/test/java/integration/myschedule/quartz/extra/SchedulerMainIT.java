package integration.myschedule.quartz.extra;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import myschedule.quartz.extra.ProcessUtils;
import myschedule.quartz.extra.SchedulerMain;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.SchedulerPlugin;

public class SchedulerMainIT {
	public static File PLUGIN_RESULT_FILE = createTempFile("SchedulerMainIT-ResultSchedulerPlugin.tmp");
	
	@Test
	public void testMainWithTimeout() throws Exception {		
		try {
			try {
				// Default SchedulerMain will run as server, so this should cause test to timeout.
				String config = "integration/myschedule/quartz/extra/SchedulerMainIT-quartz.properties";
				ProcessUtils.runJava(700, SchedulerMain.class.getName(), config);
				fail("We should have timed-out, but didn't.");
			} catch (ProcessUtils.TimeoutException e) {
				// expected.
			}
			List<String> result = FileUtils.readLines(PLUGIN_RESULT_FILE);
			assertThat(result.size(), is(3));
			assertThat(result.get(0), is("name: MyResultSchedulerPluginTest"));
			assertThat(result.get(1), containsString("initialize:"));
			assertThat(result.get(2), containsString("start:"));
			
			// Note we don't have shutdown due to timeout!
		} finally {
			PLUGIN_RESULT_FILE.delete();
		}
	}
	
	public static File createTempFile(String filename) {
		return new File(System.getProperty("java.io.tmpdir") + "/" + filename);
	}

	public static class ResultSchedulerPlugin implements SchedulerPlugin {
		public ResultSchedulerPlugin() {
			// Reset file content
			try {
				FileUtils.writeStringToFile(PLUGIN_RESULT_FILE, "");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		@Override
		public void initialize(String name, Scheduler scheduler) throws SchedulerException {
			writeResult("name: " + name + "\n");
			writeResult("initialize: " + new Date() + "\n");
		}

		@Override
		public void start() {
			writeResult("start: " + new Date() + "\n");
		}

		@Override
		public void shutdown() {
			writeResult("shutdown: " + new Date() + "\n");
		}
		
		protected void writeResult(String text) {
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
	}
}
