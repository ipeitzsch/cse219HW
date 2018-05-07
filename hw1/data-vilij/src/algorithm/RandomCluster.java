package algorithm;

import dataprocessors.AlgProcessor;
import dataprocessors.DataSet;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomCluster extends Cluster{

    public RandomCluster()
    {
        dataSet = new DataSet();
        cp = new AlgProcessor();
        continuous = new AtomicBoolean();
        toContinue = new AtomicBoolean();
    }

    @Override
    public void run() {
        Set<String> inst = dataSet.getLabels().keySet();
        System.out.println(inst);
        String[] instances = new String[inst.size()];
        int count = 0;
        for(String s : inst)
        {
            instances[count] = s;
            count++;
            dataSet.updateLabel(s, "0");
        }
        Random rand = new Random();
        for(int i = 1; i <= maxIterations; i ++)
        {

            String instance = instances[rand.nextInt(instances.length)];
            String label = "" + rand.nextInt(numLabels);

            dataSet.updateLabel(instance, label);

            if(i % updateInterval == 0)
            {
                flush();
            }
        }
        if(maxIterations % updateInterval != 0) {
            flush();
        }
    }
}
