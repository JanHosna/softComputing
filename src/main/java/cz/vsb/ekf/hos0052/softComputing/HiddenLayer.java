package cz.vsb.ekf.hos0052.softComputing;

import java.util.List;

public class HiddenLayer extends Layer {
    public HiddenLayer(List<? extends Neuron> neurons, Layer previousLayer, Layer nextLayer) {
        super(neurons, previousLayer, nextLayer);
    }

}
