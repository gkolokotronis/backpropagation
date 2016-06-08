package com.gkolokotronis.backpropagation;

import java.util.ArrayList;
import java.util.HashMap;

import com.gkolokotronis.backpropagation.neuron.Neuron;
import com.gkolokotronis.backpropagation.utils.BackPropUtils;

/**
 * General Class which is used to config and initialize the backpropagation
 * algorithm
 * 
 * @author George Kolokotronis
 *
 */
// TODO SET THE log4j2.xml
public class BackPropagation {

	public static void main(String[] args) {

		BackPropagationExecutionHandler backprop = new BackPropagationExecutionHandler();
		backprop.execute();

		HashMap<Integer, ArrayList<Neuron>> layers = new HashMap<Integer, ArrayList<Neuron>>();

		ArrayList<Neuron> layer1 = new ArrayList<Neuron>();
		Neuron neuron = new Neuron();
		ArrayList<Double> weights = new ArrayList<Double>();
		weights.add(20.0);
		neuron.setWeight(weights);
		neuron.setOutput(1.0);
		layer1.add(neuron);

		Neuron neuron2 = new Neuron();
		ArrayList<Double> weights2 = new ArrayList<Double>();
		weights2.add(20.0);
		neuron2.setWeight(weights2);
		neuron2.setOutput(0.0);
		layer1.add(neuron2);

		Neuron neuron3 = new Neuron();
		ArrayList<Double> weights3 = new ArrayList<Double>();
		weights3.add(-30.0);
		neuron3.setWeight(weights3);
		neuron3.setOutput(1.0);
		layer1.add(neuron3);

		System.out.println(String.format("%.12f", BackPropUtils.sigmoidOutput(0, 1.0, layer1)));

	}

}
