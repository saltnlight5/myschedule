package myschedule.quartz.extra;

import org.quartz.SchedulerException;

/**
 * A main entry to start a scheduler as a tiny server. This program will block the main thread until user shutdown
 * the JVM (eg: use CTRL+C).
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class SchedulerMain {
	public static void main(String[] args) throws SchedulerException {
		// Create a scheduler
		final SchedulerTemplate scheduler;
		if (args.length > 0) {
			String quartzConfig = args[0];
			scheduler = new SchedulerTemplate(quartzConfig);
		} else {
			scheduler = new SchedulerTemplate();
		}		
		
		// Register a shutdown hook to bring down scheduler
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				scheduler.shutdown(true); // true => wait for jobs to complete.
			}
		});
		
		// Start the scheduler.
		scheduler.startAndWait();
	}
}
