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
public class InputLayer extends Layer {

    public InputLayer(List<? extends Neuron> neurons, Layer previousLayer, Layer nextLayer) {
        super(neurons, null, nextLayer);
    }

    @Override
    public void setPreviousLayer(Layer previousLayer) {
        if (previousLayer != null) {
            throw new IllegalArgumentException("input layer should not have a previous layer");
        }
    }

    @Override
    public Layer getPreviousLayer() {
        return null; //To change body of generated methods, choose Tools | Templates.
    }

}
