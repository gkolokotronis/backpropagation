package com.gkolokotronis.backpropagation.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.gkolokotronis.backpropagation.consts.AppConsts;
import com.gkolokotronis.backpropagation.neuron.Neuron;
import com.gkolokotronis.backpropagation.properties.ConfigPropertiesHolder;

/**
 * Useful utilities
 * 
 * @author George Kolokotronis
 *
 */
public final class BackPropUtils {

	/**
	 * to avoid instantiation
	 */
	private BackPropUtils() {

	}

	/**
	 * function that populates the outcome of the neuron
	 * 
	 * @param neuronPosition
	 *            - the position of our neuron in the layer where it resides so
	 *            that it can find the weights values from the previous layer
	 *
	 * @param learningRate
	 *            - learningRate
	 * @param previousLayer
	 *            - the previous layer of neurons
	 * @return
	 */
	// TODO remove learning rate and get it from properties file
	public static double sigmoidOutput(int neuronPosition, double learningRate, ArrayList<Neuron> previousLayer) {
		double output = 0.0;

		for (Neuron neuron : previousLayer) {
			output += neuron.getWeight().get(neuronPosition) * neuron.getOutput();
		}

		output = output * learningRate;
		output = 1 / (1 + Math.exp(-output));
		return output;
	}

	/**
	 * Initializes the weights of each Neuron. Weights on each Neuron is an
	 * arraylist of Double. First element is the weight for the first neuron on
	 * the next layer and so on. The next layer's bias unit does not have any
	 * weights attached to it
	 * 
	 * @param networkStructure
	 * @param network
	 */
	public static void initializeWeights(ArrayList<Integer> networkStructure,
			HashMap<Integer, ArrayList<Neuron>> network) {

		for (int i = 0; i < networkStructure.size() - 1; i++) {
			ArrayList<Neuron> layer = network.get(i);
			int nextLayerSize = 0;
			if (i + 1 == networkStructure.size() - 1) {
				nextLayerSize = network.get(i + 1).size();
			} else {
				nextLayerSize = network.get(i + 1).size() - 1;
			}
			for (Neuron neuron : layer) {
				ArrayList<Double> weights = new ArrayList<Double>();
				for (int j = 0; j < nextLayerSize; j++) {
					weights.add(getRandomWeight());
				}
				neuron.setWeight(weights);
			}
		}

	}

	private static Double getRandomWeight() {
		Random random = new Random();

		double max = 0.0001;
		double min = -0.0001;
		double range = max - min;
		double randomNumber;
		do {
			randomNumber = random.nextDouble();
		} while (randomNumber == 0.0);
		double scaled = randomNumber * range;
		double shifted = scaled + min;
		return shifted;
	}

	/**
	 * returns the network structure as an arraylist of integers. First element
	 * is the number of input units and the last number the output layer units
	 * 
	 * @return - ArrayList<Integer> representing the neural network structure.
	 *         First element is the input layer
	 */
	public static ArrayList<Integer> getNetworkStructure() {
		String unitsPerLayer = ConfigPropertiesHolder.getInstance().getProperties()
				.getProperty(AppConsts.PROPERTIES_CONFIG_NUMBER_UNITS_PER_LAYER);

		ArrayList<Integer> result = new ArrayList<Integer>();

		List<String> resultString = Arrays.asList(unitsPerLayer.split(","));

		for (int i = 0; i < resultString.size(); i++) {
			result.add(Integer.valueOf(resultString.get(i)));
		}

		return result;
	}

	/**
	 * Initializes the layers with Neuron. Each layer is an arraylist of Neuron
	 * plus the bias unit. Returns a HashMap where the 0 key is the input layer.
	 * 1 is the next layer and so on
	 * 
	 * @return
	 */
	public static HashMap<Integer, ArrayList<Neuron>> initializeLayers() {
		HashMap<Integer, ArrayList<Neuron>> result = new HashMap<Integer, ArrayList<Neuron>>();

		ArrayList<Integer> networkStructure = getNetworkStructure();

		for (int i = 0; i < networkStructure.size(); i++) {
			ArrayList<Neuron> layer = new ArrayList<Neuron>();
			for (int j = 0; j < networkStructure.get(i); j++) {
				Neuron neuron = new Neuron();
				layer.add(neuron);
			}
			// bias unit except on the output unit
			if (i != networkStructure.size() - 1) {
				Neuron bias = new Neuron();
				bias.setOutput(1.0);
				layer.add(bias);
			}
			result.put(i, layer);
		}

		System.out.println(result);

		return result;
	}
}
