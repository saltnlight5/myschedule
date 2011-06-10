package myschedule.service;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.simpl.CascadingClassLoadHelper;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.xml.XMLSchedulingDataProcessor;

/** 
 * Extending the parent to expose two getter methods.
 *
 * @author Zemian Deng
 */
public class XmlJobLoader extends XMLSchedulingDataProcessor {

	/**
	 * Constructor
	 *
	 * @param clh
	 * @throws ParserConfigurationException
	 */
	public XmlJobLoader(ClassLoadHelper clh) throws ParserConfigurationException {
		super(clh);
	}
	
	public XmlJobLoader() throws ParserConfigurationException {
		super(new CascadingClassLoadHelper());
		
		// Need to initialize the classLoadHelper manually.
		classLoadHelper.initialize();
	}
	
	/**
	 * Expose getter with public access.
	 * @return
	 */
	@Override
	public List<JobDetail> getLoadedJobs() {
		return super.getLoadedJobs();
	}
	
	/**
	 * Expose getter with public access.
	 * @return
	 */
	@Override
	public List<Trigger> getLoadedTriggers() {
		return super.getLoadedTriggers();
	}

}
