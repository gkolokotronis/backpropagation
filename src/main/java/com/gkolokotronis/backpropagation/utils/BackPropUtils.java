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
	 *            1.1896841802240486 * 10^-5
	 * @param learningRate
	 *            - learningRate
	 * @param previousLayer
	 *            - the previous layer of neurons
	 * @return
	 */
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

	/**
	 * Return a random Double with in the range of -0.9 to 0.9, excluding 0.0
	 * 
	 * @return - a Double with in the range of -0.9 to 0.9, excluding 0.0
	 */
	private static Double getRandomWeight() {
		Random random = new Random();

		double max = 0.9d;
		double min = -0.9d;
		double range = max - min;
		double randomNumber;
		do {
			randomNumber = random.nextDouble();
		} while (randomNumber == 0.0d);
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
	 * @param neuralNetwork
	 *            - the neural network
	 */
	public static void initializeLayers(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {

		ArrayList<Integer> networkStructure = getNetworkStructure();

		for (int i = 0; i < networkStructure.size(); i++) {
			ArrayList<Neuron> layer = new ArrayList<Neuron>();
			for (int j = 0; j < networkStructure.get(i); j++) {
				Neuron neuron = new Neuron();
				layer.add(neuron);
			}
			// set the bias unit except on the output layer
			if (i != networkStructure.size() - 1) {
				Neuron bias = new Neuron();
				bias.setOutput(1.0);
				layer.add(bias);
			}
			neuralNetwork.put(i, layer);
		}

	}

	/**
	 * executes the forward pass step of the backpropagation algorithm
	 * 
	 * @param neuralNetwork
	 *            - the neural network
	 * @param trainingExample
	 *            - the training example
	 */
	public static void forwardPass(HashMap<Integer, ArrayList<Neuron>> neuralNetwork,
			ArrayList<Double> trainingExample) {

		ArrayList<Integer> networkStructure = getNetworkStructure();
		double learningRate = Double.valueOf((String) ConfigPropertiesHolder.getInstance().getProperties()
				.get(AppConsts.PROPERTIES_CONFIG_LEARNING_RATE));

		calculateOutputForInputLayer(neuralNetwork, trainingExample);
		calculateOutputForHiddenLayers(neuralNetwork, learningRate, networkStructure);
		calculateOutputForOutputLayer(neuralNetwork, learningRate, networkStructure);

	}

	/**
	 * Sets the output for each neuron of the input layer from the training
	 * example
	 * 
	 * @param neuralNetwork
	 *            - the neural network
	 * @param trainingExample
	 *            - the training example
	 */
	private static void calculateOutputForInputLayer(HashMap<Integer, ArrayList<Neuron>> neuralNetwork,
			ArrayList<Double> trainingExample) {
		ArrayList<Neuron> inputLayer = neuralNetwork.get(0);

		for (int i = 0; i < inputLayer.size() - 1; i++) {
			inputLayer.get(i).setOutput(trainingExample.get(i));
		}
	}

	/**
	 * Calculates the output for each neuron on the hidden layers
	 * 
	 * @param neuralNetwork
	 *            - the neural network
	 * @param learningRate
	 *            - the learning rate
	 */
	private static void calculateOutputForHiddenLayers(HashMap<Integer, ArrayList<Neuron>> neuralNetwork,
			double learningRate, ArrayList<Integer> networkStructure) {

		for (int i = 1; i < networkStructure.size() - 1; i++) {
			ArrayList<Neuron> layer = neuralNetwork.get(i);

			for (int j = 0; j < layer.size() - 1; j++) {
				layer.get(j).setOutput(sigmoidOutput(j, learningRate, neuralNetwork.get(i - 1)));
			}
		}
	}

	/**
	 * Calculates the output for each neuron on the output layer
	 * 
	 * @param neuralNetwork
	 *            - the neural network
	 * @param learningRate
	 *            - the learning rate
	 */
	private static void calculateOutputForOutputLayer(HashMap<Integer, ArrayList<Neuron>> neuralNetwork,
			double learningRate, ArrayList<Integer> networkStructure) {
		ArrayList<Neuron> outputLayer = neuralNetwork.get(networkStructure.size() - 1);

		for (int i = 0; i < outputLayer.size(); i++) {
			outputLayer.get(i)
					.setOutput(sigmoidOutput(i, learningRate, neuralNetwork.get(networkStructure.size() - 2)));
		}
	}

	/**
	 * Executes the backward pass of the back propagation algorithm
	 * 
	 * @param neuralNetwork
	 *            - the neural network
	 * @param trainingExampleDouble
	 *            - the training example
	 */
	public static void backwardPass(HashMap<Integer, ArrayList<Neuron>> neuralNetwork,
			ArrayList<Double> trainingExampleDouble) {
		computeDelta(neuralNetwork, trainingExampleDouble);
		recalculateWeights(neuralNetwork);

	}

	/**
	 * Computes the delta of the neural network
	 * 
	 * @param neuralNetwork
	 *            - the neural network
	 * @param trainingExample
	 *            - the training example output is needed for the delta
	 *            computation of the output layer
	 */
	private static void computeDelta(HashMap<Integer, ArrayList<Neuron>> neuralNetwork,
			ArrayList<Double> trainingExample) {

		computeDeltaOutputLayer(neuralNetwork.get(neuralNetwork.size() - 1), trainingExample);

		// calculate hidden layers if exist
		if (getNetworkStructure().size() > 2) {
			computeDeltaHiddenLayers(neuralNetwork);
		}
	}

	/**
	 * Computes the delta for the hidden layers neurons
	 * 
	 * @param neuralNetwork
	 *            - the neuralNetwork
	 */
	private static void computeDeltaHiddenLayers(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {

		int networkSize = getNetworkStructure().size();

		for (int i = networkSize - 2; i > 0; i--) {
			ArrayList<Neuron> currentLayer = neuralNetwork.get(i);

			// do not update the delta of the bias unit - bias unit is the last
			// neuron on each layer
			for (int j = 0; j < currentLayer.size() - 1; j++) {
				ArrayList<Neuron> nextLayer = neuralNetwork.get(i + 1);
				double delta = 0.0;
				double weightComputation = 0.0;

				delta = currentLayer.get(j).getOutput() * (1 - currentLayer.get(j).getOutput());
				ArrayList<Double> weights = currentLayer.get(j).getWeight();

				for (int k = 0; k < weights.size(); k++) {
					weightComputation += weights.get(k) * nextLayer.get(k).getDelta();
				}

				delta = delta * weightComputation;
				currentLayer.get(j).setDelta(delta);
			}
		}
	}

	/**
	 * Compute the delta for the output layer neurons
	 * 
	 * @param outputLayer
	 *            - the output layer of the neural network
	 * @param trainingExample
	 *            - the training example (both input and output units)
	 */
	private static void computeDeltaOutputLayer(ArrayList<Neuron> outputLayer, ArrayList<Double> trainingExample) {
		int numberInputNeurons = getNetworkStructure().get(0);

		for (int i = 0; i < outputLayer.size(); i++) {

			double y = outputLayer.get(i).getOutput();
			double desiredOutput = trainingExample.get(numberInputNeurons + i);
			outputLayer.get(i).setDelta(y * (1.0 - y) * (y - desiredOutput));

		}
	}

	/**
	 * loop through the neurons of the network and recalculate the weights of
	 * each neuron. It excludes the output layer.
	 * 
	 * @param neuralNetwork
	 *            - the neural network which needs weight recalculation
	 */
	private static void recalculateWeights(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		double momentum = Double.valueOf(
				ConfigPropertiesHolder.getInstance().getProperties().getProperty(AppConsts.PROPERTIES_CONFIG_MOMENTUM));

		int networkSize = getNetworkStructure().size();

		for (int i = 0; i < networkSize - 1; i++) {
			ArrayList<Neuron> layer = neuralNetwork.get(i);
			ArrayList<Neuron> nextLayer = neuralNetwork.get(i + 1);

			for (Neuron neuron : layer) {

				ArrayList<Double> weights = neuron.getWeight();
				ArrayList<Double> newWeights = new ArrayList<Double>();
				for (int j = 0; j < weights.size(); j++) {

					Double newWeight = weights.get(j) - momentum * nextLayer.get(j).getDelta() * neuron.getOutput();
					newWeights.add(newWeight);
				}

				neuron.setWeight(newWeights);
			}
		}

	}

	/**
	 * 
	 * @param neuralNetwork
	 *            - the neural network
	 * @param trainingExampleDouble
	 * @return
	 */
	public static double calculateTrainError(HashMap<Integer, ArrayList<Neuron>> neuralNetwork,
			ArrayList<Double> trainingExampleDouble) {

		ArrayList<Neuron> outputLayer = neuralNetwork.get(neuralNetwork.size() - 1);
		ArrayList<Double> desiredOutput = new ArrayList<Double>();

		for (int i = 0; i < outputLayer.size(); i++) {
			int numberInputNeurons = BackPropUtils.getNetworkStructure().get(0);
			desiredOutput.add(trainingExampleDouble.get(numberInputNeurons + i));
		}

		return calculateError(outputLayer, desiredOutput);
	}

	/**
	 * It compares the output of the neural network (the output layer) with the
	 * desired output and it populates the error.
	 * 
	 * @param outputLayer
	 *            - the output layer of the neural network
	 * @param desiredOutput
	 *            - the desired output
	 * @return the error between the actual output and the desired output
	 */
	private static double calculateError(ArrayList<Neuron> outputLayer, ArrayList<Double> desiredOutput) {
		double result = 0;
		for (int i = 0; i < outputLayer.size(); i++) {
			result += Math.pow((desiredOutput.get(i) - outputLayer.get(i).getOutput()), 2);
		}

		result += result / 2;
		return result;
	}

}
