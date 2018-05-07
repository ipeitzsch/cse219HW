package algorithm;

import dataprocessors.AlgProcessor;

import dataprocessors.DataSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();
    private final List<Integer> DONE = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;


    public RandomClassifier(){
       // dataset = new DataSet(applicationTemplate);
        cp = new AlgProcessor();
    }

    @Override
    public void run() {
        for (int i = 1; i <= maxIterations; i++) {
            int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int constant     = new Double(RAND.nextDouble() * 100).intValue();

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if(i % updateInterval == 0)
            {
                flush();
            }
        }
        if(maxIterations % updateInterval == 0)
        {
            flush();
        }


    }

    // for internal viewing only
    private synchronized void flush() {
        cp.addLine(this.getOutput());
    }

    /** A placeholder main method to just make sure this code runs smoothly
    public static void main(String... args) throws IOException {
        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        classifier.run(); // no multithreading yet
    } */
}
