/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.ekf.hos0052.softComputing;

import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * @author hos0052
 */
public class Neuron {

    private List<Double> input;
    private List<Double> weight;
    private Double bias;
    private ActivationFunction activationFunction;
    private Double output;

    public Neuron(List<Double> weight, Double bias, ActivationFunction activationFunction) {
        this.weight = weight;
        this.bias = bias;
        this.activationFunction = activationFunction;
    }
    
    public Double derivate () {
        final double sum = IntStream.range(0, Math.min(input.size(), weight.size()))
                .mapToDouble(i -> getInput().get(i) * getWeight().get(i))
                .sum();
        return getActivationFunction().derivate(sum + getBias());
        
    }

    public List<Double> getInput() {
        return input;
    }

    public void setInput(List<Double> input) {
        this.input = input;
    }

    public List<Double> getWeight() {
        return weight;
    }

    public void setWeight(List<Double> weight) {
        this.weight = weight;
    }

    public Double getBias() {
        return bias;
    }

    public void setBias(Double bias) {
        this.bias = bias;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

    public Double getOutput() {
        return output;
    }

    public void setOutput(Double output) {
        this.output = output;
    }

    public void calculate() {
        final double sum = IntStream.range(0, Math.min(input.size(), weight.size()))
                .mapToDouble(i -> getInput().get(i) * getWeight().get(i))
                .sum();
        output = getActivationFunction().value(sum + getBias());
    }
    
    public Double calculate(List<Double> input){
        setInput(input);
        calculate();
        return getOutput();
    }

}
