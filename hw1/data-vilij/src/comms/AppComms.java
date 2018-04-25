package comms;

import algorithm.Classifier;
import algorithm.Clus;
import algorithm.Cluster;
import algorithm.RandomClassifier;
import vilij.templates.ApplicationTemplate;

import java.util.HashMap;
import java.util.Set;

public class AppComms {
    private HashMap<String, Classifier> classifiers;
    private HashMap<String, Cluster> clusters;

    public AppComms()
    {
        classifiers = new HashMap<>();
        clusters = new HashMap<>();

        classifiers.put("Random Classifier", new RandomClassifier());
        clusters.put("Cluster", new Clus());
    }

    public Set<String> getClassNames()
    {
        return classifiers.keySet();
    }
    public Set<String> getClustNames()
    {
        return clusters.keySet();
    }
    public Classifier getClassif(String s)
    {
        return classifiers.get(s);
    }
    public Cluster getClust(String s)
    {
        return clusters.get(s);
    }
    public void setClasssif(String name, int max, int refresh, boolean cont, ApplicationTemplate a)
    {
        Classifier c = classifiers.get(name);
        c.setMax(max);
        c.setUpdate(refresh);
        c.setToContinue(cont);

        classifiers.put(name, c);
    }
    public void setClust(String name, int max, int refresh, boolean cont, int num, ApplicationTemplate a)
    {
        Cluster c = clusters.get(name);
        c.setMax(max);
        c.setUpdate(refresh);
        c.setToContinue(cont);
        c.setNumLabels(num);
        c.setApplicationTemplate(a);
        clusters.put(name, c);
    }
}
