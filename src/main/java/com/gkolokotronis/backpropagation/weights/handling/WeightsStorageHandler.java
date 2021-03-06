package com.gkolokotronis.backpropagation.weights.handling;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gkolokotronis.backpropagation.consts.AppConsts;
import com.gkolokotronis.backpropagation.neuron.Neuron;
import com.gkolokotronis.backpropagation.properties.ConfigPropertiesHolder;
import com.gkolokotronis.backpropagation.utils.BackPropUtils;

public class WeightsStorageHandler extends WeightsHandler {

	public WeightsStorageHandler(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		super(neuralNetwork);
	}

	@Override
	public void execute() {
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

			Element learningRateElement = doc.createElement("learningRate");
			learningRateElement.appendChild(doc.createTextNode(ConfigPropertiesHolder.getInstance().getProperties()
					.getProperty(AppConsts.PROPERTIES_CONFIG_LEARNING_RATE)));
			rootElement.appendChild(learningRateElement);

			Element layersElement = doc.createElement("layers");
			rootElement.appendChild(layersElement);

			for (int i = 0; i < networkSize; i++) {

				// adding each layer
				Element layerElement = doc.createElement("layer");
				layersElement.appendChild(layerElement);

				ArrayList<Neuron> layer = getNeuralNetwork().get(i);

				// adding the neurons of each layer
				for (Neuron neuron : layer) {

					Element neuronElement = doc.createElement("neuron");

					// adding the weights of the neuron
					Element weightsElement = doc.createElement("weights");
					neuronElement.appendChild(weightsElement);

					ArrayList<Double> neuronWeights = neuron.getWeight();

					if (neuronWeights != null) {
						for (Double weight : neuronWeights) {

							Element weightElement = doc.createElement("weight");
							weightElement.appendChild(doc.createTextNode(weight.toString()));

							weightsElement.appendChild(weightElement);
						}
					}

					// adding the output of the neuron
					Element outputElement = doc.createElement("output");
					outputElement.appendChild(doc.createTextNode(String.valueOf(neuron.getOutput())));
					neuronElement.appendChild(outputElement);

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

}
