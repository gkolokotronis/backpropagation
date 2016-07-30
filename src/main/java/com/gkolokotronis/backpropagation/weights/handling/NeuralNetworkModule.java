package com.gkolokotronis.backpropagation.weights.handling;

import org.apache.commons.digester3.binder.AbstractRulesModule;

import com.gkolokotronis.backpropagation.weights.xml.elements.LayerElement;
import com.gkolokotronis.backpropagation.weights.xml.elements.NeuralNetworkElement;
import com.gkolokotronis.backpropagation.weights.xml.elements.NeuronElement;

/**
 * 
 * @author George Kolokotronis
 * 
 *         Class which contains all the patterns of the xml and the connecting
 *         Classes
 *
 */
public class NeuralNetworkModule extends AbstractRulesModule {

	@Override
	protected void configure() {

		forPattern("neuralNetwork").createObject().ofType(NeuralNetworkElement.class).then().setProperties();

		forPattern("neuralNetwork/learningRate").callMethod("setLearningRate").withParamCount(1)
				.withParamTypes(Double.class).then().callParam();

		forPattern("neuralNetwork/layers/layer").createObject().ofType(LayerElement.class).then().setProperties().then()
				.setNext("addLayer");

		forPattern("neuralNetwork/layers/layer/neuron").createObject().ofType(NeuronElement.class).then()
				.setProperties().then().setNext("addNeuron");

		forPattern("neuralNetwork/layers/layer/neuron/weights/weight").callMethod("addWeight").withParamCount(1)
				.withParamTypes(Double.class).then().callParam();

		forPattern("neuralNetwork/layers/layer/neuron/output").callMethod("setOutput").withParamCount(1)
				.withParamTypes(Double.class).then().callParam();
	}
}
