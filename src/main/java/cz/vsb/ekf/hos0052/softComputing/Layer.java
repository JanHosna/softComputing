/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.ekf.hos0052.softComputing;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author hos0052
 */
public abstract class Layer {

    public List<? extends Neuron> neurons;
    public Layer previousLayer;
    public Layer nextLayer;
    public List<Double> input;
    public List<Double> output;

    public Layer(List<? extends Neuron> neurons, Layer previousLayer, Layer nextLayer) {
        this.neurons = neurons;
        this.previousLayer = previousLayer;
        this.nextLayer = nextLayer;
    }

    public void calculateOutput () {
        final List<Double> activations = getNeurons().stream().map(n -> n.calculate(input)).collect(Collectors.toList());
        
        setOutput(activations);
    }
    
    public List<Double> calulcateOutput(List<Double> input) {
        setInput(input);
        calculateOutput();
        return getOutput();
    }

    public List<? extends Neuron> getNeurons() {
        return neurons;
    }

    public void setNeurons(List<? extends Neuron> neurons) {
        this.neurons = neurons;
    }

    public Layer getPreviousLayer() {
        return previousLayer;
    }

    public void setPreviousLayer(Layer previousLayer) {
        this.previousLayer = previousLayer;
    }

    public Layer getNextLayer() {
        return nextLayer;
    }

    public void setNextLayer(Layer nextLayer) {
        this.nextLayer = nextLayer;
    }

    public List<Double> getInput() {
        return input;
    }

    public void setInput(List<Double> input) {
        this.input = input;
    }

    public List<Double> getOutput() {
        return output;
    }

    public void setOutput(List<Double> output) {
        this.output = output;
    }
}
