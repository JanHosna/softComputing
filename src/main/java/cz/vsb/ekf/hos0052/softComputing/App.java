package cz.vsb.ekf.hos0052.softComputing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class App {

    private static final String SAMPLE_CSV_FILE_PATH = "./cruzeirodosul2010daily.txt";
    private static double minX;
    private static double maxX;

    public static void main(String[] args) throws IOException {
        final int numberOfHiddenNeurons = 10;
        final double learningRule = 0.002;
        final double targetMSE = 0.0000000005;
        final int maxEpochs = 1000;
        final int shift = 20;
        final int numberOfInputNeurons = shift;
        List<Double> beforeValues = new ArrayList<>();

        importCSVIntoList(beforeValues);
        double meanBeforeValues = calculateMean(beforeValues);
        System.out.printf("Before diff mean = %s, dispersion = %s\n", meanBeforeValues, calculateDispersion(meanBeforeValues, beforeValues));

        List<Double> differencesInValues = calculateDifferences(beforeValues);
        double meanDifferencesInValues = calculateMean(differencesInValues);
        System.out.printf("Diff mean = %s, dispersion = %s\n", meanDifferencesInValues, calculateDispersion(meanDifferencesInValues, differencesInValues));

        List<Double> normalizedDifferencesInValues = normalizeValues(differencesInValues);
        List<DataSetRow> dataSet = createDataSet(normalizedDifferencesInValues, shift);

        int dataSetSplitter = dataSet.size() - (int) (dataSet.size() * 0.25);
        List<DataSetRow> trainingDataSet = dataSet.subList(0, dataSetSplitter);
        List<DataSetRow> validatingDataSet = dataSet.subList(dataSetSplitter, dataSet.size());

        final InputLayer inputLayer = createInputLayer(numberOfInputNeurons);
        final HiddenLayer hiddenLayer = createHiddenLayer(numberOfHiddenNeurons, numberOfInputNeurons);
        final OutputLayer outputLayer = createOutputLayer(numberOfHiddenNeurons);

        inputLayer.setNextLayer(hiddenLayer);
        hiddenLayer.setPreviousLayer(inputLayer);
        hiddenLayer.setNextLayer(outputLayer);
        outputLayer.setPreviousLayer(hiddenLayer);

        System.out.println("Original values");
        calculateAndPrintLayerOutputs(dataSet, inputLayer, hiddenLayer, outputLayer);

        final BackPropagation backPropagation = new BackPropagation(inputLayer, learningRule, targetMSE, maxEpochs);
        backPropagation.calculateMSE(validatingDataSet);
        backPropagation.train(trainingDataSet);

        System.out.println("Trained network values");
        calculateAndPrintLayerOutputs(validatingDataSet, inputLayer, hiddenLayer, outputLayer);
        backPropagation.calculateMSE(validatingDataSet);

    }

    private static double calculateDispersion(double meanBeforeValues, List<Double> beforeValues) {
        double temp = 0;
        for (double a : beforeValues)
            temp += (a - meanBeforeValues) * (a - meanBeforeValues);
        return Math.sqrt(temp / (beforeValues.size() - 1));
    }

    private static double calculateMean(List<Double> beforeValues) {
        double sum = 0.0;
        for (double a : beforeValues)
            sum += a;
        return sum / beforeValues.size();
    }

    private static void importCSVIntoList(List<Double> beforeValues) throws IOException {
        List<String> dataFromFile = new ArrayList<>();

        try (
                Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase());) {
            for (CSVRecord csvRecord : csvParser) {
                dataFromFile.add(csvRecord.get(2));
            }
            for (int i = 0; i < dataFromFile.size(); i++) {
                if (isNumeric(dataFromFile.get(i))) {
                    beforeValues.add(Double.parseDouble(dataFromFile.get(i)));
                } else {
                    Double previousNumber = Double.parseDouble(dataFromFile.get(i - 1));
                    Double nextNumber = getNextNumber(dataFromFile, i);
                    beforeValues.add((nextNumber + previousNumber) / 2);
                }
            }

        } catch (CustomException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static List<Double> normalizeValues(List<Double> listOfDoubles) {
        List<Double> normalizedListOfDoubles = new ArrayList<>();
        List<Double> sortedList = new ArrayList<>(listOfDoubles);
        Collections.sort(sortedList);
        minX = sortedList.get(0);
        maxX = sortedList.get(sortedList.size() - 1);
        for (Double x : listOfDoubles) {
            normalizedListOfDoubles.add((2 * (x - minX) / (maxX - minX)) - 1.0);
        }

        return normalizedListOfDoubles;
    }


    public static boolean isNumeric(String strNum) {
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    private static List<Double> calculateDifferences(List<Double> values) {
        List<Double> differencesInTemperatures = new ArrayList<>();

        for (int i = 1; i < values.size(); i++) {
            differencesInTemperatures.add(values.get(i) - values.get(i - 1));
        }

        return differencesInTemperatures;
    }

    private static Double getNextNumber(List<String> dataFromFile, int i) throws CustomException {
        int counter = i + 1;
        while (dataFromFile.size() > counter) {
            if (isNumeric(dataFromFile.get(counter))) {
                return Double.parseDouble(dataFromFile.get(counter));
            }
            counter++;
        }
        throw new CustomException("Unable to find next number to interpolate");
    }

    private static void calculateAndPrintLayerOutputs(List<DataSetRow> dataset, Layer inputLayer, HiddenLayer hiddenLayer, OutputLayer outputLayer) {

        for (int i = 0; i < dataset.size(); i++) {
            DataSetRow row = dataset.get(i);
            final List<Double> calculatedHiddenLayerOutput = hiddenLayer.calulcateOutput(row.getInputs());
            final List<Double> calculatedOutput = outputLayer.calulcateOutput(calculatedHiddenLayerOutput);

                List<Double> denormalizedOutput = denormalizeOutput(calculatedOutput);
                List<Double> denormalizedExpectedOutput = denormalizeOutput(row.getExpectedOutputs());
                List<Double> denormalizedInputs = denormalizeOutput(row.getInputs());
//                System.out.printf("inputs: %s, expected output: %s, calculated output: %s\n", denormalizedInputs, denormalizedExpectedOutput, denormalizedOutput);
                System.out.printf("%s\n",denormalizedOutput);
            }
    }

    private static List<Double> denormalizeOutput(List<Double> calculatedOutput) {
        List<Double> denormalizedListOfDoubles = new ArrayList<>();
        for (Double normalizedX : calculatedOutput) {
            double denormalized = ((((normalizedX + 1.0) / 2) * (maxX - minX) + minX));

            denormalizedListOfDoubles.add(denormalized);
        }
        return denormalizedListOfDoubles;

    }

    private static List<DataSetRow> createDataSet(List<Double> data, int posun) {
        List<DataSetRow> dataSet = new ArrayList<>();
        for (int i = posun; i < data.size(); i++) {
            List<Double> inputs = new ArrayList<>();
            List<Double> outputs = new ArrayList<>();
            for (int j = 1; j < posun + 1; j++) {
                inputs.add(data.get(i - j));
            }
            outputs.add(data.get(i));
            dataSet.add(new DataSetRow(inputs, outputs));
        }
        return dataSet;
    }

    private static InputLayer createInputLayer(int numberOfInputNeurons) {
        List<Double> weights = new ArrayList<>(2);
        weights.add(1.0);
        ArrayList<Neuron> inputNeurons = new ArrayList<>(numberOfInputNeurons);
        for (int i = 0; i < numberOfInputNeurons; i++) {
            Neuron neuron = new InputNeuron(weights, new ActivationTanh(1.0));
            inputNeurons.add(neuron);
        }
        return new InputLayer(inputNeurons, null, null);

    }

    private static HiddenLayer createHiddenLayer(int numberOfHiddenNeurons, int numberOfInputNeurons) {

        ArrayList<Neuron> hiddenNeurons = new ArrayList<>(numberOfHiddenNeurons);
        for (int i = 0; i < numberOfHiddenNeurons; i++) {
            List<Double> weights = new ArrayList<>(2);
            for (int j = 0; j < numberOfInputNeurons; j++) {
                weights.add(Math.random());
            }
            Neuron neuron = new Neuron(weights, 0.0, new ActivationTanh(1.0));
            hiddenNeurons.add(neuron);
        }
        return new HiddenLayer(hiddenNeurons, null, null);
    }

    private static OutputLayer createOutputLayer(int numberOfHiddenNeurons) {
        List<Double> weights = new ArrayList<>(2);
        for (int i = 0; i < numberOfHiddenNeurons; i++) {
            weights.add(Math.random());
        }
        Neuron neuron = new Neuron(weights, 0.0, new ActivationTanh(1.0));
        return new OutputLayer(new ArrayList<>(Arrays.asList(neuron)), null, null);

    }

}
