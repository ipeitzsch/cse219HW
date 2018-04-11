package algorithm;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Cluster implements Algorithm{
    /**
     * See Appendix C of the SRS. Defining the output as a
     * list instead of a triple allows for future extension
     * into polynomial curves instead of just straight lines.
     * See 3.4.4 of the SRS.
     */
    protected List<Integer> output;

    public List<Integer> getOutput() { return output; }
    protected int maxIterations;
    protected int updateInterval;
    protected int numLabels;
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
    public int getNumLabels()
    {
        return numLabels;
    }
    public void setMax(int i)
    {
        maxIterations = i;
    }
    public void setUpdate(int i)
    {
        updateInterval = i;
    }
    public void setToContinue(boolean b)
    {
        tocontinue.set(b);
    }
    public void setNumLabels(int i)
    {
        numLabels = i;
    }

}
