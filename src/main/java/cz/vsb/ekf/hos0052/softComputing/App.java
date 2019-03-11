package cz.vsb.ekf.hos0052.softComputing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        final int numberOfHiddenNeurons = 5;
        final double learningRule = 3.0;
        final double targetMSE = 0.0000000005;
        final int maxEpochs = 10000;


        final List<DataSetRow> dataSet = createDataSet();
        final InputLayer inputLayer = createInputLayer();
        final HiddenLayer hiddenLayer = createHiddenLayer(numberOfHiddenNeurons);
        final OutputLayer outputLayer = createOutputLayer(numberOfHiddenNeurons);

        inputLayer.setNextLayer(hiddenLayer);
        hiddenLayer.setPreviousLayer(inputLayer);
        hiddenLayer.setNextLayer(outputLayer);
        outputLayer.setPreviousLayer(hiddenLayer);

        System.out.println("Original values");
        calculateAndPrintLayerOutputs(dataSet, inputLayer, hiddenLayer, outputLayer);

        final DeltaRule deltarule = new DeltaRule(inputLayer, learningRule, targetMSE, maxEpochs);
        deltarule.train(dataSet);

        System.out.println("trained network values");
        calculateAndPrintLayerOutputs(dataSet, inputLayer, hiddenLayer, outputLayer);
    }

    private static void calculateAndPrintLayerOutputs(List<DataSetRow> dataset, Layer inputLayer, HiddenLayer hiddenLayer, OutputLayer outputLayer) {

        for (DataSetRow row : dataset) {
            final List<Double> calculatedInputLayerOutput = inputLayer.calulcateOutput(row.getInputs());
            final List<Double> calculatedHiddenLayerOutput = hiddenLayer.calulcateOutput(calculatedInputLayerOutput);
            final List<Double> calculatedOutput = outputLayer.calulcateOutput(calculatedHiddenLayerOutput);
            System.out.printf("%s, %s -> %s\n", row.getInputs(), row.getExpectedOutputs(), calculatedOutput);
        }
    }


    private static List<DataSetRow> createDataSet() {
//        List<DataSetRow> dataSet = new ArrayList<>(4);
//        double j = -0.1;
//        while (j <= 0.09) {
//            dataSet.add(new DataSetRow(Arrays.asList(j), Arrays.asList(Math.sinh(j))));
//            j = j + 0.01;
//        }

        List<DataSetRow> dataSet = new ArrayList<>(4);
        double j = -5;
        while (j <= 5) {
            dataSet.add(new DataSetRow(Arrays.asList(j/100), Arrays.asList(Math.sinh(j)/100)));
            j = j + 1;
        }

        return dataSet;
    }

    private static InputLayer createInputLayer() {
        List<Double> weights = new ArrayList<>(2);
        weights.add(1.0);
        // weights.add(0.5);
        InputNeuron neuron = new InputNeuron(weights, new ActivationTanh(1.0));
        InputLayer layer = new InputLayer(new ArrayList<>(Arrays.asList(neuron)), null, null);
        return layer;


    }

    private static HiddenLayer createHiddenLayer(int numberOfHiddenNeurons) {
        List<Double> weights = new ArrayList<>(2);
        weights.add(Math.random());

        ArrayList<Neuron> hiddenNeurons = new ArrayList<>(numberOfHiddenNeurons);
        for (int i = 0; i < numberOfHiddenNeurons; i++) {
            Neuron neuron = new Neuron(weights, 0.0, new ActivationTanh(1.0));
            hiddenNeurons.add(neuron);
        }
        HiddenLayer layer = new HiddenLayer(hiddenNeurons, null, null);
        return layer;
    }

    private static OutputLayer createOutputLayer(int numberOfHiddenNeurons) {
        List<Double> weights = new ArrayList<>(2);
        for (int i = 0; i < numberOfHiddenNeurons; i++) {
            weights.add(Math.random());
        }
        Neuron neuron = new Neuron(weights, 0.0, new ActivationTanh(1.0));
        OutputLayer layer = new OutputLayer(new ArrayList<>(Arrays.asList(neuron)), null, null);
        return layer;

    }
}
