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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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

		String trainingFile = (String) ConfigPropertiesHolder.getInstance().getProperties()
				.get(AppConsts.PROPERTIES_CONFIG_TRAINING_FILE);
		String crossValidFile = (String) ConfigPropertiesHolder.getInstance().getProperties()
				.get(AppConsts.PROPERTIES_CONFIG_CROSS_VALIDATION_FILE);
		double trainingError = 0.0;
		double crossValidationError = 0.0;

		XYSeries trainErrorDataset = new XYSeries("training error");
		XYSeries crossValidErrorDataset = new XYSeries("cross validation error");

		for (int currentEpoch = 0; currentEpoch < epochs; currentEpoch++) {

			trainingError = runBackProp(neuralNetwork, true, trainingFile);
			crossValidationError = runBackProp(neuralNetwork, false, crossValidFile);

			trainErrorDataset.add(trainingError, Integer.valueOf(currentEpoch));
			crossValidErrorDataset.add(crossValidationError, Integer.valueOf(currentEpoch));

			System.out.println("Training error: " + trainingError);
			System.out.println("Cross validation error: " + crossValidationError);
			System.out.println("Epoch " + currentEpoch + " done");

		}

		createErrorChart(trainErrorDataset, crossValidErrorDataset);

	}

	private void createErrorChart(XYSeries trainErrorDataset, XYSeries crossValidErrorDataset) {
		XYSeriesCollection errorCollection = new XYSeriesCollection();
		errorCollection.addSeries(trainErrorDataset);
		errorCollection.addSeries(crossValidErrorDataset);

		// to draw the training error
		JFreeChart lineChartObject = ChartFactory.createXYLineChart("Training/Cross validation error during epochs",
				"Epoch", "Error", errorCollection, PlotOrientation.HORIZONTAL, true, true, false);
		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */
		File lineChart = new File("LineChart.jpeg");
		try {
			ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
		} catch (IOException e) {
			logger.error("Something went wrong while drawing the error chart");
		}
	}

	private double runBackProp(HashMap<Integer, ArrayList<Neuron>> neuralNetwork, boolean runBackwardPass,
			String fileName) {

		double error = 0.0;
		int lineNumber = 1;

		// Read the training file and iterate through the examples and run back
		// propagation
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fileReader);

			// iterate through the file
			for (String line; (line = br.readLine()) != null;) {

				// transform pattern from string to double
				ArrayList<Double> trainingExample = null;
				try {
					trainingExample = transformTrainingExampleToDouble(line, fileName, lineNumber);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					continue;
				}

				BackPropUtils.feedForward(neuralNetwork, trainingExample);
				if (runBackwardPass) {
					BackPropUtils.backwardPass(neuralNetwork, trainingExample);
				}
				error += BackPropUtils.calculateTrainError(neuralNetwork, trainingExample);

				lineNumber++;

			}

		} catch (IOException e) {
			logger.error("Cannot open file " + fileName, e);
		}

		return error;
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

	private ArrayList<Double> transformTrainingExampleToDouble(String line, String trainingFile, int lineNumber)
			throws Exception {
		ArrayList<Integer> networkStructure = BackPropUtils.getNetworkStructure();
		List<String> trainingExample = Arrays.asList(line.split(","));
		ArrayList<Double> result = new ArrayList<Double>();

		if (trainingExample != null && trainingExample.size() != networkStructure.get(0)
				+ networkStructure.get(networkStructure.size() - 1)) {
			throw new Exception("Problem with training example at line: " + lineNumber + "of file: " + trainingFile);

		}

		for (String example : trainingExample) {
			result.add(Double.valueOf(example));
		}
		return result;
	}

}
