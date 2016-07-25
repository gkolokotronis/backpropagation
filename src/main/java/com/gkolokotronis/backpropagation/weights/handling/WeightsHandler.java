package com.gkolokotronis.backpropagation.weights.handling;

import java.util.ArrayList;
import java.util.HashMap;

import com.gkolokotronis.backpropagation.neuron.Neuron;

public abstract class WeightsHandler {

	private HashMap<Integer, ArrayList<Neuron>> neuralNetwork;

	public WeightsHandler(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		this.neuralNetwork = neuralNetwork;
	}

	/**
	 * @return the neuralNetwork
	 */
	public HashMap<Integer, ArrayList<Neuron>> getNeuralNetwork() {
		return neuralNetwork;
	}

	/**
	 * @param neuralNetwork
	 *            the neuralNetwork to set
	 */
	public void setNeuralNetwork(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		this.neuralNetwork = neuralNetwork;
	}

	abstract public void execute();

}
