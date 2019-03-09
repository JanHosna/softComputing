/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.ekf.hos0052.softComputing;

/**
 *
 * @author hos0052
 */
public class ActivationTanh implements ActivationFunction{
    
    private Double coefficient;

    public ActivationTanh(Double coefficient) {
        this.coefficient = coefficient;
    }
    
    

    @Override
    public Double value(Double weightedInputs) {
        final double exp = Math.exp( -coefficient * weightedInputs);
        return (1.0 - exp) / (1.0 + exp);
    }

    @Override
    public Double derivate(Double weightedInputs) {
        return 1.0 - Math.pow(value(weightedInputs), 2.0);
    }
    
}
