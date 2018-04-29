package dataprocessors;


import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import vilij.templates.ApplicationTemplate;

import java.util.List;
import java.util.Map;

public class AlgProcessor {

    private XYChart<Number, Number> chart;
    private AppData data;
    public AlgProcessor()
    {
        data = new AppData(new ApplicationTemplate());
        chart = new LineChart<>(new NumberAxis(), new NumberAxis());
    }
    public AlgProcessor(XYChart<Number, Number> c, AppData a)
    {
        chart = c;
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
