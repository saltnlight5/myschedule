package myschedule.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A file storage implementation for SchedulerService DAO.
 * 
 * @author Zemian Deng
 */
public class SchedulerServiceFileDao implements SchedulerServiceDao {

	public static final String CONFIG_EXT = ".properties";
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected File configStoreLocation;
	
	public void setConfigStoreLocation(File configStoreLocation) {
		this.configStoreLocation = configStoreLocation;
	}

	@Override
	public void saveSchedulerService(SchedulerService schedulerService) {
		Properties configProps = schedulerService.getConfigProps();
		String name = configProps.getProperty(Quartz18SchedulerService.NAME_KEY, "QuartzScheduler");
		File file = getConfigFile(name);
		
		// Ensure we do not overwrite existing file.
		if (file.exists())
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, "Config " + file + " already exists.");
		
		// Ensure we have scheduler data save together into the configProperties
		if (!configProps.containsKey(Quartz18SchedulerService.AUTO_START_KEY))
			configProps.setProperty(Quartz18SchedulerService.AUTO_START_KEY, "" + schedulerService.isAutoStart());
		if (!configProps.containsKey(Quartz18SchedulerService.WAIT_FOR_JOBS_KEY))
			configProps.setProperty(Quartz18SchedulerService.WAIT_FOR_JOBS_KEY, "" + schedulerService.isWaitForJobsToComplete());
		
		// Write config file.
		try {
			FileWriter writer = new FileWriter(file);
			configProps.store(writer, "MySchedule Scheduler Config. CreateDate: " + new Date());
			writer.close();
			logger.info("Config props " + file + " has been saved.");
		} catch (Exception e) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, "Failed to create Properties from input config.", e);			
		}				
	}

	/** Return an un-initialized scheduler service instance. */
	@Override
	public SchedulerService getSchedulerService(String schedulerServiceName) {
		File file = getConfigFile(schedulerServiceName);
		Properties configProps = new Properties();
		try {
			FileReader reader = new FileReader(file);
			configProps.load(reader);
			reader.close();
		} catch (IOException e) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, "Failed to read configConfigs: " + file, e);	
		}
		
		Quartz18SchedulerService schedulerService = new Quartz18SchedulerService();
		schedulerService.setConfigProps(configProps);
		return schedulerService;
	}

	@Override
	public void deleteSchedulerService(String schedulerServiceName) {
		File file = getConfigFile(schedulerServiceName);
		if(!file.delete()) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, 
					"Failed to delete config: " + file);			
		}
		logger.info("Config " + file + " has been removed.");
	}
	
	protected File getConfigFile(String schedulerServiceName) {
		return new File(configStoreLocation, schedulerServiceName + CONFIG_EXT);
	}

	@Override
	public boolean hasSchedulerService(String schedulerServiceName) {
		return getConfigFile(schedulerServiceName).exists();
	}


	@Override
	public List<String> getSchedulerServiceNames() {
		List<String> names = new ArrayList<String>();
		File[] files = configStoreLocation.listFiles();
		for (File file : files) {
			String name = file.getName();
			if (name.endsWith(CONFIG_EXT))
				name = name.substring(0, name.length() - CONFIG_EXT.length());
				names.add(name);
		}
		return names;
	}
	
	@Override
	public void init() {
		if (!configStoreLocation.exists()) {
			if (!configStoreLocation.mkdirs()) {
				throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, "Failed to create configStoreLocation " + configStoreLocation);
			}
			logger.info("Created directory for configStoreLocation " + configStoreLocation);
		}
	}

	@Override
	public void destroy() {
	}
}
