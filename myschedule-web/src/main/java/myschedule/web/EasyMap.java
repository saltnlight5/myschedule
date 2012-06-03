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
 * A easy map for configuration that backed by Java Properties. You may use multiple ways to add configuration values into this
 * class, and each time you add properties, it will auto check to expand any <code>${variable}</code> using existing
 * System Properties and what's already loaded as lookup values.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 * 
 */
public class EasyMap {
	private static final String CLASSPATH_PREFIX = "classpath:";
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected Properties config;

	public EasyMap() {
		config = new Properties();
	}

	public EasyMap(String configFile) {
		config = new Properties();
		if (configFile != null)
			addConfig(configFile);
	}

	// ====================
	// Config value getters
	// ====================
	public String getConfig(String key) {
		ensureKeyExists(key);
		String result = config.getProperty(key);
		if (result != null)
			result = result.trim();
		return result;
	}

	public String getConfig(String key, String def) {
		return config.getProperty(key, def);
	}

	public int getConfigInt(String key) {
		ensureKeyExists(key);
		String val = getConfig(key);
		int ret = Integer.parseInt(val);
		return ret;
	}

	public int getConfigInt(String key, int def) {
		String val = getConfig(key, "" + def);
		int ret = Integer.parseInt(val);
		return ret;
	}
	
	public long getConfigLong(String key) {
		ensureKeyExists(key);
		String val = getConfig(key);
		long ret = Long.parseLong(val);
		return ret;
	}

	public long getConfigLong(String key, long def) {
		String val = getConfig(key, "" + def);
		long ret = Long.parseLong(val);
		return ret;
	}

	public double getConfigDouble(String key) {
		ensureKeyExists(key);
		String val = getConfig(key);
		double ret = Double.parseDouble(val);
		return ret;
	}

	public double getConfigDouble(String key, double def) {
		String val = getConfig(key, "" + def);
		double ret = Double.parseDouble(val);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public <T> T getConfigClass(String key) {
		ensureKeyExists(key);
		String val = getConfig(key);
		return (T)toClass(val);
	}

	@SuppressWarnings("unchecked")
	public <T> T getConfigClass(String key, String def) {
		String val = getConfig(key, def);
		if (val == null)
			return null;
		return (T)toClass(val);
	}

	// ========================
	// Add Configuration Values
	// ========================
	/**
	 * Add a direct Properties as configuration values.
	 * 
	 * @param newConfig
	 */
	public void addConfig(Properties newConfig) {
		logger.debug("Adding configuration from direct properties: {}.", newConfig);
		Properties props = expandVariables(newConfig, config);
		config.putAll(props);
	}

	/**
	 * Add any file configuration. It will support any URL and 'classpath:' protocol as well.
	 * 
	 * @param configFile
	 *            - a file or resource name to load Properties as configuration values.
	 */
	public void addConfig(String configFile) {
		logger.debug("Adding configuration from URL: {}.", configFile);
		InputStream inStream = null;
		if (configFile.startsWith(CLASSPATH_PREFIX)) {
			// If configFile is prefix with 'classpath:' then load it as jar
			// resource
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
		Properties props = null;
		try {
			props = new Properties();
			props.load(inStream);
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

		// Expand variables and then add to config store.
		props = expandVariables(props, config);
		config.putAll(props);
	}

	/**
	 * Check to see if Java System Properties exists with a key, and then load it as URL or 'classpath:' protocol
	 * configuration values.
	 * 
	 * @param configFileKey
	 */
	public void addConfigBySysProps(String configFileKey) {
		String configFile = System.getProperty(configFileKey);
		if (configFile != null) {
			addConfig(configFile);
		}
	}

	/**
	 * Auto expand any ${variable} in expandProps using a lookupProps for existing variable definitions. This method
	 * will automatically search the System Properties space for lookup as well.
	 */
	protected Properties expandVariables(Properties props, Properties lookupProps) {
		Properties expandedProps = new Properties();
		expandedProps.putAll(props);

		Properties lookupPropsCopy = new Properties();
		lookupPropsCopy.putAll(lookupProps);
		lookupPropsCopy.putAll(System.getProperties());

		StrSubstitutor substitutor = new StrSubstitutor(lookupPropsCopy);
		for (String name : expandedProps.stringPropertyNames()) {
			String val = expandedProps.getProperty(name);
			String newVal = substitutor.replace(val);
			if (!newVal.equals(val)) {
				expandedProps.put(name, newVal);
				logger.debug("Props {} has variable substituted with new value: {}", name, newVal);
			}
		}
		return expandedProps;
	}

	// ====================
	// Supporting functions
	// ====================
	protected void ensureKeyExists(String key) {
		if (!config.containsKey(key)) {
			throw new IllegalArgumentException("Config key not found: " + key);
		}
	}

	protected Class<?> toClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	protected ClassLoader getClassLoader() {
		return getClass().getClassLoader();
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(Class<?> cls) {
		try {
			Object obj = cls.newInstance();
			return (T)obj;
		} catch (InstantiationException e) {
			throw new RuntimeException("Failed to create new instance: From " + cls, e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to create new instance: From " + cls, e);
		}
	}
}
