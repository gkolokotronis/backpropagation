package com.gkolokotronis.backpropagation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

		initializeLayers(neuralNetwork);
		trainNeuralNetwork(neuralNetwork);
		storeFinalWeights(neuralNetwork);
		// testNeuralNetwork(neuralNetwork);

	}

	/**
	 * It stores the final weights of the neural network to the file specified
	 * in the application.properties. In case the file exists, it overwrites it
	 * 
	 * @param neuralNetwork
	 *            - the neuralNetwork
	 */
	private void storeFinalWeights(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		String fileName = ConfigPropertiesHolder.getInstance().getProperties()
				.getProperty(AppConsts.PROPERTIES_CONFIG_FINAL_WEIGHTS_FILE);

		int networkSize = BackPropUtils.getNetworkStructure().size();

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("neuralNetwork");
			doc.appendChild(rootElement);

			for (int i = 0; i < networkSize; i++) {

				Element layerElement = doc.createElement("layer");
				rootElement.appendChild(layerElement);

				ArrayList<Neuron> layer = neuralNetwork.get(i);

				for (Neuron neuron : layer) {
					Element neuronElement = doc.createElement("Neuron");

					ArrayList<Double> neuronWeights = neuron.getWeight();

					if (neuronWeights != null) {
						for (Double weight : neuronWeights) {

							neuronElement.setAttribute("weight", weight.toString());

						}
					}

					layerElement.appendChild(neuronElement);

				}

			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			// in order to add spaces when writing to xml
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fileName));

			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

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
			System.out.println("Training file done");

			crossValidationError = runBackProp(neuralNetwork, false, crossValidFile);
			System.out.println("Cross valid file done");

			trainErrorDataset.add(trainingError, currentEpoch);
			crossValidErrorDataset.add(crossValidationError, currentEpoch);

			System.out.println("Epoch " + currentEpoch + " done                  ");
			System.out.println("Training error: " + trainingError);
			System.out.println("Cross validation error: " + crossValidationError);

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

	private void testNeuralNetwork(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		while (true) {
			// testXOR(neuralNetwork);
			testHandWritten(neuralNetwork);
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

	private void testXOR(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
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

	private void testHandWritten(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
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

				System.out.println("");
				System.out.println("Press enter to predict");
				reader.nextLine();
				BackPropUtils.forwardPass(neuralNetwork, input);

				ArrayList<Neuron> output = neuralNetwork.get(BackPropUtils.getNetworkStructure().size() - 1);

				System.out.println("Prediction:");

				for (int i = 0; i < output.size(); i++) {
					if (i == 9) {
						System.out.println("0: " + String.format("%.6f", output.get(i).getOutput()));
					} else {
						System.out.println(i + 1 + ": " + String.format("%.6f", output.get(i).getOutput()));
					}
				}

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

		BackPropUtils.forwardPass(neuralNetwork, input);
		System.out.println(neuralNetwork);
		input.clear();
	}
}
