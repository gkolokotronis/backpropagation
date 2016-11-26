package com.gkolokotronis.backpropagation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.gkolokotronis.backpropagation.neuron.Neuron;
import com.gkolokotronis.backpropagation.utils.BackPropUtils;

public class XORExecutionHandler extends BackPropagationExecutionHandler {

	@Override
	protected void testNeuralNetwork(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		while (true) {
			Scanner reader = new Scanner(System.in); // Reading from System.in
			ArrayList<Double> input = new ArrayList<Double>();

			System.out.println("Enter 1st number: ");
			double n = reader.nextDouble();

			input.add(n);
			System.out.println("Enter 2nd number: ");
			n = reader.nextDouble();

			input.add(n);

			BackPropUtils.forwardPass(neuralNetwork, input);
			System.out.println(neuralNetwork);
			input.clear();
		}

	}

}
