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
public class DataSetRow {

    public List<Double> inputs;
    public List<Double> expectedOutputs;

    public DataSetRow(List<Double> inputs, List<Double> expectedOutputs) {
        this.inputs = inputs;
        this.expectedOutputs = expectedOutputs;
    }

    public List<Double> getInputs() {
        return inputs;
    }

    public void setInputs(List<Double> inputs) {
        this.inputs = inputs;
    }

    public List<Double> getExpectedOutputs() {
        return expectedOutputs;
    }

    public void setExpectedOutputs(List<Double> expectedOutputs) {
        this.expectedOutputs = expectedOutputs;
    }

}
