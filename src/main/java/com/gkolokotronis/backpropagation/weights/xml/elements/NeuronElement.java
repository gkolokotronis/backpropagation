package com.gkolokotronis.backpropagation.weights.xml.elements;

import java.util.ArrayList;

public class NeuronElement {

	ArrayList<Double> weight = new ArrayList<Double>();

	Double output;

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

	/**
	 * @return the output
	 */
	public Double getOutput() {
		return output;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput(Double output) {
		this.output = output;
	}

}
