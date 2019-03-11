/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.ekf.hos0052.softComputing;

import java.util.List;

/**
 *
 * @author hos0052
 */
public class InputNeuron extends Neuron {

    public InputNeuron(List<Double> weight, ActivationFunction activationFunction) {
        super(weight, 0.0, activationFunction);
    }



    public Double calculate(List<Double> input) {
        setInput(input);
        setOutput(input.get(0));
        return getOutput();
    }
}