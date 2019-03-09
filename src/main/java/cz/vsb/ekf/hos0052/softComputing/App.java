package cz.vsb.ekf.hos0052.softComputing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        final List<DataSetRow> dataSet = createOrDataSet0();
        final InputLayer inputLayer = createInputLayer();
        
        System.out.println("Original values");
        calculateAndPrintLayerOutputs(dataSet, inputLayer);
        
        final DeltaRule deltarule = new DeltaRule(inputLayer,0.2,0.005,2500);
        deltarule.train(dataSet);
        
        System.out.println("trained network values");
        calculateAndPrintLayerOutputs(dataSet, inputLayer);
    }
    
    private static void calculateAndPrintLayerOutputs(List<DataSetRow> dataset, InputLayer inputLayer){
    
    for(DataSetRow row : dataset ) {
        final List<Double> calculatedOutput = inputLayer.calulcateOutput(row.getInputs());
            System.out.printf("%s, %s -> %s\n", row.getInputs(), row.getExpectedOutputs(), calculatedOutput);
        }
    }
            

    private static List<DataSetRow> createOrDataSet0() {
        List<DataSetRow> dataSet = new ArrayList<>(4);

        dataSet.add(new DataSetRow(Arrays.asList(1.0,1.0), Arrays.asList(1.0)));
        dataSet.add(new DataSetRow(Arrays.asList(1.0,0.0), Arrays.asList(1.0)));
        dataSet.add(new DataSetRow(Arrays.asList(0.0,1.0), Arrays.asList(1.0)));
        dataSet.add(new DataSetRow(Arrays.asList(0.0,0.0), Arrays.asList(0.0)));

        return dataSet;
    }
    
    private static InputLayer createInputLayer(){
     List<Double> weights = new ArrayList<>(2);
     weights.add(0.5);
     weights.add(0.5);
     InputNeuron neuron = new InputNeuron(weights, new ActivationTanh(1.0));
     InputLayer layer = new InputLayer(new ArrayList<>(Arrays.asList(neuron)), null, null);
     return layer;

     
    }

}
