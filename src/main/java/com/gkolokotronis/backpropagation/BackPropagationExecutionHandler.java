package com.gkolokotronis.backpropagation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.gkolokotronis.backpropagation.consts.AppConsts;
import com.gkolokotronis.backpropagation.neuron.Neuron;
import com.gkolokotronis.backpropagation.properties.ConfigPropertiesHolder;
import com.gkolokotronis.backpropagation.utils.BackPropUtils;

/**
 * This class is responsible for handling the execution of the application
 * 
 * @author George Kolokotronis
 *
 */
public class BackPropagationExecutionHandler {

	final Logger logger = LogManager.getLogger(BackPropagationExecutionHandler.class);

	public void execute() {
		HashMap<Integer, ArrayList<Neuron>> neuralNetwork = new HashMap<Integer, ArrayList<Neuron>>();

		logger.debug("Validate properties file");
		validatePropertiesFile();
		initializeLayers(neuralNetwork);
		trainNeuralNetwork(neuralNetwork);
		testNeuralNetwork(neuralNetwork);

	}

	private void validatePropertiesFile() {
		// TODO validate the properties file
		Properties properties = ConfigPropertiesHolder.getInstance().getProperties();
		System.out.println(properties.entrySet());
	}

	private void initializeLayers(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		ArrayList<Integer> networkStructure = BackPropUtils.getNetworkStructure();

		BackPropUtils.initializeLayers(neuralNetwork);
		BackPropUtils.initializeWeights(networkStructure, neuralNetwork);

	}

	private void trainNeuralNetwork(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		int epochs = Integer.valueOf(ConfigPropertiesHolder.getInstance().getProperties()
				.getProperty(AppConsts.PROPERTIES_CONFIG_NUMBER_EPOCH));

		DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

		for (int currentEpoch = 0; currentEpoch < epochs; currentEpoch++) {

			double trainingError = 0.0;
			String trainingFile = (String) ConfigPropertiesHolder.getInstance().getProperties()
					.get(AppConsts.PROPERTIES_CONFIG_TRAINING_FILE);

			ArrayList<Integer> networkStructure = BackPropUtils.getNetworkStructure();

			FileReader fileReader = null;
			try {
				fileReader = new FileReader(trainingFile);
				BufferedReader br = new BufferedReader(fileReader);
				int lineNumber = 1;

				for (String line; (line = br.readLine()) != null;) {

					List<String> trainingExample = Arrays.asList(line.split(","));
					if (trainingExample != null && trainingExample.size() != networkStructure.get(0)
							+ networkStructure.get(networkStructure.size() - 1)) {
						logger.error(
								"Problem with training example at line: " + lineNumber + "of file: " + trainingFile);
						continue;
					}
					ArrayList<Double> trainingExampleDouble = new ArrayList<Double>();

					for (String example : trainingExample) {
						trainingExampleDouble.add(Double.valueOf(example));
					}

					BackPropUtils.feedForward(neuralNetwork, trainingExampleDouble);

					BackPropUtils.backwardPass(neuralNetwork, trainingExampleDouble);
					// find train error
					ArrayList<Neuron> outputLayer = neuralNetwork.get(neuralNetwork.size() - 1);
					ArrayList<Double> desiredOutput = new ArrayList<Double>();
					for (int i = 0; i < outputLayer.size(); i++) {
						int numberInputNeurons = BackPropUtils.getNetworkStructure().get(0);
						desiredOutput.add(trainingExampleDouble.get(numberInputNeurons + i));
					}
					trainingError += BackPropUtils.calculateError(outputLayer, desiredOutput);

				}

				// find cross validation error

				line_chart_dataset.addValue(trainingError, "training error", Integer.valueOf(currentEpoch));

				System.out.println("Training error: " + trainingError);
				System.out.println("Epoch " + currentEpoch + " done");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// to draw the training error
		JFreeChart lineChartObject = ChartFactory.createLineChart("Training/Cross validation error during epochs",
				"Epoch", "Training Error", line_chart_dataset, PlotOrientation.VERTICAL, true, true, false);

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */
		File lineChart = new File("LineChart.jpeg");
		try {
			ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void testNeuralNetwork(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		Scanner reader = new Scanner(System.in); // Reading from System.in
		ArrayList<Double> input = new ArrayList<Double>();
		while (true) {
			System.out.println("Enter 1st number: ");
			double n = reader.nextDouble();

			input.add(n);
			System.out.println("Enter 2nd number: ");
			n = reader.nextDouble();

			input.add(n);

			BackPropUtils.feedForward(neuralNetwork, input);
			System.out.println(neuralNetwork);
			input.clear();
		}
	}

}
