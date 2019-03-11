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
public class DeltaRule {

    private Layer layer;
    private Double learningRate;
    private Double targetMse;
    private int maxEpochs;

    public DeltaRule(InputLayer layer, Double learningRule, Double targetMse, int maxEpochs) {
        this.layer = layer;
        this.learningRate = learningRule;
        this.targetMse = targetMse;
        this.maxEpochs = maxEpochs;
    }

    public void train(List<DataSetRow> trainingSet) {
        int epochs = 0;
        Double mse = Double.MAX_VALUE;

        while (epochs < maxEpochs && mse > targetMse) {

            final List<Double> allExpected = new ArrayList<>(trainingSet.size());
            final List<Double> allCalculated = new ArrayList<>(trainingSet.size());


            for (DataSetRow row : trainingSet) {

                // forward propagation
                // calculate input layer result (only takes input and sets it as output)
                final List<Double> calcInputLayerOutput = layer.calulcateOutput(row.getInputs());
                final Layer hiddenLayer = layer.getNextLayer();
                // calculate hidden layer result
                final List<Double> calculatedHiddenLayerOutput = hiddenLayer.calulcateOutput(calcInputLayerOutput);
                final Layer outputLayer = hiddenLayer.getNextLayer();
                // calculate neural network output
                final List<Double> calcOutput = outputLayer.calulcateOutput(calculatedHiddenLayerOutput);

                // add expected result and actual result to lists, for MSE calculation later on
                allCalculated.addAll(calcOutput);
                allExpected.addAll(row.getExpectedOutputs());

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
                    final double biasDelta = learningRate * outputNeuron.getError();
                    outputNeuron.setBias(outputNeuron.getBias() + biasDelta);

                    // for each connection between hidden and output layer neurons
                    for (int i = 0; i < outputNeuron.getInput().size(); i++) {
                        // adapt weights (v)
                        //  v_j = v_j + learning rate * output of neuron in hidden layer with index i * "delta error"
                        final Double delta = learningRate * outputNeuron.getInput().get(i) * outputNeuron.getError();
                        outputNeuron.getWeight().set(i, outputNeuron.getWeight().get(i) + delta);

                        Neuron hiddenNeuron = hiddenLayer.getNeurons().get(i);
                        hiddenNeuron.setError(outputLayer.getNeurons().get(k).getError() * hiddenNeuron.derivate() * outputNeuron.getWeight().get(i));
                        hiddenNeuron.setBias(hiddenNeuron.getBias() + learningRate * hiddenNeuron.getError());
                        for (int j = 0; j < hiddenNeuron.getInput().size(); j++) {
                            final double hiddenInput = hiddenNeuron.getInput().get(j);
                            hiddenNeuron.getWeight().set(j, hiddenNeuron.getWeight().get(j) + hiddenNeuron.getError() *  learningRate * hiddenInput);
                        }
                    }
                }

            }
            mse = MSE(allExpected, allCalculated);
            System.out.printf("Epoch %d, MSE %.10f\n", epochs, mse);
            epochs++;

        }

    }

    private Double MSE(List<Double> expected, List<Double> calculated) {
        final double mse = IntStream.range(0, Math.min(expected.size(), calculated.size()))
                .mapToDouble(i -> Math.pow(expected.get(i) - calculated.get(i), 2.0)).average().orElse(0.0);
        return mse;
    }
}
