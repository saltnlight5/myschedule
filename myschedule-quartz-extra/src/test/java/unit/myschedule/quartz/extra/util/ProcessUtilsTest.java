package unit.myschedule.quartz.extra.util;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import myschedule.quartz.extra.util.ProcessUtils;
import myschedule.quartz.extra.util.ProcessUtils.BackgroundProcess;

import org.junit.Test;

public class ProcessUtilsTest {
	@Test
	public void testRunJava() throws Exception {
		String[] javaCmdArgs = new String[]{ RunJavaMain.class.getName() };
		List<String> output = ProcessUtils.runJava(3000, javaCmdArgs);
		assertThat(output.size(), is(1));
		assertThat(output.get(0), is("DONE"));
		
		javaCmdArgs = new String[]{ RunJavaMain.class.getName(), "A", "B", "C" };
		output = ProcessUtils.runJava(3000, javaCmdArgs);		
		assertThat(output.size(), is(4));
		assertThat(output, hasItems("A", "B", "C", "DONE"));
	}
	
	@Test
	public void testRunJavaWithOpts() throws Exception {
		String[] javaOpts = new String[]{ "-DTestX=foo", "-DTestY=bar" };
		String[] javaCmdArgs = new String[]{ RunJavaWithOptsMain.class.getName()	};
		List<String> output = ProcessUtils.runJavaWithOpts(3000, javaOpts, javaCmdArgs);
		assertThat(output.size(), is(3));
		assertThat(output, hasItems("TestX=foo", "TestY=bar", "DONE"));
		
		javaOpts = new String[]{ "-DTestX=foo", "-DTestY=bar" };
		javaCmdArgs = new String[]{ RunJavaWithOptsMain.class.getName(), "A", "B", "C"	};
		output = ProcessUtils.runJavaWithOpts(3000, javaOpts, javaCmdArgs);
		assertThat(output.size(), is(6));
		assertThat(output, hasItems("TestX=foo", "TestY=bar", "A", "B", "C", "DONE"));
	}
	
	@Test
	public void testRunJavaWithTimeout() throws Exception {
		String[] javaCmdArgs = new String[]{ RunBackgroundProcessMain.class.getName(), "3000" };
		try {
			ProcessUtils.runJava(500, javaCmdArgs);
			fail("We should be timing out after 500 ms, but we are not.");
		} catch (ProcessUtils.TimeoutException e) {
			// Expected to be here.
		}
	}
		
	@Test
	public void testRunBackgroundProcess() throws Exception {
		String pathSep = File.separator;
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + pathSep + "bin" + pathSep + "java";
		String classpath = System.getProperty("java.class.path");
		
		ProcessUtils.LineCollector lineCollector = new ProcessUtils.LineCollector();
		String[] cmdArgs = new String[]{ javaBin, "-cp", classpath, RunBackgroundProcessMain.class.getName() };
		BackgroundProcess bgProcess = ProcessUtils.runInBackground(cmdArgs, lineCollector);
		int exitCode = bgProcess.waitForExit();
		assertThat(exitCode, is(0));
		
		lineCollector = new ProcessUtils.LineCollector();
		cmdArgs = new String[]{ javaBin, "-cp", classpath, RunBackgroundProcessMain.class.getName(), "3000" };
		bgProcess = ProcessUtils.runInBackground(cmdArgs, lineCollector);
		Thread.sleep(500);
		assertThat(bgProcess.isDone(), is(false));
		assertThat(bgProcess.isDestroyed(), is(false));
		bgProcess.destroy();
		Thread.sleep(700); //sometimes we need to wait just a bit to let the process die.
		assertThat(bgProcess.isDone(), is(true));
		assertThat(bgProcess.isDestroyed(), is(true));
	}
	
	public static class RunJavaMain {		
		public static void main(String[] args) {
			for (String arg : args) {
				System.out.println(arg);
			}
			System.out.println("DONE");
		}
	}
	
	public static class RunJavaWithOptsMain {		
		public static void main(String[] args) {
			for (String arg : args) {
				System.out.println(arg);
			}
			for (String name : System.getProperties().stringPropertyNames()) {
				if (name.startsWith("Test")) {
					System.out.println(name + "=" + System.getProperty(name));
				}
			}
			System.out.println("DONE");
		}
	}
	
	public static class RunBackgroundProcessMain {		
		public static void main(String[] args) throws Exception {
			long millis = 0;
			if (args.length > 0) {
				millis = Long.parseLong(args[0]);
			}
			System.out.println("Sleep " + millis + "ms.");
			Thread.sleep(millis);
			System.out.println("DONE");
		}
	}
}
