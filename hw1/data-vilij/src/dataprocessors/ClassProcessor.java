package dataprocessors;

import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import vilij.templates.ApplicationTemplate;

import java.util.List;

public class ClassProcessor {

    private XYChart<Number, Number> chart;
    private AppData data;
    public ClassProcessor()
    {
        data = new AppData(new ApplicationTemplate());
        chart = new LineChart<>(new NumberAxis(), new NumberAxis());
    }
    public ClassProcessor(XYChart<Number, Number> c, AppData a)
    {
        chart = c;
        data = a;
    }

    public void addLine(List<Integer> l)
    {
        System.out.println("\t***** CP *****");
        try {
            data.handleLine(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
