package myschedule.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoader {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceLoader.class);
	protected ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	
	public void copyResource(String resName, Writer writer) {
		logger.debug("Loading resource: {}", resName);
		ClassLoader classLoader = getClassLoader();
		InputStream inStream = classLoader.getResourceAsStream(resName);
		if (inStream == null) {
			throw new IllegalArgumentException("Resource " + resName + " not found.");
		}
		try {
			IOUtils.copy(inStream, writer);
			inStream.close();
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Failed copy resource " + resName + " into a writer object.", e);
		}
	}
	
	protected ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}
