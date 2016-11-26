package com.gkolokotronis.backpropagation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
import com.gkolokotronis.backpropagation.weights.handling.WeightsLoadingHandler;
import com.gkolokotronis.backpropagation.weights.handling.WeightsStorageHandler;

/**
 * This class is responsible for handling the execution of the application
 * 
 * @author George Kolokotronis
 *
 */
abstract public class BackPropagationExecutionHandler {

	final Logger logger = LogManager.getLogger(BackPropagationExecutionHandler.class);

	public void execute() {
		HashMap<Integer, ArrayList<Neuron>> neuralNetwork = new HashMap<Integer, ArrayList<Neuron>>();

		String initializationFile = (String) ConfigPropertiesHolder.getInstance().getProperties()
				.get(AppConsts.PROPERTIES_CONFIG_FILE_TO_INITIALIZE);

		if (initializationFile == null || initializationFile.length() == 0) {
			initializeLayers(neuralNetwork);
			trainNeuralNetwork(neuralNetwork);
			storeFinalWeights(neuralNetwork);
			testNeuralNetwork(neuralNetwork);
		} else {
			WeightsLoadingHandler weightsLoadingHandler = new WeightsLoadingHandler(neuralNetwork);
			weightsLoadingHandler.execute();
			testNeuralNetwork(neuralNetwork);
		}

	}

	/**
	 * It stores the final weights of the neural network to the file specified
	 * in the application.properties. In case the file exists, it overwrites it
	 * 
	 * @param neuralNetwork
	 *            - the neuralNetwork
	 */
	private void storeFinalWeights(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		WeightsStorageHandler weightsStorage = new WeightsStorageHandler(neuralNetwork);

		weightsStorage.execute();

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

		PrintWriter writerTrainError = null;
		PrintWriter writerCrossValidError = null;
		try {
			writerTrainError = new PrintWriter("train_error.csv", "UTF-8");
			writerCrossValidError = new PrintWriter("cross_valid_error.csv", "UTF-8");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int currentEpoch = 0; currentEpoch < epochs; currentEpoch++) {

			crossValidationError = runBackProp(neuralNetwork, false, crossValidFile);
			System.out.println("Cross valid file done");

			trainingError = runBackProp(neuralNetwork, true, trainingFile);
			System.out.println("Training file done");

			trainErrorDataset.add(trainingError, currentEpoch);
			writerTrainError.println(trainingError + "," + currentEpoch);

			crossValidErrorDataset.add(crossValidationError, currentEpoch);
			writerCrossValidError.println(crossValidationError + "," + currentEpoch);

			System.out.println("Epoch " + currentEpoch + " done                  ");
			System.out.println("Training error: " + trainingError);
			System.out.println("Cross validation error: " + crossValidationError);

		}
		writerTrainError.close();
		writerCrossValidError.close();
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
		int lineNumber = 0;

		// Read the file and iterate through the examples and run back
		// propagation
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fileReader);

			// iterate through the file
			double percentage = 0.0;

			for (String line; (line = br.readLine()) != null;) {

				// transform pattern from string to double
				ArrayList<Double> trainingExample = null;
				try {
					trainingExample = transformTrainingExampleToDouble(line, fileName, lineNumber);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					continue;
				}

				BackPropUtils.forwardPass(neuralNetwork, trainingExample);
				if (runBackwardPass) {
					BackPropUtils.backwardPass(neuralNetwork, trainingExample);
				}
				error += BackPropUtils.calculateTrainError(neuralNetwork, trainingExample);

				lineNumber++;
				if (runBackwardPass) {
					percentage = (lineNumber * 100) / 46710;
				} else {
					percentage = (lineNumber * 100) / 8920;
				}
				System.out.print(percentage + " ");
				if (percentage <= 10) {
					System.out.print("|          |\r");
				} else if (percentage > 10 && percentage <= 20) {
					System.out.print("|+         |\r");
				} else if (percentage > 20 && percentage <= 30) {
					System.out.print("|++        |\r");
				} else if (percentage > 30 && percentage <= 40) {
					System.out.print("|+++       |\r");
				} else if (percentage > 40 && percentage <= 50) {
					System.out.print("|++++      |\r");
				} else if (percentage > 50 && percentage <= 60) {
					System.out.print("|+++++     |\r");
				} else if (percentage > 60 && percentage <= 70) {
					System.out.print("|++++++    |\r");
				} else if (percentage > 70 && percentage <= 80) {
					System.out.print("|+++++++   |\r");
				} else if (percentage > 80 && percentage <= 90) {
					System.out.print("|++++++++  |\r");
				} else if (percentage > 90 && percentage < 100) {
					System.out.print("|+++++++++ |\r");
				} else if (percentage == 100) {
					System.out.print("|++++++++++|\r");
				}

			}

			error = error / lineNumber;

		} catch (IOException e) {
			logger.error("Cannot open file " + fileName, e);
		}

		return error;
	}

	abstract protected void testNeuralNetwork(HashMap<Integer, ArrayList<Neuron>> neuralNetwork);

	protected ArrayList<Double> transformTrainingExampleToDouble(String line, String trainingFile, int lineNumber)
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
