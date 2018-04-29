package comms;

import algorithm.*;
import vilij.templates.ApplicationTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AppComms {
    private HashMap<String, Algorithm> algorithms;


    public AppComms()
    {
        algorithms = new HashMap<>();


        algorithms.put("Random Classifier", new RandomClassifier());
        algorithms.put("Random Cluster", new RandomCluster());
    }

    public Set<String> getClassNames()
    {
        Set<String> names = new HashSet<>();

        for(String s : algorithms.keySet())
        {
            if(algorithms.get(s) instanceof Classifier)
            {
                names.add(s);
            }
        }
        return names;
    }
    public Set<String> getClustNames()
    {
        Set<String> names = new HashSet<>();

        for(String s : algorithms.keySet())
        {
            if(algorithms.get(s) instanceof Cluster)
            {
                names.add(s);
            }
        }
        return names;
    }
    public Algorithm getAlgorithm(String s)
    {
        return algorithms.get(s);
    }
    public void setClasssif(String name, int max, int refresh, boolean cont, ApplicationTemplate a)
    {
        Classifier c = (Classifier) algorithms.get(name);
        c.setMax(max);
        c.setUpdate(refresh);
        c.setToContinue(cont);
        algorithms.put(name, c);
    }
    public void setClust(String name, int max, int refresh, boolean cont, int num, ApplicationTemplate a)
    {
        Cluster c = (Cluster) algorithms.get(name);
        c.setMax(max);
        c.setUpdate(refresh);
        c.setToContinue(cont);
        c.setNumLabels(num);
        algorithms.put(name, c);
    }
}
