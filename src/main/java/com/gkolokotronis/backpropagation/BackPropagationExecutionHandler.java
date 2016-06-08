package com.gkolokotronis.backpropagation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gkolokotronis.backpropagation.consts.AppConsts;
import com.gkolokotronis.backpropagation.neuron.Neuron;
import com.gkolokotronis.backpropagation.properties.ConfigPropertiesHolder;
import com.gkolokotronis.backpropagation.utils.BackPropUtils;

/**
 * This class is responsible for handling the execution of the application
 * 
 * @author George Kolokotronis
 *
 */
public class BackPropagationExecutionHandler {

	final Logger logger = LogManager.getLogger(BackPropagationExecutionHandler.class);

	public void execute() {
		HashMap<Integer, ArrayList<Neuron>> neuralNetwork = null;

		logger.debug("Validate properties file");
		validatePropertiesFile();
		initializeLayers(neuralNetwork);
		trainNeuralNetwork();
		testNeuralNetwork();

	}

	private void validatePropertiesFile() {
		// TODO validate the properties file
		Properties properties = ConfigPropertiesHolder.getInstance().getProperties();
		System.out.println(properties.entrySet());
	}

	private void initializeLayers(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		ArrayList<Integer> networkStructure = BackPropUtils.getNetworkStructure();

		neuralNetwork = BackPropUtils.initializeLayers();
		BackPropUtils.initializeWeights(networkStructure, neuralNetwork);
	}

	private void trainNeuralNetwork() {
		int epochs = Integer.valueOf(ConfigPropertiesHolder.getInstance().getProperties()
				.getProperty(AppConsts.PROPERTIES_CONFIG_NUMBER_EPOCH));

		for (int currentEpoch = 0; currentEpoch < epochs; currentEpoch++) {

		}

	}

	private void testNeuralNetwork() {

	}

}
