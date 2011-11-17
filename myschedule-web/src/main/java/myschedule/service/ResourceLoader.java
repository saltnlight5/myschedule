package myschedule.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to help load resource content within a ClassLoader (or inside a packaged jar).
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class ResourceLoader {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceLoader.class);
	private ClassLoader classLoader;
	
	public ResourceLoader() {
		// Default to use current thread ClassLoader
		this.classLoader = Thread.currentThread().getContextClassLoader();
	}
	
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}
	
	public InputStream getResourceInputStream(String resourceName) {
		logger.debug("Loading resource: {}", resourceName);
		ClassLoader classLoader = getClassLoader();
		InputStream inStream = classLoader.getResourceAsStream(resourceName);
		if (inStream == null) {
			throw new IllegalArgumentException("Resource " + resourceName + " not found.");
		}
		return inStream;
	}
	
	public void copyResource(String resourceName, Writer writer) {
		InputStream inStream = getResourceInputStream(resourceName);
		try {
			IOUtils.copy(inStream, writer);
			inStream.close();
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Failed to copy resource " + resourceName + " into a writer object.", e);
		}
	}
	
	public Properties loadProperties(String resourceName) {
		Properties props = new Properties();
		InputStream inStream = getResourceInputStream(resourceName);
		try {
			props.load(inStream);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load Properties resource " + resourceName, e);
		} finally {
			if (inStream != null) {
				IOUtils.closeQuietly(inStream);
			}
		}
		return props;
	}
}
