package com.gkolokotronis.backpropagation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
		HashMap<Integer, ArrayList<Neuron>> neuralNetwork = new HashMap<Integer, ArrayList<Neuron>>();

		logger.debug("Validate properties file");
		validatePropertiesFile();
		initializeLayers(neuralNetwork);
		trainNeuralNetwork(neuralNetwork);
		testNeuralNetwork(neuralNetwork);

	}

	private void validatePropertiesFile() {
		// TODO validate the properties file
		Properties properties = ConfigPropertiesHolder.getInstance().getProperties();
		System.out.println(properties.entrySet());
	}

	private void initializeLayers(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		ArrayList<Integer> networkStructure = BackPropUtils.getNetworkStructure();

		BackPropUtils.initializeLayers(neuralNetwork);
		BackPropUtils.initializeWeights(networkStructure, neuralNetwork);

	}

	private void trainNeuralNetwork(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		int epochs = Integer.valueOf(ConfigPropertiesHolder.getInstance().getProperties()
				.getProperty(AppConsts.PROPERTIES_CONFIG_NUMBER_EPOCH));

		for (int currentEpoch = 0; currentEpoch < epochs; currentEpoch++) {

			String trainingFile = (String) ConfigPropertiesHolder.getInstance().getProperties()
					.get(AppConsts.PROPERTIES_CONFIG_TRAINING_FILE);

			ArrayList<Integer> networkStructure = BackPropUtils.getNetworkStructure();

			FileReader fileReader = null;
			try {
				fileReader = new FileReader(trainingFile);
				BufferedReader br = new BufferedReader(fileReader);
				int lineNumber = 1;

				for (String line; (line = br.readLine()) != null;) {

					List<String> trainingExample = Arrays.asList(line.split(","));
					if (trainingExample != null && trainingExample.size() != networkStructure.get(0)
							+ networkStructure.get(networkStructure.size() - 1)) {
						logger.error(
								"Problem with training example at line: " + lineNumber + "of file: " + trainingFile);
						continue;
					}
					ArrayList<Double> trainingExampleDouble = new ArrayList<Double>();

					for (String example : trainingExample) {
						trainingExampleDouble.add(Double.valueOf(example));
					}

					BackPropUtils.feedForward(neuralNetwork, trainingExampleDouble);
					BackPropUtils.backwardPass(neuralNetwork, trainingExampleDouble);

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void testNeuralNetwork(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {

	}

}
