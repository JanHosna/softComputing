/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.ekf.hos0052.softComputing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author hos0052
 */
public class BackPropagation {

    private Layer inputLayer;
    private Double learningRate;
    private int maxEpochs;

    public BackPropagation(InputLayer inputLayer, Double learningRule, int maxEpochs) {
        this.inputLayer = inputLayer;
        this.learningRate = learningRule;
        this.maxEpochs = maxEpochs;
    }

    public void train(List<DataSetRow> trainingSet) {
        int epochs = 0;

        while (epochs < maxEpochs) {

            System.out.println("Training epoch " + epochs);

            for (DataSetRow row : trainingSet) {

                // forward propagation
                // calculate input inputLayer result (only takes input and sets it as output)
                final List<Double> calcInputLayerOutput = inputLayer.calulcateOutput(row.getInputs());
                final Layer hiddenLayer = inputLayer.getNextLayer();
                // calculate hidden layer results
                final List<Double> calculatedHiddenLayerOutput = hiddenLayer.calulcateOutput(calcInputLayerOutput);
                final Layer outputLayer = hiddenLayer.getNextLayer();
                // calculate neural network output
                final List<Double> calcOutput = outputLayer.calulcateOutput(calculatedHiddenLayerOutput);

                // back propagation
                // for each output layer outputNeuron
                for (int k = 0; k < calcOutput.size(); k++) {
                    final Double expected = row.getExpectedOutputs().get(k);
                    final Double calculated = calcOutput.get(k);
                    // calculate "global error"
                    final Double diff = expected - calculated;

                    // get outputNeuron for current iteration and derivate its activation function
                    // using input as a function argument
                    final Neuron outputNeuron = outputLayer.getNeurons().get(k);
                    final Double derivative = outputNeuron.derivate();

                    // set "delta error" as output outputNeuron error
                    outputNeuron.setError(diff * derivative);

                    // adapt output outputNeuron's bias
                    // bias = bias + (η * e * ψ’(u_3))
                    outputNeuron.setBias(outputNeuron.getBias() + learningRate * outputNeuron.getError());

                    // for each connection between hidden and output layer neurons
                    for (int i = 0; i < outputNeuron.getInput().size(); i++) {
                        // adapt weights (v)
                        // v_i = v_i + (η * o_i * e * ψ’(u_3))
                        outputNeuron.getWeight().set(i, outputNeuron.getWeight().get(i) + learningRate * outputNeuron.getInput().get(i) * outputNeuron.getError());

                        Neuron hiddenNeuron = hiddenLayer.getNeurons().get(i);
                        hiddenNeuron.setError(outputNeuron.getError() * hiddenNeuron.derivate() * outputNeuron.getWeight().get(i));
                        // adapt hidden neuron's bias
                        // bias = bias + (η * e * ψ’(u_3) * ψ´2 (u_i ) * v_i)
                        hiddenNeuron.setBias(hiddenNeuron.getBias() + learningRate * hiddenNeuron.getError());
                        for (int j = 0; j < hiddenNeuron.getInput().size(); j++) {
                            // adapt weights (w)
                            // w_ji = w_ji + (η * x_j * e * ψ’(u_3) * ψ´2 (u_i) * v_i )
                            hiddenNeuron.getWeight().set(j, hiddenNeuron.getWeight().get(j) + learningRate * hiddenNeuron.getInput().get(j) * hiddenNeuron.getError());
                        }
                    }
                }

            }
            epochs++;

        }
    }

    public void calculateMSE(List<DataSetRow> trainingSet) {

        final List<Double> allExpected = new ArrayList<>(trainingSet.size());
        final List<Double> allCalculated = new ArrayList<>(trainingSet.size());

        for (DataSetRow row : trainingSet) {

            // forward propagation
            // calculate input inputLayer result (only takes input and sets it as output)
            final List<Double> calcInputLayerOutput = inputLayer.calulcateOutput(row.getInputs());
            final Layer hiddenLayer = inputLayer.getNextLayer();
            // calculate hidden layer results
            final List<Double> calculatedHiddenLayerOutput = hiddenLayer.calulcateOutput(calcInputLayerOutput);
            final Layer outputLayer = hiddenLayer.getNextLayer();
            // calculate neural network output
            final List<Double> calcOutput = outputLayer.calulcateOutput(calculatedHiddenLayerOutput);

            // add expected result and actual result to lists, for MSE calculation later on
            allCalculated.addAll(calcOutput);
            allExpected.addAll(row.getExpectedOutputs());
        }

        Double mse = MSE(allExpected, allCalculated);
        System.out.printf("MSE %.10f\n", mse);

    }

    private Double MSE(List<Double> expected, List<Double> calculated) {
        final double mse = IntStream.range(0, Math.min(expected.size(), calculated.size()))
                .mapToDouble(i -> Math.pow(expected.get(i) - calculated.get(i), 2.0)).average().orElse(0.0);
        return mse;
    }
}
