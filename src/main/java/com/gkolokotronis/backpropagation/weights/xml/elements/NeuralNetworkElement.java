package com.gkolokotronis.backpropagation.weights.xml.elements;

import java.util.ArrayList;

/**
 * Represents <code>neuralNetwork</code> element of the xml containg the weights
 * of a neural netowrk,
 * 
 * @author George Kolokotronis
 *
 */
public class NeuralNetworkElement {
	ArrayList<LayerElement> neuralNetwork = new ArrayList<LayerElement>();

	Double learningRate;

	/**
	 * @return the neuralNetwork
	 */
	public ArrayList<LayerElement> getNeuralNetwork() {
		return neuralNetwork;
	}

	/**
	 * @param neuralNetwork
	 *            the neuralNetwork to set
	 */
	public void setNeuralNetwork(ArrayList<LayerElement> neuralNetwork) {
		this.neuralNetwork = neuralNetwork;
	}

	public void addLayer(LayerElement layer) {
		neuralNetwork.add(layer);
	}

	/**
	 * @return the learningRate
	 */
	public Double getLearningRate() {
		return learningRate;
	}

	/**
	 * @param learningRate
	 *            the learningRate to set
	 */
	public void setLearningRate(Double learningRate) {
		this.learningRate = learningRate;
	}

}