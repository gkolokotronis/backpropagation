package com.gkolokotronis.backpropagation.properties;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gkolokotronis.backpropagation.consts.AppConsts;

public final class ConfigPropertiesValidator {

	private final static Logger logger = LogManager.getLogger(ConfigPropertiesValidator.class);

	private Properties properties;

	private ConfigPropertiesValidator(Properties properties) {
		this.properties = properties;
	}

	public static ConfigPropertiesValidator newInstance(Properties properties) {
		return new ConfigPropertiesValidator(properties);
	}

	public void validate() {
		logger.debug("Started validation of " + AppConsts.PROPERTIES_FILE_NAME + " file");
		validateMandatoryProperties();
		validateNonMandatoryProperties();
	}

	protected void validateMandatoryProperties() {
		validateForNulls();
		validateUnitsPerLayer();
		validateDouble(AppConsts.PROPERTIES_CONFIG_LEARNING_RATE);
		validateDouble(AppConsts.PROPERTIES_CONFIG_MOMENTUM);
		validateEpochs();
		validateFileExistence();

	}

	protected void validateUnitsPerLayer() {
		String unitsPerLayer = properties.getProperty(AppConsts.PROPERTIES_CONFIG_NUMBER_UNITS_PER_LAYER);

		List<String> resultString = Arrays.asList(unitsPerLayer.split(","));

		if (resultString.size() < 2) {
			logger.error("Property " + AppConsts.PROPERTIES_CONFIG_NUMBER_UNITS_PER_LAYER
					+ " must be a list of integers separated by a comma (,) of a size at least 2. Please check your "
					+ AppConsts.PROPERTIES_FILE_NAME + " file");
			throw new RuntimeException("Property " + AppConsts.PROPERTIES_CONFIG_NUMBER_UNITS_PER_LAYER
					+ " must be a list of integers separated by a comma (,) of a size at least 2. Please check your "
					+ AppConsts.PROPERTIES_FILE_NAME + " file");
		}

		for (int i = 0; i < resultString.size(); i++) {
			try {
				Integer.valueOf(resultString.get(i));
			} catch (NumberFormatException e) {
				logger.error("Property " + AppConsts.PROPERTIES_CONFIG_NUMBER_UNITS_PER_LAYER
						+ " must be a list of integers separated by a comma (,) of a size at least 2. Please check your "
						+ AppConsts.PROPERTIES_FILE_NAME + " file");
				throw new RuntimeException("Property " + AppConsts.PROPERTIES_CONFIG_NUMBER_UNITS_PER_LAYER
						+ " must be a list of integers separated by a comma (,) of a size at least 2. Please check your "
						+ AppConsts.PROPERTIES_FILE_NAME + " file");
			}
		}

	}

	protected void validateDouble(String propertyName) {
		String learningRate = properties.getProperty(propertyName);
		try {
			Double.valueOf(learningRate);
		} catch (NumberFormatException e) {
			logger.error("Property " + propertyName + " must be a double precision number. Please check your "
					+ AppConsts.PROPERTIES_FILE_NAME + " file");
			throw new RuntimeException(
					"Property " + propertyName + " must be a double precision number. Please check your "
							+ AppConsts.PROPERTIES_FILE_NAME + " file");
		}
	}

	protected void validateEpochs() {
		String numberOfEpochs = properties.getProperty(AppConsts.PROPERTIES_CONFIG_NUMBER_EPOCH);
		try {
			Integer.valueOf(numberOfEpochs);
		} catch (NumberFormatException e) {
			logger.error("Property " + AppConsts.PROPERTIES_CONFIG_NUMBER_EPOCH
					+ " must be an integer. Please check your " + AppConsts.PROPERTIES_FILE_NAME + " file");
			throw new RuntimeException("Property " + AppConsts.PROPERTIES_CONFIG_NUMBER_EPOCH
					+ " must be an integer. Please check your " + AppConsts.PROPERTIES_FILE_NAME + " file");
		}
	}

	protected void validateFileExistence() {
		String[] mandatoryFiles = { AppConsts.PROPERTIES_CONFIG_TRAINING_FILE,
				AppConsts.PROPERTIES_CONFIG_CROSS_VALIDATION_FILE, AppConsts.PROPERTIES_CONFIG_TEST_FILE };

		for (String propertyName : mandatoryFiles) {
			String fileLocation = properties.getProperty(propertyName);
			File file = new File(fileLocation);

			if (!file.exists() || !file.isFile()) {
				logger.error("Property " + propertyName + " contains a file that does not exists. Please check your "
						+ AppConsts.PROPERTIES_FILE_NAME + " file");
				throw new RuntimeException(
						"Property " + propertyName + " contains a file that does not exists. Please check your "
								+ AppConsts.PROPERTIES_FILE_NAME + " file");
			}
		}
	}

	protected void validateForNulls() {
		String[] mandatoryProperties = { AppConsts.PROPERTIES_CONFIG_NUMBER_UNITS_PER_LAYER,
				AppConsts.PROPERTIES_CONFIG_LEARNING_RATE, AppConsts.PROPERTIES_CONFIG_MOMENTUM,
				AppConsts.PROPERTIES_CONFIG_NUMBER_EPOCH, AppConsts.PROPERTIES_CONFIG_TRAINING_FILE,
				AppConsts.PROPERTIES_CONFIG_CROSS_VALIDATION_FILE, AppConsts.PROPERTIES_CONFIG_TEST_FILE,
				AppConsts.PROPERTIES_CONFIG_FINAL_WEIGHTS_FILE };

		for (String property : mandatoryProperties) {
			String propertyValue = properties.getProperty(property);
			if (propertyValue == null) {
				logger.error("Property " + property + " is mandatory. Please check your "
						+ AppConsts.PROPERTIES_FILE_NAME + " file");
				throw new RuntimeException("Property " + property + " is mandatory. Please check your "
						+ AppConsts.PROPERTIES_FILE_NAME + " file");
			}
		}
	}

	protected void validateNonMandatoryProperties() {
		String initializationFile = properties.getProperty(AppConsts.PROPERTIES_CONFIG_FILE_TO_INITIALIZE);

		if (initializationFile != null && initializationFile.length() > 0) {
			File file = new File(initializationFile);

			if (!file.exists() || !file.isFile()) {
				logger.error("Property " + AppConsts.PROPERTIES_CONFIG_FILE_TO_INITIALIZE
						+ " contains a file that does not exists. Please check your " + AppConsts.PROPERTIES_FILE_NAME
						+ " file");
				throw new RuntimeException("Property " + AppConsts.PROPERTIES_CONFIG_FILE_TO_INITIALIZE
						+ " contains a file that does not exists. Please check your " + AppConsts.PROPERTIES_FILE_NAME
						+ " file");
			}
		}
	}
}
