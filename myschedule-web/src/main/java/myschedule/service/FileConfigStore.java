package myschedule.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.io.IOUtils;

/**
 * A file and directory based ConfigStore that use a single config per file storage. An UUID will be auto generated 
 * upon each new config created.
 * 
 * <p>You should NOT store any other files in the storeDir other than these managed config files, or else the 
 * getAllIds() will not work!
 * 
 * <p>This service will auto create the storeDir if not exist upon service initialization.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class FileConfigStore extends AbstractService implements ConfigStore {
	
	private File storeDir;
	
	public void setStoreDir(File storeDir) {
		this.storeDir = storeDir;
	}
	
	protected String generateId() {
		return UUID.randomUUID().toString();
	}
	
	protected void write(File file, String text) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			IOUtils.write(text, writer);
		} catch (IOException e) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLEM, 
					"Failed to save file: " + file.getAbsolutePath());
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	protected String read(File file) {
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			String text = IOUtils.toString(reader);
			return text;
		} catch (IOException e) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLEM, 
					"Failed to read file: " + file.getAbsolutePath());
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	@Override
	public String create(String config) {
		String id = generateId();
		File file = new File(storeDir, id);
		logger.debug("Saving text to file {}.", file);
		write(file, config);
		logger.info("Text {} saved.", id);
		return id;
	}
	
	@Override
	public String get(String id) {
		File file = new File(storeDir, id);
		return read(file);
	}

	@Override
	public void delete(String id) {
		File file = new File(storeDir, id);
		if (!file.exists()) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLEM, 
					"File does not exists: " + file.getAbsolutePath());
		}
		if (!file.delete()) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLEM, 
					"Failed to delete file: " + file.getAbsolutePath());
		}
	}
	
	@Override
	public void update(String id, String config) {
		File file = new File(storeDir, id);
		logger.debug("Updating file {}.", file);
		write(file, config);
		logger.info("Config {} updated.", id);
	}

	@Override
	public Set<String> getAllIds() {
		Set<String> ids = new HashSet<String>();
		File[] files = storeDir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				ids.add(file.getName());
			}
		}
		return ids;
	}

	@Override
	protected void initService() {
		if (!storeDir.exists()) {
			logger.warn("Directory {} doesn't exist. Will attempt to create it.", storeDir);
			if (!storeDir.mkdirs()) {
				throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLEM, "Failed to create directory " + storeDir);
			}
			logger.info("New directory {} has been created.", storeDir);
		}
	}
}
