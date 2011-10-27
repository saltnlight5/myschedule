package myschedule.quartz.extra;

import java.io.InputStream;
import org.quartz.Scheduler;

/**
 * Place any utility methods that would help programmer in using Quartz.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class QuartzExtraUtils {

	/**
	 * Load job scheduling data xml using XMLSchedulingDataProcessor.
	 * 
	 * @param inStream - input stream of content for job_scheduling_data xml.
	 * @return XMLSchedulingDataProcessor instance will contain all the jobs parsed from xml input.
	 */
	public static XmlJobLoader scheduleXmlSchedulingData(InputStream inStream, Scheduler scheduler) {
		try {
			// XmlJobLoader is not only just a loader, but also use to store what's loaded!
			XmlJobLoader xmlJobLoader = XmlJobLoader.newInstance(); 
			String systemId = XmlJobLoader.XML_SYSTEM_ID;
			xmlJobLoader.processStreamAndScheduleJobs(inStream, systemId, scheduler);
			return xmlJobLoader;
		} catch (Exception e) {
			throw new QuartzRuntimeException(e);
		}
	}
	
}
