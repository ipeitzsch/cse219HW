package algorithm;

import dataprocessors.AlgProcessor;

import dataprocessors.DataSet;
import vilij.templates.ApplicationTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An abstract class for classification algorithms. The output
 * for these algorithms is a straight line, as described in
 * Appendix C of the software requirements specification
 * (SRS). The {@link #output} is defined with extensibility
 * in mind.
 *
 * @author Ritwik Banerjee
 */
public abstract class Classifier implements Algorithm {

    /**
     * See Appendix C of the SRS. Defining the output as a
     * list instead of a triple allows for future extension
     * into polynomial curves instead of just straight lines.
     * See 3.4.4 of the SRS.
     */
    protected List<Integer> output;
    protected ApplicationTemplate applicationTemplate;
    public List<Integer> getOutput() { return output; }
    protected int maxIterations;
    protected int updateInterval;
    protected DataSet dataset;
    protected AlgProcessor cp;
    // currently, this value does not change after instantiation
    protected AtomicBoolean tocontinue;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        if(tocontinue == null)
        {
            tocontinue = new AtomicBoolean();
            tocontinue.set(false);
        }
        return tocontinue.get();
    }
    @Override
    public void setMax(int i)
    {
        maxIterations = i;
    }
    @Override
    public void setUpdate(int i)
    {
        updateInterval = i;
    }
    @Override
    public void setToContinue(boolean b)
    {
        tocontinue.set(b);
    }
    @Override
    public void setCP(AlgProcessor c)
    {
        cp = c;
    }
    @Override
    public void setDataset(DataSet d)
    {
        dataset = d;
    }

}
