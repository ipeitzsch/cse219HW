package comms;

import algorithm.*;
import settings.AlgPropertyTypes;
import settings.AppPropertyTypes;
import vilij.templates.ApplicationTemplate;

import java.util.*;

public class AppComms {
    private HashMap<String, Algorithm> algorithms;


    public AppComms(ApplicationTemplate applicationTemplate)
    {
        algorithms = new HashMap<>();

        String path = applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALG_PATH.name());

        try {
            Class<?> algs = Class.forName(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALG_PROP_PATH.name()));

            List l = Arrays.asList(algs.getEnumConstants());
            for(Object o : l)
            {
                if(o instanceof Enum)
                {
                    Class<?> c = Class.forName(path + applicationTemplate.manager.getPropertyValue(((Enum)o).name()));
                    algorithms.put(c.getSimpleName(), (Algorithm)c.newInstance());
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

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
