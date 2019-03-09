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
 *
 * @author hos0052
 */
public class DeltaRule {

    private Layer layer;
    private Double learningRate;
    private Double targetMse;
    private int maxEpochs;

    public DeltaRule(Layer layer, Double learningRule, Double targetMse, int maxEpochs) {
        this.layer = layer;
        this.learningRate = learningRule;
        this.targetMse = targetMse;
        this.maxEpochs = maxEpochs;
    }

    public void train(List<DataSetRow> trainingSet) {
        int epochs = 0;
        Double mse = Double.MAX_VALUE;
        
        while(epochs < maxEpochs && mse > targetMse) {
            
            final List<Double> allExpected = new ArrayList<>(trainingSet.size());
            final List<Double> allCalculated = new ArrayList<>(trainingSet.size());

        
            for(DataSetRow row : trainingSet){
                final List<Double> calcOutput = layer.calulcateOutput(row.getInputs());
                allCalculated.addAll(calcOutput);
                allExpected.addAll(row.getExpectedOutputs());
                
                for (int k = 0; k < calcOutput.size() ; k++) {
                    final Double expected = row.getExpectedOutputs().get(k);
                    final Double calculated = calcOutput.get(k);
                    final Double diff = expected - calculated;
                    
                    final Neuron neuron = layer.getNeurons().get(k);
                    final Double derivative = neuron.derivate();
                    
                    for (int i = 0; i < row.getInputs().size();i++){
                        final Double input = row.getInputs().get(i);
                        final Double delta = learningRate * diff * derivative * input;
                        neuron.getWeight().set(i, neuron.getWeight().get(i) + delta);
                        
                    }
                    
                    final double biasDelta = learningRate * diff * derivative;
                    neuron.setBias(neuron.getBias() + biasDelta);
                    
                }
                
                

            }
            mse = MSE(allExpected, allCalculated);
            System.out.printf("Epoch %d, MSE %f\n", epochs, mse);
            epochs++;
            
        }
        
    }
    
    private Double MSE(List<Double> expected, List<Double> calculated){
        final double mse = IntStream.range(0, Math.min(expected.size(),calculated.size()))
                .mapToDouble(i -> Math.pow(expected.get(i) - calculated.get(i), 2.0)).average().orElse(0.0);
        return mse;
    }
}
