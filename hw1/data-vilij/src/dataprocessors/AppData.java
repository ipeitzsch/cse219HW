package dataprocessors;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.util.Duration;
import settings.AppPropertyTypes;
import ui.AppUI;

import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;
    private List<Map<String, String>> labels = new ArrayList<>();
    private boolean isLoaded;
    private String loadedData;
    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
        isLoaded = false;
        loadedData = "";
    }


    @Override
    public void loadData(Path dataFilePath) {
        File f = dataFilePath.toFile();
        int count = 0;
        ArrayList<String> S = new ArrayList<>();
        String s = "";
        try {
            Scanner sc = new Scanner(f);

            while(sc.hasNextLine())
            {
                String s1 = sc.nextLine();
                    S.add(s1);
                    s = s + s1 + "\n";
                 count++;
            }
            if(checkValid(s)) {
                loadedData = s;
                processor.processString(s);
                AppUI a = (AppUI) (applicationTemplate.getUIComponent());
                s = "";
                for (int i = 0; i < 10 && i < S.size(); i++) {
                    String q = S.get(i);
                    s = s + q + "\n";
                }
                a.setText(s);
                a.setChange(S);
                a.setReadOnly(true);
                isLoaded = true;

                ErrorDialog c = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                int cc;
                if (count < 10) {
                    cc = count;
                } else {
                    cc = 10;
                }
                c.show(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SUCCESSFUL_LOAD_TITLE.name()), "Successfully loaded " + count + " lines. " + cc + " lines are shown in the text area.");
            }
        }
        catch(Exception e){
            e.printStackTrace();
            ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager  = applicationTemplate.manager;
            String          errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String          errMsg   = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String          errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
            String message = "Error on line " + (count + 1) + " in " + manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name()) + "\n\"" + e.getMessage() + "\"";
            dialog.show(errTitle, errMsg + errInput + "\n" + message);
        }
        // TODO: NOT A PART OF HW 1
    }
    public boolean isLoaded()
    {
        return isLoaded;
    }
    public String getLoadedData()
    {
        return loadedData;
    }
    public void setLoaded(boolean b)
    {
        isLoaded = b;
    }
    private boolean checkValid(String text)
    {
        ArrayList<Integer> a = new ArrayList<>();
        AtomicBoolean b = new AtomicBoolean();
        SortedSet<String> g = new TreeSet<>();
        b.set(true);
        Stream.of(text.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    try {

                        if(!(list.get(0).startsWith("@")) || !(g.add(list.get(0))))
                        {
                            throw new Exception(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.INVALID_NAME_ERROR.name()) + list.get(0) + ".");
                        }
                        String[] pair  = list.get(2).split(",");
                        double i = Double.parseDouble(pair[0]);
                        double j = Double.parseDouble(pair[1]);
                        a.add(0);
                    } catch (Exception e) {
                        b.set(false);
                        ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                        PropertyManager manager  = applicationTemplate.manager;
                        String          errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
                        String          errMsg   = manager.getPropertyValue(AppPropertyTypes.INVALID_TEXT_FIELD.name());
                        String          errInput = "Error on line " + (a.size() + 1) + ". " + e.getMessage();
                        dialog.show(errTitle, errMsg + errInput);
                    }
                });
        return b.get();
    }

    public void loadData(String dataString) {
        try {
            if(checkValid(dataString)) {
                processor.processString(dataString);
            }
        } catch (Exception e) {
            ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager  = applicationTemplate.manager;
            String          errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String          errMsg   = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String          errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
            dialog.show(errTitle, errMsg + errInput);
        }
    }

    @Override
    public void saveData(Path dataFilePath) {
        // NOTE: completing this method was not a part of HW 1. You may have implemented file saving from the
        // confirmation dialog elsewhere in a different way.
        String toSave = loadedData;
        if(!isLoaded)
        {
            toSave = ((AppUI) applicationTemplate.getUIComponent()).getCurrentText();
        }
        if(checkValid(toSave)) {
            try (PrintWriter writer = new PrintWriter(Files.newOutputStream(dataFilePath))) {
                writer.write(toSave);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
    public void displayClust(AtomicBoolean isRunning, boolean cont, AtomicInteger count, AtomicBoolean firstClick)
    {
        int size = 1;
        if(cont)
        {
            size = labels.size();
        }
        AppUI a = (AppUI) applicationTemplate.getUIComponent();
        XYChart<Number, Number> chart = a.getChart();

        Timeline animate = new Timeline();

        animate.setCycleCount(size);

        if(a.getAlgPane().getChildren().get(a.getAlgPane().getChildren().size() - 1) instanceof Text)
        {
            if(((Text) a.getAlgPane().getChildren().get(a.getAlgPane().getChildren().size() - 1)).getText().equals(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DONE.name())))
            {
                a.getAlgPane().getChildren().remove(a.getAlgPane().getChildren().size() - 1);
            }
        }
        animate.getKeyFrames().add(new KeyFrame(Duration.millis(100), (javafx.event.ActionEvent actionEvent) -> {
            chart.getData().clear();
            processor.setLabels(labels.get(count.get()));

            count.set(count.get() + 1);
            processor.toChartData(chart);
            if(count.get() == labels.size())
            {
                isRunning.set(false);
                a.setScreenShotDisable(false);
                a.disableDisp(false);
                firstClick.set(true);
                count.set(0);
                a.getAlgPane().getChildren().add(new Text(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DONE.name())));
            }
        }));
        animate.play();
    }
    public int getNumLabels()
    {
       return processor.getNumLabels();
    }

    public boolean hasNull()
    {
        return processor.isNull();
    }

    public int getNumPoints()
    {
        return processor.getNumInstances();
    }

    public synchronized void handleLine(List<Integer> line) {
        processor.handleLine(line);
    }
    public synchronized void setLabels(Map<String, String> labels)
    {
        this.labels.add(labels);
    }
    public void displayLine(AtomicBoolean isRunning, boolean cont, AtomicInteger count, AtomicBoolean firstClick)
    {
        int size = 1;
        List<XYChart.Series<Number, Number>> line = processor.getList();
        AppUI a = (AppUI) applicationTemplate.getUIComponent();
        XYChart<Number, Number> chart = a.getChart();
        if(cont) {
            size = line.size();
        }
        Timeline animate = new Timeline();

        animate.setCycleCount(size);

        if(a.getAlgPane().getChildren().get(a.getAlgPane().getChildren().size() - 1) instanceof Text)
        {
            if(((Text) a.getAlgPane().getChildren().get(a.getAlgPane().getChildren().size() - 1)).getText().equals(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DONE.name())))
            {
                a.getAlgPane().getChildren().remove(a.getAlgPane().getChildren().size() - 1);
            }
        }

        animate.getKeyFrames().add(new KeyFrame(Duration.millis(100), (javafx.event.ActionEvent actionEvent) -> {
            if (count.get() != 0) {
                chart.getData().remove(chart.getData().size() - 1);
            }
            line.get(count.get()).setName(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.LINE.name()));
            chart.getData().add(line.get(count.get()));
            count.set(count.get() + 1);
            if (count.get() == line.size()) {
                a.setScreenShotDisable(false);
                a.disableDisp(false);
                isRunning.set(false);
                firstClick.set(true);
                count.set(0);
                a.getAlgPane().getChildren().add(new Text(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DONE.name())));
            }
        }));
        animate.play();

    }
    public Set<String> getLabelNames()
    {
        return processor.getLabelNames();
    }
    public HashMap<String, String> getLabels()
    {
        return processor.getLabels();
    }
    public HashMap<String, Point2D> getPoints()
    {
        return processor.getPoints();
    }
    public double getMaxX()
    {
        return processor.getMaxX();
    }
    public double getMinX()
    {
        return processor.getMinX();
    }
    public double getMaxY()
    {
        return processor.getMaxY();
    }
    public double getMinY()
    {
        return processor.getMinY();
    }
}
