package dataprocessors;


import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import vilij.templates.ApplicationTemplate;

import java.util.List;
import java.util.Map;

public class AlgProcessor {
    private AppData data;
    public AlgProcessor()
    {
        data = new AppData(new ApplicationTemplate());
    }
    public AlgProcessor(AppData a)
    {
        data = a;
    }

    public void addLine(List<Integer> l)
    {
            data.handleLine(l);
    }

    public void refresh(Map<String, String> labels)
    {
        data.setLabels(labels);
    }
}
