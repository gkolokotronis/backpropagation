package com.gkolokotronis.backpropagation.weights.handling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.gkolokotronis.backpropagation.consts.AppConsts;
import com.gkolokotronis.backpropagation.neuron.Neuron;
import com.gkolokotronis.backpropagation.properties.ConfigPropertiesHolder;
import com.gkolokotronis.backpropagation.weights.xml.elements.LayerElement;
import com.gkolokotronis.backpropagation.weights.xml.elements.NeuralNetworkElement;
import com.gkolokotronis.backpropagation.weights.xml.elements.NeuronElement;
import com.gkolokotronis.backpropagation.xsd.ResourceResolver;

public class WeightsLoadingHandler extends WeightsHandler {

	final Logger logger = LogManager.getLogger(WeightsLoadingHandler.class);

	private NeuralNetworkElement neuralNetworkElement;

	public WeightsLoadingHandler(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		super(neuralNetwork);
	}

	@Override
	public void execute() {
		String xmlDistinct = ConfigPropertiesHolder.getInstance().getProperties()
				.getProperty(AppConsts.PROPERTIES_CONFIG_FILE_TO_INITIALIZE);

		neuralNetworkElement = loadInitializationWeights(xmlDistinct, AppConsts.INITIALIZATION_FILE_XSD_LOCATION);

		setNeuralNetwork(neuralNetworkElement);

	}

	private void setNeuralNetwork(NeuralNetworkElement neuralNetworkElement) {
		ConfigPropertiesHolder.getInstance().getProperties().setProperty(AppConsts.PROPERTIES_CONFIG_LEARNING_RATE,
				neuralNetworkElement.getLearningRate().toString());

		ArrayList<LayerElement> layers = neuralNetworkElement.getNeuralNetwork();

		int layerNum = 0;
		for (LayerElement layerElement : layers) {
			ArrayList<Neuron> layer = new ArrayList<Neuron>();

			for (NeuronElement neuronElement : layerElement.getNeurons()) {
				Neuron neuron = new Neuron();
				neuron.setWeight(neuronElement.getWeight());
				layer.add(neuron);
			}

			getNeuralNetwork().put(layerNum, layer);
			layerNum++;
		}
	}

	private NeuralNetworkElement loadInitializationWeights(String xmlFilePath, String xsdPath) {

		NeuralNetworkElement result = null;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(xmlFilePath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot find file " + xmlFilePath, e);
		}

		validateXMLSchema(xmlFilePath, xsdPath);
		DigesterLoader digesterLoader = DigesterLoader.newLoader(new NeuralNetworkModule());
		Digester digester = digesterLoader.newDigester();

		try {
			result = digester.parse(inputStream);
		} catch (IOException e) {
			throw new RuntimeException("Input/output error while parsing xml file " + xmlFilePath, e);
		} catch (SAXException e) {
			throw new RuntimeException("Error while parsing xml file " + xmlFilePath, e);
		}

		return result;
	}

	/**
	 * Validates the xml structure, and it validates it against the
	 * application's xsd
	 * 
	 * @param xmlPath
	 *            - xml file to be validated *
	 */
	private void validateXMLSchema(String xmlPath, String xsdPath) {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);

		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		DocumentBuilder parser = null;
		Document document = null;
		try {

			parser = builderFactory.newDocumentBuilder();

		} catch (ParserConfigurationException e) {
			throw new RuntimeException(
					"Something went wrong while loading xsd file " + xsdPath + " from the root of the jar file", e);
		}

		// parse the XML into a document object
		File xmlFile = new File(xmlPath);
		FileInputStream xmlFileInput = null;
		try {
			xmlFileInput = new FileInputStream(xmlFile);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Cannot find file " + xmlPath, e);
		}

		try {
			document = parser.parse(xmlFileInput);
		} catch (SAXException | IOException e) {
			logger.error("Something went wrong while parsing XML file " + xmlPath + " to DOM object");
			throw new IllegalArgumentException(
					"Something went wrong while parsing XML file " + xmlPath + " to DOM object", e);
		}

		// associate the schema factory with the resource resolver, which is
		// responsible for resolving the imported XSD's
		factory.setResourceResolver(new ResourceResolver());

		Source schemaFile = new StreamSource(WeightsLoadingHandler.class.getClassLoader().getResourceAsStream(xsdPath));
		Schema schema = null;

		try {
			schema = factory.newSchema(schemaFile);
		} catch (SAXException e) {
			throw new IllegalArgumentException("Something went wrong while parsing XSD file " + xsdPath, e);
		}

		Validator validator = schema.newValidator();

		try {
			validator.validate(new DOMSource(document));
		} catch (SAXException | IOException e) {
			throw new RuntimeException("Error while validating file: " + xmlPath + " with " + xsdPath);
		}

	}

}
