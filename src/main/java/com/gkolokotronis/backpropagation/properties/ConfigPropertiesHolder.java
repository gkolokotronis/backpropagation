package com.gkolokotronis.backpropagation.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gkolokotronis.backpropagation.consts.AppConsts;

public final class ConfigPropertiesHolder {

	private static final Logger logger = LogManager.getLogger(ConfigPropertiesHolder.class);

	private static final ConfigPropertiesHolder SINGLETON = new ConfigPropertiesHolder();

	private Properties properties = new Properties();

	private ConfigPropertiesValidator validator;

	private ConfigPropertiesHolder() {

		String propertiesPath = System.getProperty("user.dir") + System.getProperty("file.separator")
				+ AppConsts.PROPERTIES_FILE_NAME;
		load(propertiesPath);

		this.validator = ConfigPropertiesValidator.newInstance(getProperties());
		this.validator.validate();
	}

	public static ConfigPropertiesHolder getInstance() {
		return SINGLETON;
	}

	/**
	 * Returns the properties file.
	 * 
	 * @return DistinctElement
	 */
	public Properties getProperties() {
		return this.properties;
	}

	/**
	 * It loads the properties path
	 * 
	 * @param propertiesFilePath
	 *            - the file path of the properties file
	 */
	private void load(String propertiesFilePath) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(propertiesFilePath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Property file " + propertiesFilePath + " not found", e);

		}

		try {
			getProperties().load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException("Something went wrong while reading property file " + propertiesFilePath, e);
		}

	}

	/**
	 * It validates the properties path
	 */
	private void validate() {
		Properties properties = getProperties();

	}

}
