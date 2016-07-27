package com.gkolokotronis.backpropagation.weights.xml.elements;

import java.util.ArrayList;

public class NeuronElement {

	ArrayList<Double> weight = new ArrayList<Double>();

	/**
	 * @return the weight
	 */
	public ArrayList<Double> getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(ArrayList<Double> weight) {
		this.weight = weight;
	}

	public void addWeight(Double weight) {
		this.weight.add(weight);
	}

}
