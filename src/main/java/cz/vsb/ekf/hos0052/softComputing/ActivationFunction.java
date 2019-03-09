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
public interface ActivationFunction {
    Double value(Double weightedInputs);    
    Double derivate (Double weightedInputs);
}
