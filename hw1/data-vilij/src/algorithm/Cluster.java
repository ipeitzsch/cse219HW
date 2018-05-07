package algorithm;

import dataprocessors.AlgProcessor;
import dataprocessors.DataSet;
import vilij.templates.ApplicationTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Cluster implements Algorithm{
    /**
     * See Appendix C of the SRS. Defining the output as a
     * list instead of a triple allows for future extension
     * into polynomial curves instead of just straight lines.
     * See 3.4.4 of the SRS.
     */

    protected ApplicationTemplate applicationTemplate;

    protected int maxIterations;
    protected int updateInterval;
    protected int numLabels;
    // currently, this value does not change after instantiation
    protected AtomicBoolean continuous;
    protected AlgProcessor cp;
    protected DataSet dataSet;
    protected AtomicBoolean toContinue;
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
        if(continuous == null)
        {
            continuous = new AtomicBoolean();
            continuous.set(false);
        }
        return continuous.get();
    }

    public int getNumLabels()
    {
        return numLabels;
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
        if(continuous == null)
        {
            continuous = new AtomicBoolean();
        }
        continuous.set(b);
    }

    public void setNumLabels(int i)
    {
        numLabels = i;
    }
    @Override
    public void setCP(AlgProcessor c)
    {
        cp = c;
    }
    @Override
    public void setDataset(DataSet d)
    {
        dataSet = d;
    }

    public void flush()  {
        cp.refresh(new HashMap<String, String>(dataSet.getLabels()));
    }
}
