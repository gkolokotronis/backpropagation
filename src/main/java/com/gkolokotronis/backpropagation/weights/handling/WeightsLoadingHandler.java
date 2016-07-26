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
import org.apache.logging.log4j.Level;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.gkolokotronis.backpropagation.consts.AppConsts;
import com.gkolokotronis.backpropagation.neuron.Neuron;
import com.gkolokotronis.backpropagation.weights.xml.elements.NeuralNetworkElement;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;

public class WeightsLoadingHandler extends WeightsHandler {

	private NeuralNetworkElement neuralNetworkElement;

	public WeightsLoadingHandler(HashMap<Integer, ArrayList<Neuron>> neuralNetwork) {
		super(neuralNetwork);
	}

	@Override
	public void execute() {
		neuralNetworkElement = loadInitializationWeights(xmlDistinct, AppConsts.CUSTOM_XSD_LOCATION);

	}

	private static NeuralNetworkElement loadInitializationWeights(String xmlFilePath, String xsdPath) {

		NeuralNetworkElement result = null;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(xmlFilePath);
		} catch (FileNotFoundException e) {
			throw ExceptionFactory.createException(RuntimeException.class, MessageCodes.ERR_CANNOT_FIND_FILE, e, logger,
					Level.ERROR, xmlFilePath);
		}

		validateXMLSchema(xmlFilePath, xsdPath);
		DigesterLoader digesterLoader = DigesterLoader.newLoader(new CustomModule());
		Digester digester = digesterLoader.newDigester();

		try {
			result = digester.parse(inputStream);
		} catch (IOException e) {
			throw ExceptionFactory.createException(RuntimeException.class, MessageCodes.ERR_INP_OUT_WHILE_PARSING_XML,
					e, logger, Level.ERROR, xmlFilePath);
		} catch (SAXException e) {
			throw ExceptionFactory.createException(RuntimeException.class, MessageCodes.ERR_WHILE_PARSING_XML, e,
					logger, Level.ERROR, xmlFilePath);
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
	public static void validateXMLSchema(String xmlPath, String xsdPath) {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);

		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		DocumentBuilder parser = null;
		Document document = null;
		try {

			parser = builderFactory.newDocumentBuilder();

		} catch (ParserConfigurationException e) {
			throw ExceptionFactory.createException(RuntimeException.class, MessageCodes.ERR_WHILE_LOADING_XSD, e,
					logger, Level.ERROR, xsdPath);
		}

		// parse the XML into a document object
		File xmlFile = new File(xmlPath);
		FileInputStream xmlFileInput = null;
		try {
			xmlFileInput = new FileInputStream(xmlFile);
		} catch (FileNotFoundException e) {
			throw ExceptionFactory.createException(IllegalArgumentException.class, MessageCodes.ERR_CANNOT_FIND_FILE, e,
					logger, Level.ERROR, xmlPath);
		}

		try {
			document = parser.parse(xmlFileInput);
		} catch (SAXException | IOException e) {

			throw ExceptionFactory.createException(IllegalArgumentException.class,
					MessageCodes.ERR_WHILE_LOADING_PARSING_XML_TO_DOM, e, logger, Level.ERROR, xmlPath);
		}

		// associate the schema factory with the resource resolver, which is
		// responsible for resolving the imported XSD's
		factory.setResourceResolver(new ResourceResolver());

		Source schemaFile = new StreamSource(ChecksCreatorUtils.class.getClassLoader().getResourceAsStream(xsdPath));
		Schema schema = null;

		try {
			schema = factory.newSchema(schemaFile);
		} catch (SAXException e) {
			throw ExceptionFactory.createException(IllegalArgumentException.class,
					MessageCodes.ERR_WHILE_LOADING_PARSING_XSD, e, logger, Level.ERROR, xsdPath);

		}

		Validator validator = schema.newValidator();

		try {
			validator.validate(new DOMSource(document));
		} catch (SAXException | IOException e) {
			throw ExceptionFactory.createException(RuntimeException.class,
					MessageCodes.ERR_WHILE_VALIDATING_XML_WITH_XSD, e, logger, Level.ERROR, xmlPath, xsdPath);
		}

	}

}
