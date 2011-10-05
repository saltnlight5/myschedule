package myschedule.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

/**
 * An entity that holds a scheduler configuration.
 * 
 * <p>A scheduler configuration will be hold in configProps. This class will also allow
 * a text string to be set and auto load configProps. The configId can be used as filename
 * to store the configPropsText in separate DAO service. The purpose of the configPropsText
 * is used to preserve properties config context will remain in order after save/load
 * from a DAO service.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class SchedulerConfig {
	
	protected String configId;
	protected String configPropsText;
	protected Properties configProps;
	
	public SchedulerConfig(String configId, String configPropsText) {
		setConfigId(configId);
		setConfigPropsText(configPropsText);
	}
	
	public Properties getConfigProps() {
		return configProps;
	}
	
	public void setConfigProps(Properties configProps) {
		this.configProps = configProps;
	}
	
	public String getConfigId() {
		return configId;
	}
	
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	
	public String getConfigPropsText() {
		return configPropsText;
	}
	
	public void setConfigPropsText(String configPropsText) {
		this.configPropsText = configPropsText;
		
		// Auto load and reset the configProps.
		configProps = new Properties();
		Reader reader = null;
		try {
			reader = new StringReader(configPropsText);
			configProps.load(reader);
		} catch (IOException e) {
			throw new ErrorCodeException(ErrorCode.DATA_ACCESS_PROBLME, 
					"Failed to create Properties object from configPropsText.");
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

}
