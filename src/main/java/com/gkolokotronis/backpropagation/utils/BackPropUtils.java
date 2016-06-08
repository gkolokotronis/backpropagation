package com.gkolokotronis.backpropagation.utils;

import java.util.ArrayList;

import com.gkolokotronis.backpropagation.neuron.Neuron;

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
}
