package com.gkolokotronis.backpropagation.neuron;

import java.util.ArrayList;

public class Neuron {

	ArrayList<Double> weight;
	double output;
	double delta;

	/**
	 * @return the output
	 */
	public double getOutput() {
		return output;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput(double output) {
		this.output = output;
	}

	/**
	 * @return the delta
	 */
	public double getDelta() {
		return delta;
	}

	/**
	 * @param delta
	 *            the delta to set
	 */
	public void setDelta(double delta) {
		this.delta = delta;
	}

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

}
