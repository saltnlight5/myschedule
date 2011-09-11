package myschedule.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSchedulerConfigDao extends AbstractService implements SchedulerConfigDao {
	
	private static final Logger logger = LoggerFactory.getLogger(FileSchedulerConfigDao.class);
	protected File storeDir;
	
	public void setStoreDir(File storeDir) {
		this.storeDir = storeDir;
	}
	
	@Override
	public void save(SchedulerConfig sc) {
		String id = sc.getConfigId();
		logger.debug("Saving SchedulerConfig to file {}.", id);
		write(id, sc.getConfigPropsText());
		logger.info("Config {} saved.", id);
	}

	@Override
	public void update(SchedulerConfig sc) {
		String id = sc.getConfigId();
		logger.debug("Updating SchedulerConfig on file {}.", id);
		write(id, sc.getConfigPropsText());
		logger.info("Config {} updated.", id);
	}
	
	protected void write(String name, String text) {
		File file = new File(storeDir, name);
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			IOUtils.write(text, writer);
		} catch (IOException e) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, 
					"Failed to save file: " + file.getAbsolutePath());
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	@Override
	public SchedulerConfig load(String configId) {
		File file = new File(storeDir, configId);
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			String text = IOUtils.toString(reader);
			SchedulerConfig sc = new SchedulerConfig(configId, text);
			return sc;
		} catch (IOException e) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, 
					"Failed to read file: " + file.getAbsolutePath());
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	@Override
	public SchedulerConfig delete(String configId) {
		SchedulerConfig sc = load(configId);
		File file = new File(storeDir, configId);
		if (!file.delete()) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, 
					"Failed to delete file: " + file.getAbsolutePath());
		}
		return sc;
	}

	@Override
	protected void initService() {
		if (!storeDir.exists()) {
			logger.warn("Directory {} doesn't exist. Will attempt to create it.", storeDir);
			if (!storeDir.mkdirs()) {
				throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, "Failed to create directory " + storeDir);
			}
			logger.info("New directory {} has been created.", storeDir);
		}
	}

	@Override
	public Collection<String> getAllSchedulerConfigIds() {
		List<String> ids = new ArrayList<String>();
		File[] files = storeDir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				ids.add(file.getName());
			}
		}
		Collections.sort(ids); // Sort the ids so the end user may consistently get the first unchanged record.
		return ids;
	}	
}
