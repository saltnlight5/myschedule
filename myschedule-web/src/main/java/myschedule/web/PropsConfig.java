package myschedule.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * A base class to load configuration set. This class will load a Properties file. The file can be specify either
 * through a constructor, or through Java System Properties key {@link #configFileKey}.
 * 
 * <p>User must call {@link #initConfig()} before using the config properties!
 * 
 * <p>Example on command line usage:
 * {@code
 * $ java -Dconfig=classpath:myapplication/config.properties myapp.Main
 * 
 * Or
 * 
 * $ java -Dconfig=file:///myapplication/config.properties myapp.Main
 * }
 * 
 * <p>Or you can subclass and use it like this:
 * <pre><code>
 * public class MyDbConfig extends PropsConfig {
 *   protected void initConfig() {
 *     config = new Properties();
 *     loadConfigPropertiesFromDb(config); // You have to implements this method.
 *     expandVariables(); // Auto expand any ${variable}.
 *   }
 * }
 * </code></pre>
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class PropsConfig {
	private static final Logger logger = LoggerFactory.getLogger(PropsConfig.class);
	private static final String CLASSPATH_PREFIX = "classpath:";
	private String configFileKey = "config";
	private String configFileDefaultName = "config.properties";
	private String configFile;
	private Properties config;
	
	public PropsConfig() {
	}
	
	public PropsConfig(String configFile) {
		this.configFile = configFile;
	}
	
	public void setConfig(Properties config) {
		this.config = config;
	}
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	public void setConfigFileKey(String configFileKey) {
		this.configFileKey = configFileKey;
	}
	public void setConfigFileDefaultName(String configFileDefaultName) {
		this.configFileDefaultName = configFileDefaultName;
	}

	// ====================
	// Config value getters
	// ====================
	public String getConfig(String key) {
		return config.getProperty(key);
	}
	public String getConfig(String key, String def) {
		return config.getProperty(key, def);
	}
	public int getConfigInt(String key, int def) {
		String val = getConfig(key, "" + def);
		int ret = Integer.parseInt(val);
		return ret;
	}
	public double getConfigDouble(String key, double def) {
		String val = getConfig(key, "" + def);
		double ret = Double.parseDouble(val);
		return ret;
	}
	
	public Class<?> getConfigClass(String key, String def) {
		String val = getConfig(key, def);
		try {
			return Class.forName(val);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
		
	protected void initConfig() {
		// Do nothing if config is already loaded.
		if (config != null) {
			return;
		}
		
		// If user didn't set configFile, then use Java System Properties key=value to file configFile name.
		if (configFile == null) {
			configFile = System.getProperty(configFileKey, configFileDefaultName);
		}
		logger.debug("Initializing configuration from {}.", configFile);

		InputStream inStream = null;
		
		if (configFile.startsWith(CLASSPATH_PREFIX)) {
			// If configFile is prefix with 'classpath:' then load it as jar resource
			String resName = configFile.substring(CLASSPATH_PREFIX.length());
			inStream = getClassLoader().getResourceAsStream(resName);
			if (inStream == null) {
				throw new RuntimeException("Config file via jar resource not found: " + configFile);
			}
		} else {
			// load config file as URL
			URL url;
			try {
				url = new URL(configFile);
				inStream = url.openStream();
			} catch (MalformedURLException e) {
				throw new RuntimeException("Config file is invalid: " + configFile, e);
			} catch (IOException e) {
				throw new RuntimeException("Config file is invalid: " + configFile, e);
			}
		}
		
		// Load config from inStream.
		try {
			config = new Properties();
			config.load(inStream);
			logger.info("Configuration loaded successfully from {}.", configFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (inStream != null) {
				IOUtils.closeQuietly(inStream);
			}
		}
		
		expandVariables();
	}
	
	/** Auto expand any ${variable}. */
	protected void expandVariables() {
		Properties configCopy = new Properties(config);
		configCopy.putAll(System.getProperties());
		StrSubstitutor substitutor = new StrSubstitutor(configCopy);
		for (String name : config.stringPropertyNames()) {
			String val = config.getProperty(name);
			String newVal = substitutor.replace(val);
			if (!newVal.equals(val)) {
				config.put(name,  newVal);
				logger.trace("Config {} has new value: {}", name, newVal);
			}
		}
	}
	
	private ClassLoader getClassLoader() {
		return getClass().getClassLoader();
	}
}
