package cz.vsb.ekf.hos0052.softComputing;

import java.util.List;

public class OutputLayer extends Layer {

    public OutputLayer(List<? extends Neuron> neurons, Layer previousLayer, Layer nextLayer) {
        super(neurons, previousLayer, null);
    }

    @Override
    public void setNextLayer(Layer nextLayer) {
        if (nextLayer != null) {
            throw new IllegalArgumentException("input layer should not have a previous layer");
        }
    }

    @Override
    public Layer getNextLayer() {
        return null; //To change body of generated methods, choose Tools | Templates.
    }

}
