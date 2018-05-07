package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.io.IOException;
import java.util.ArrayList;

import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private Button                       readOnly;
    private Button                       classifier;
    private Button                       cluster;
    private VBox                         algPane;
    private VBox                         leftPanel;

    public LineChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }
    public void setSaveDisable(boolean b){ saveButton.setDisable(b);}
    public void setNewDisable(boolean b) { newButton.setDisable(b);}
    public void disableDisp(boolean b) {displayButton.setDisable(b);}
    public boolean isScrnDisabled() { return scrnshotButton.isDisabled();}
    public void setScreenShotDisable(boolean b) { scrnshotButton.setDisable(b);}
    public Scene getScene() {return primaryScene;}
    public void setText(String s) {textArea.setText(s); }
    public void setChange(ArrayList<String> s)
    {
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String t[] = newValue.split("\n");
                if(t.length < 10) {
                    for (int i = 0; i < t.length; i++) {
                        if (!s.get(i).equals(t[i])) {
                            s.remove(i);
                        }
                    }
                    String h = "";
                    for (int i = 0; i < 10 && i < s.size(); i++) {
                        h = h + s.get(i) + "\n";
                    }
                    textArea.setText(h);
                }
                if(!oldValue.equals(newValue)) {
                    ((AppData) applicationTemplate.getDataComponent()).setLoaded(false);
                }
            }
        });
    }
    public void setAlgPane(VBox alg)
    {
        leftPanel.getChildren().remove(algPane);
        algPane = alg;
        leftPanel.getChildren().add(algPane);
    }
    public VBox getAlgPane()
    {
        return algPane;
    }
    public void setReadOnly(boolean b)
    {
        textArea.setDisable(b);
        AppData a = (AppData)(applicationTemplate.getDataComponent());
        if(textArea.isDisabled()) {


            String s = "Label names: ";
            for(String g : a.getLabelNames())
            {
                s = s + g + " ";
            }
            AppActions ap = (AppActions) applicationTemplate.getActionComponent();
            String path = "";
            if(ap.getPath() != null)
            {
                path = "Path: " + ap.getPath();
            }
            Text t = new Text(path);
            t.setWrappingWidth(algPane.getWidth());
            algPane.getChildren().remove(0, algPane.getChildren().size());
            algPane.getChildren().addAll(new Text("There are " + a.getNumLabels() + " labels."), new Text("There are " + a.getNumPoints() + " instances."), new Text(s), t, classifier, cluster);
            setClassifierDisable(true);
            setClusterDisable(false);
            if(a.getNumLabels() == 2 && !(a.hasNull()))
            {
                setClassifierDisable(false);
            }

            readOnly.setText("Edit");
        }
        else
        {
            readOnly.setText("Done");
            algPane.getChildren().remove(0, algPane.getChildren().size());
            algPane.getChildren().addAll(classifier, cluster);
            setClusterDisable(true);
            setClassifierDisable(true);
        }
    }
    public void setClassifierDisable(boolean b)
    {
        classifier.setDisable(b);
    }
    public void setClusterDisable(boolean b)
    {
        cluster.setDisable(b);
    }
    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                                                   manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                                   manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String scrnshoticonPath = String.join(SEPARATOR,
                                              iconsPath,
                                              manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ICON.name()));
        scrnshotButton = setToolbarButton(scrnshoticonPath,
                                          manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_TOOLTIP.name()),
                                          true);
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> {
            AppData a = (AppData)(applicationTemplate.getDataComponent());
            applicationTemplate.getActionComponent().handleLoadRequest();



        });
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        AppActions a = (AppActions)(applicationTemplate.getActionComponent());
        scrnshotButton.setOnAction(e -> {
            try {
                a.handleScreenshotRequest();
            }
            catch(IOException i) {
            }
        });

    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        textArea.clear();
        chart.getData().clear();
        saveButton.setDisable(true);
        newButton.setDisable(true);
    }

    public String getCurrentText() { return textArea.getText(); }

    private void layout() {
        PropertyManager manager = applicationTemplate.manager;
        NumberAxis      xAxis   = new NumberAxis();

        NumberAxis      yAxis   = new NumberAxis();

        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));
        chart.setAnimated(false);

        readOnly = new Button("Done");
        readOnly.setOnAction(e -> {
            textArea.setDisable(!textArea.isDisabled());
            AppData a = (AppData)(applicationTemplate.getDataComponent());
            if(textArea.isDisabled()) {
                a.clear();
                a.loadData(textArea.getText());
                String s = "Label names: ";
                for(String g : a.getLabelNames())
                {
                    s = s + g + " ";
                }
                AppActions ap = (AppActions) applicationTemplate.getActionComponent();
                String path = "";
                if(ap.getPath() != null)
                {
                    path = "Path: " + ap.getPath();
                }
                Text t = new Text(path);
                t.setWrappingWidth(algPane.getWidth());
                algPane.getChildren().remove(0, algPane.getChildren().size());
                algPane.getChildren().addAll(new Text("There are " + a.getNumLabels() + " labels."), new Text("There are " + a.getNumPoints() + " instances."), new Text(s), t, classifier, cluster);
                setClassifierDisable(true);
                setClusterDisable(false);
                if(a.getNumLabels() == 2 && !(a.hasNull()))
                {
                    setClassifierDisable(false);
                }

                readOnly.setText("Edit");
            }
            else
            {
                readOnly.setText("Done");
                algPane.getChildren().remove(0, algPane.getChildren().size());
                algPane.getChildren().addAll(classifier, cluster);
                setClusterDisable(true);
                setClassifierDisable(true);
            }



        });

        leftPanel = new VBox(8);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(10));

        VBox.setVgrow(leftPanel, Priority.ALWAYS);
        leftPanel.setMaxSize(windowWidth * 0.29, windowHeight );
        leftPanel.setMinSize(windowWidth * 0.29, windowHeight );

        Text   leftPanelTitle = new Text(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLE.name()));
        String fontname       = manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLEFONT.name());
        Double fontsize       = Double.parseDouble(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLESIZE.name()));
        leftPanelTitle.setFont(Font.font(fontname, fontsize));

        textArea = new TextArea();

        HBox processButtonsBox = new HBox(8);

        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String runPath = String.join(SEPARATOR,
                iconsPath,
                manager.getPropertyValue(AppPropertyTypes.RUN_ICON.name()));
        displayButton = setToolbarButton(runPath, manager.getPropertyValue(AppPropertyTypes.RUN_TOOLTIP.name()), false);

        HBox.setHgrow(processButtonsBox, Priority.ALWAYS);
        processButtonsBox.getChildren().addAll(displayButton, readOnly);

        classifier = new Button("Classification");
        cluster = new Button("Cluster");
        setClusterDisable(true);
        setClassifierDisable(true);
        algPane = new VBox();
        algPane.getChildren().addAll(new Text(""), new Text(""), new Text(""), new Text(""), classifier, cluster);

        leftPanel.getChildren().addAll(leftPanelTitle, textArea, processButtonsBox, algPane);

        StackPane rightPanel = new StackPane(chart);
        rightPanel.setMaxSize(windowWidth * 0.69, windowHeight * 0.69);
        rightPanel.setMinSize(windowWidth * 0.69, windowHeight * 0.69);
        StackPane.setAlignment(rightPanel, Pos.CENTER);

        workspace = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(workspace, Priority.ALWAYS);

        appPane.getChildren().add(workspace);
        VBox.setVgrow(appPane, Priority.ALWAYS);
        AppUI a = (AppUI)(applicationTemplate.getUIComponent()) ;

        primaryScene.getStylesheets().add(AppUI.class.getResource("chart.css").toExternalForm());
    }

    private void setWorkspaceActions() {
        setTextAreaActions();
        setDisplayButtonActions();
        AppActions a = (AppActions)(applicationTemplate.getActionComponent());
        classifier.setOnAction(e -> a.handleClassifierRequest());
        cluster.setOnAction(e -> a.handleClusterRequest());
    }

    private void setTextAreaActions() {
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.equals(oldValue)) {
                    ((AppActions) applicationTemplate.getActionComponent()).setIsUnsavedProperty(true);
                    if (newValue.charAt(newValue.length() - 1) == '\n' || newValue.isEmpty())
                        hasNewText = true;
                    newButton.setDisable(false);
                    saveButton.setDisable(false);
                }
            } catch (IndexOutOfBoundsException e) {
                System.err.println(newValue);
            }
        });
    }

    private void setDisplayButtonActions() {
        displayButton.setOnAction(event -> {
            if (hasNewText) {
                try {
                    AppData dataComponent = (AppData) applicationTemplate.getDataComponent();
                    NumberAxis y = new NumberAxis();
                    y.setUpperBound(dataComponent.getMaxY() + 3);
                    y.setLowerBound(dataComponent.getMinY() - 3);
                    y.setAutoRanging(false);
                    NumberAxis x = new NumberAxis();
                    x.setLowerBound(dataComponent.getMinX() - 3);
                    x.setUpperBound(dataComponent.getMaxX() + 3);
                    x.setAutoRanging(false);
                    StackPane right = (StackPane) workspace.getChildren().get(1);
                    right.getChildren().remove(chart);
                    chart = new LineChart<>(x, y);
                    chart.setTitle(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));
                    chart.setAnimated(false);
                    right.getChildren().add(chart);
                    AppActions a = (AppActions)applicationTemplate.getActionComponent();
                    a.handleDisplayRequest();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

    });
    }


}
