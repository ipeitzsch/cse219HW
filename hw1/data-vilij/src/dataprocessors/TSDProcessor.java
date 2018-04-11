package dataprocessors;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;


import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;
    private Set<String> labels;
    private AtomicBoolean hasNull;

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        labels = new HashSet<>();
        hasNull = new AtomicBoolean();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        hasNull.set(false);
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      String   name  = checkedname(list.get(0));
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                      if(label.equals("null"))
                      {
                          hasNull.set(true);
                      }
                      else {
                          labels.add(label);
                      }
                  } catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }

    public int getNumLabels()
    {
        return labels.size();
    }

    public boolean isNull()
    {
        return hasNull.get();
    }
    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void toChartData(XYChart<Number, Number> chart) {

        Set<String> labels = new HashSet<>(dataLabels.values());
        int count = 0;
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY(), entry.getKey()));


            });
            chart.getData().add(series);
            Set<Node>  n = chart.lookupAll(".series"  + count);
            for(Node x : n)
            {
                x.setStyle("-fx-stroke: none;");
            }
            count++;
        }
        setTooltip(chart);

        Set<Node>  n = chart.lookupAll(".series" + count);
        for(Node x : n)
        {
            x.setStyle("-fx-shape: \"M0,0 L2,0 L4,0 L7,0 L9,0 L4,0 Z\";");

        }

    }

    void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }
    private void setTooltip(XYChart<Number, Number> chart)
    {
        ObservableList<XYChart.Series<Number,Number>> a = chart.getData();
        for(XYChart.Series<Number, Number> ser : a)
        {
            ObservableList<XYChart.Data<Number,Number>> o = ser.getData();
            for(XYChart.Data<Number,Number> c : o)
            {
                Tooltip t = new Tooltip(c.getExtraValue().toString());
                Tooltip.install(c.getNode(), t);
            }
        }
    }

}
