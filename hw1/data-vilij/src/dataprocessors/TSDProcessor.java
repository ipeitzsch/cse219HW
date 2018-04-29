package dataprocessors;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
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
    private double min;
    private double max;
    private double minY;
    private double maxY;
    private List<XYChart.Series<Number, Number>>l;
    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        labels = new HashSet<>();
        hasNull = new AtomicBoolean();
        l = new ArrayList<>();
    }
    public HashMap<String, String> getLabels()
    {
        return (HashMap<String, String>)dataLabels;
    }
    public HashMap<String, Point2D> getPoints()
    {
        return (HashMap<String, Point2D>)dataPoints;
    }
    public Set<String> getLabelNames(){
        return labels;
    }
    public void setLabels(Map<String, String> lab)
    {
        dataLabels = lab;
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
    public int getNumInstances() { return dataPoints.size(); }
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

            Set<Node>  n = chart.lookupAll(".series"  + (count));
            for(Node x : n)
            {

                x.setStyle("-fx-stroke: none;");
            }
            count++;
        }
        setTooltip(chart);


    }

    void clear() {
        dataPoints.clear();
        dataLabels.clear();
        labels.clear();
        l.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }
    private void setTooltip(XYChart<Number, Number> chart)
    {
        boolean flag = true;
        ObservableList<XYChart.Series<Number,Number>> a = chart.getData();
        for(XYChart.Series<Number, Number> ser : a)
        {
            ObservableList<XYChart.Data<Number,Number>> o = ser.getData();
            for(XYChart.Data<Number,Number> c : o)
            {
                if(flag)
                {
                    min = (Double)c.getXValue();
                    max = (Double)c.getXValue();
                    minY = (Double)c.getYValue();
                    maxY = (Double)c.getYValue();
                    flag = false;
                }
                else if(min > (Double)c.getXValue())
                {
                    min = (Double)c.getXValue();
                }
                else if(max < (Double)c.getXValue())
                {
                    max = (Double)c.getXValue();
                }
                if(minY > (Double)c.getYValue())
                {
                    minY = (Double)c.getYValue();
                }
                else if(maxY < (Double)c.getYValue())
                {
                    maxY = (Double)c.getYValue();
                }
                Tooltip t = new Tooltip(c.getExtraValue().toString());
                Tooltip.install(c.getNode(), t);
            }
        }
    }

    public void handleLine(List<Integer> line) {
        double a = line.get(0);
        double b = line.get(1);
        double c = line.get(2);
        double y1 = -1 * (-(a / b) * min -(c / b));
        double y2 = -1 * (-(a / b) * max - (c / b));




            XYChart.Series<Number,Number> x = new XYChart.Series<>();

            x.getData().add(new XYChart.Data<>(min, y1));
            x.getData().add(new XYChart.Data<>(max, y2));

        l.add(x);


    }
    public List<XYChart.Series<Number, Number>> getList()
    {
        return l;
    }
    public double getMinX()
    {
        return min;
    }
    public double getMaxX() {
        return max;
    }
    public double getMinY()
    {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }
}

