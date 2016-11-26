package com.gkolokotronis.backpropagation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.gkolokotronis.backpropagation.consts.AppConsts;
import com.gkolokotronis.backpropagation.neuron.Neuron;
import com.gkolokotronis.backpropagation.properties.ConfigPropertiesHolder;
import com.gkolokotronis.backpropagation.utils.BackPropUtils;

public class HandWrittenBPExecutionHandler extends BackPropagationExecutionHandler {

	@Override
	protected void testNeuralNetwork(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		while (true) {

			Scanner reader = new Scanner(System.in); // Reading from System.in
			String crossValidFile = (String) ConfigPropertiesHolder.getInstance().getProperties()
					.get(AppConsts.PROPERTIES_CONFIG_TEST_FILE);
			ArrayList<Double> input = new ArrayList<Double>();
			FileReader fileReader;
			try {
				fileReader = new FileReader(crossValidFile);

				BufferedReader br = new BufferedReader(fileReader);

				// iterate through the file
				int lineNumber = 0;
				for (String line; (line = br.readLine()) != null;) {

					input = transformTrainingExampleToDouble(line, crossValidFile, lineNumber);

					for (int cnt = 0; cnt < 28; cnt++) {
						for (int cnt2 = 0; cnt2 < 28; cnt2++) {
							if (input.get(28 * cnt + cnt2) > 0.5) {
								System.out.print("X");
							} else {
								System.out.print(" ");
							}
						}
						System.out.println("");

					}

					ArrayList<Double> number = new ArrayList<Double>();
					for (int cnt3 = 0; cnt3 < 10; cnt3++) {
						// System.out.print(input.get(28 * 28 + cnt3) + " ");
						number.add(input.get(28 * 28 + cnt3));
					}

					for (int i = 0; i < number.size(); i++) {
						if (number.get(i) == 1.0) {
							if (i == 9) {
								System.out.println("0");
							} else {
								System.out.println(i + 1);
							}
						}
					}

					System.out.println("Press enter to predict");
					reader.nextLine();
					BackPropUtils.forwardPass(neuralNetwork, input);

					ArrayList<Neuron> output = neuralNetwork.get(BackPropUtils.getNetworkStructure().size() - 1);

					/*
					 * System.out.println("Prediction:");
					 * 
					 * for (int i = 0; i < output.size(); i++) { if (i == 9) {
					 * System.out.println("0: " + String.format("%.6f",
					 * output.get(i).getOutput())); } else {
					 * System.out.println(i + 1 + ": " + String.format("%.6f",
					 * output.get(i).getOutput())); } }
					 */

					int prediction = 0;
					double max = -1.0;
					int i = 0;
					for (Neuron neuron : output) {
						if (neuron.getOutput() > max) {
							max = neuron.getOutput();
							prediction = i;
						}
						i++;
					}

					System.out.print("Prediction is : ");
					switch (prediction) {
					case 0:
						System.out.println("1");
						break;
					case 1:

						System.out.println("2");
						break;
					case 2:
						System.out.println("3");
						break;
					case 3:
						System.out.println("4");
						break;
					case 4:
						System.out.println("5");
						break;
					case 5:
						System.out.println("6");
						break;
					case 6:
						System.out.println("7");
						break;
					case 7:
						System.out.println("8");
						break;
					case 8:
						System.out.println("9");
						break;
					case 9:
						System.out.println("0");
						break;

					}
					input.clear();

					System.out.println("Press enter for the next number");
					reader.nextLine();
					lineNumber++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
