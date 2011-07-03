package myschedule.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

/**
 * A file storage implementation for SchedulerService DAO.
 * 
 * @author Zemian Deng
 */
public class SchedulerServiceFileDao extends AbstractService implements SchedulerServiceDao {

	public static final String CONFIG_EXT = ".properties";
		
	protected File configStoreLocation;
	
	public void setConfigStoreLocation(File configStoreLocation) {
		this.configStoreLocation = configStoreLocation;
	}
	
	@Override
	public void saveSchedulerService(SchedulerService schedulerService, String origConfigPropsText, boolean update) {
		String name = schedulerService.getConfigSchedulerName();
		if (name == null)
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, "Config is missing scheduler name.");
		
		File file = getConfigFile(name);
		
		if (!update) {
			// Ensure we do not overwrite existing file.
			if (file.exists())
				throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, "Config " + file + " already exists.");
		}
		
		// Write config file.
		try {
			FileWriter writer = new FileWriter(file);
			if (origConfigPropsText != null) {
				writer.write(origConfigPropsText);
				logger.info("Save config using origConfigPropsText.");
			} else {
				Properties configProps = schedulerService.getConfigProps();
				configProps.store(writer, "MySchedule Scheduler Config. CreateDate: " + new Date());
				logger.info("Save config using configProps object.");
			}
			writer.flush();
			writer.close();
			if (update)
				logger.info("Update to SchedulerService " + name + " config props on file: " + file + " is done.");
			else 
				logger.info("New SchedulerService " + name + " config props on file: " + file + " is done.");
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
	public String getConfigPropsText(String schedulerServiceName) {
		File file = getConfigFile(schedulerServiceName);
		try {
			FileReader reader = new FileReader(file);
			StringWriter writer = new StringWriter();
			IOUtils.copy(reader, writer);
			reader.close();
			writer.close();
			return writer.toString();
		} catch (IOException e) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, "Failed to read config props from file: " + file);
		}
	}
	
	@Override
	public void initService() {
		if (!configStoreLocation.exists()) {
			if (!configStoreLocation.mkdirs()) {
				throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, "Failed to create configStoreLocation " + configStoreLocation);
			}
			logger.info("Created directory for configStoreLocation " + configStoreLocation);
		}
	}

	@Override
	protected void destroyService() {
	}

	@Override
	protected void startService() {
	}

	@Override
	protected void stopService() {
	}
}
