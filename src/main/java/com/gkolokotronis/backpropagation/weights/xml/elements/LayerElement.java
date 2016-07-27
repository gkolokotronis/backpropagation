package com.gkolokotronis.backpropagation.weights.xml.elements;

import java.util.ArrayList;

public class LayerElement {

	ArrayList<NeuronElement> neurons = new ArrayList<NeuronElement>();

	/**
	 * @return the layer neurons
	 */
	public ArrayList<NeuronElement> Neurons() {
		return neurons;
	}

	/**
	 * @param neurons
	 *            the layer neurons
	 */
	public void setNeurons(ArrayList<NeuronElement> neurons) {
		this.neurons = neurons;
	}

	public void addNeuron(NeuronElement layer) {
		neurons.add(layer);
	}

}
