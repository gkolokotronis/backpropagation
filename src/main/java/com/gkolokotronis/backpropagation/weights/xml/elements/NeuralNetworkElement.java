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
}