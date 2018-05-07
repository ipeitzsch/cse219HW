package actions;

import algorithm.Algorithm;
import algorithm.Classifier;
import algorithm.Cluster;
import comms.AppComms;
import dataprocessors.AlgProcessor;
import dataprocessors.AppData;

import dataprocessors.DataSet;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import settings.AppPropertyTypes;
import ui.AppUI;
import ui.ClassifDialog;
import ui.ClusterDialog;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.SAVE_WORK_TITLE;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    private Path dataFilePath;

    /** The boolean property marking whether or not there are any unsaved changes. */
    private SimpleBooleanProperty isUnsaved;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private AppComms comms;
    private ArrayList<CheckBox> boxes;
    private String selected;
    private AtomicBoolean firstClick = new AtomicBoolean(true);
    private AtomicInteger count = new AtomicInteger();
    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        this.isUnsaved = new SimpleBooleanProperty(false);
        comms = new AppComms(applicationTemplate);
        boxes = new ArrayList<>();
    }

    public void setIsUnsavedProperty(boolean property) { isUnsaved.set(property); }
    public SimpleBooleanProperty getIsUnsavedProperty() {return isUnsaved;}
    @Override
    public void handleNewRequest() {
        try {
            if (!isUnsaved.get() || promptToSave()) {
                applicationTemplate.getDataComponent().clear();
                applicationTemplate.getUIComponent().clear();
                isUnsaved.set(false);
                dataFilePath = null;
                ((AppData)(applicationTemplate.getDataComponent())).setLoaded(false);
            }
        } catch (IOException e) { errorHandlingHelper(); }
    }
    private boolean checkValid(String text)
    {
        ArrayList<Integer> a = new ArrayList<>();
        AtomicBoolean b = new AtomicBoolean();
        SortedSet<String> g = new TreeSet<>();

        Stream.of(text.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    try {
                        b.set(true);
                        if(!(list.get(0).startsWith("@")) || !(g.add(list.get(0))))
                        {
                            throw new Exception(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.INVALID_NAME_ERROR.name()) + list.get(0) + ".");
                        }
                        String[] pair  = list.get(2).split(",");
                        double i = Double.parseDouble(pair[0]);
                        double j = Double.parseDouble(pair[1]);
                        a.add(0);
                    } catch (Exception e) {

                        ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                        PropertyManager manager  = applicationTemplate.manager;
                        String          errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
                        String          errMsg   = manager.getPropertyValue(AppPropertyTypes.INVALID_TEXT_FIELD.name());
                        String          errInput = "Error on line " + (a.size() + 1) + ". " + e.getMessage();
                        dialog.show(errTitle, errMsg + errInput);
                        b.set(false);
                    }
                });
        return b.get();
    }
    public String getPath()
    {
        if(dataFilePath != null)
            return dataFilePath.toString();
        return null;
    }
    public void handleDisplayRequest(){
            if(selected != null)
            {
                Algorithm c = comms.getAlgorithm(selected);
                DataSet d = new DataSet();


                AppUI a = (AppUI)applicationTemplate.getUIComponent();
                AppData ap = (AppData)applicationTemplate.getDataComponent();

                if(firstClick.get())
                {
                    ap.clear();
                    String s = a.getCurrentText();
                    if(ap.isLoaded())
                    {
                        s = ap.getLoadedData();
                    }
                    a.getChart().getData().clear();
                    ap.loadData(s);
                    ap.displayData();
                }

                d.setLabels(ap.getLabels());
                d.setLocations(ap.getPoints());
                c.setDataset(d);

                AlgProcessor cp = new AlgProcessor(ap);
                c.setCP(cp);

                if(c.tocontinue()) {
                    isRunning.set(true);
                    a.disableDisp(true);
                    a.setScreenShotDisable(true);

                        c.run();
                        if(c instanceof Classifier)
                        {
                            ap.displayLine(isRunning, true, count, new AtomicBoolean(false));
                        }
                        else if(c instanceof Cluster)
                        {
                            ap.displayClust(isRunning, true, count, new AtomicBoolean(false));
                        }


                }
                else
                {
                        isRunning.set(true);
                        a.disableDisp(true);
                        a.setScreenShotDisable(true);
                        if(firstClick.get()) {
                            c.run();
                            firstClick.set(false);
                        }
                        if(c instanceof Classifier)
                        {

                            ap.displayLine(isRunning, false, count, firstClick);
                        }
                        else if(c instanceof Cluster)
                        {
                            ap.displayClust(isRunning, false, count, firstClick);
                        }
                        isRunning.set(false);
                        a.disableDisp(false);
                        a.setScreenShotDisable(false);
                }


            }

    }
    @Override
    public void handleSaveRequest() {

        PropertyManager    manager = applicationTemplate.manager;
        if(!(checkValid(((AppUI) applicationTemplate.getUIComponent()).getCurrentText())))
            return;
        if(dataFilePath == null)
        {
            FileChooser fileChooser = new FileChooser();
            String      dataDirPath = "/" + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
            URL         dataDirURL  = getClass().getResource(dataDirPath);

            if (dataDirURL == null)
                errorHandlingHelper();

            fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
            fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

            String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
            String extension   = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
            ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                    String.format("*.%s", extension));

            fileChooser.getExtensionFilters().add(extFilter);
            File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
            if (selected != null) {
                dataFilePath = selected.toPath();
                try {
                    save();
                    isUnsaved.set(false);
                    AppUI a = (AppUI)(applicationTemplate.getUIComponent());
                    a.setSaveDisable(true);
                }
                catch(IOException e)
                {
                    errorHandlingHelper();
                }
            }
            else
            {
                errorHandlingHelper();
            }
        }
        else
        {
            try {
                save();
                isUnsaved.set(false);
                AppUI a = (AppUI)(applicationTemplate.getUIComponent());
                a.setSaveDisable(true);
            }
            catch(IOException e)
            {
                errorHandlingHelper();
            }
        }
        // TODO: DISABLE BUTTON
    }

    @Override
    public void handleLoadRequest() {
        PropertyManager    manager = applicationTemplate.manager;
        String      dataDirPath = "/" + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
        URL         dataDirURL  = getClass().getResource(dataDirPath);

        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
        fileChooser.setTitle(manager.getPropertyValue(AppPropertyTypes.LOAD_WORK_TITLE.name()));

        String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
        String extension   = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
        ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                String.format("*%s", extension));

        fileChooser.getExtensionFilters().add(extFilter);
        File selected = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());


        if(selected != null)
        {
                AppData a = (AppData)(applicationTemplate.getDataComponent());
                a.clear();
                AppUI b = (AppUI)(applicationTemplate.getUIComponent());
                b.clear();
                dataFilePath = selected.toPath();
                a.loadData(dataFilePath);

        }
    }

    @Override
    public void handleExitRequest() {
        try {

            if(isRunning.get())
            {
                ConfirmationDialog     dialog   = (ConfirmationDialog) applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
                PropertyManager manager  = applicationTemplate.manager;
                String          errTitle = manager.getPropertyValue(AppPropertyTypes.STILL_RUNNING_TITLE.name());
                String          errMsg   = manager.getPropertyValue(AppPropertyTypes.STILL_RUNNING_MSG.name());

                dialog.show(errTitle, errMsg);
                if(dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES))
                {
                    isRunning.set(false);
                }
            }
            if ((!isUnsaved.get() || promptToSave()) && !isRunning.get())
                System.exit(0);
        } catch (IOException e) { errorHandlingHelper(); }
    }

    @Override
    public void handlePrintRequest() {
        
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        AppUI a = (AppUI)(applicationTemplate.getUIComponent());
        Image img = a.getChart().snapshot(new SnapshotParameters(), null);
        PropertyManager manager = applicationTemplate.manager;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(manager.getPropertyValue(AppPropertyTypes.SAVE_SCRNSHOT_TITLE.name()));

        String description = manager.getPropertyValue(AppPropertyTypes.SAVE_SCRNSHOT_DESC.name());
        String extension = manager.getPropertyValue(AppPropertyTypes.SAVE_SCRNSHOT_EXT.name());

        ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                String.format("*.%s", extension));
        fileChooser.getExtensionFilters().addAll(extFilter);
        File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        if (file != null) {
            ImageIO.write(SwingFXUtils.fromFXImage(img,null), manager.getPropertyValue(AppPropertyTypes.SAVE_SCRNSHOT_FORMAT.name()), file);
            a.setScreenShotDisable(true);
        }

    }

    public void handleClassifierRequest()
    {
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = "/" + String.join("/",
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String optionsPath = String.join("/",
                iconsPath,
                manager.getPropertyValue(AppPropertyTypes.OPTIONS_ICON.name()));
        AppUI a = (AppUI)(applicationTemplate.getUIComponent());
        VBox algPane = new VBox(8);
        for(String s : comms.getClassNames()) {
            HBox holder = new HBox(8);
            CheckBox c = new CheckBox();
            c.setSelected(false);
            c.setAllowIndeterminate(false);
            boxes.add(c);
            c.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(newValue)
                    {
                        for(CheckBox g : boxes)
                        {
                            if(g != c)
                            {
                                g.setSelected(false);
                            }
                        }
                        selected = s;
                    }
                }
            });
            Button options = new Button(null, new ImageView(new Image(getClass().getResourceAsStream(optionsPath))));
            options.setTooltip(new Tooltip(manager.getPropertyValue(AppPropertyTypes.OPTIONS_TOOLTIP.name())));
            options.setOnAction(e -> {
                ClassifDialog cl = new ClassifDialog(s, comms, applicationTemplate);

                cl.show(s + manager.getPropertyValue(AppPropertyTypes.OPTIONS.name()));
            });
            holder.getChildren().addAll(c, new Text(s), options);
            algPane.getChildren().add(holder);
        }

        a.setAlgPane(algPane);

    }

    public void handleClusterRequest()
    {
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = "/" + String.join("/",
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String optionsPath = String.join("/",
                iconsPath,
                manager.getPropertyValue(AppPropertyTypes.OPTIONS_ICON.name()));
        AppUI a = (AppUI)(applicationTemplate.getUIComponent());
        VBox algPane = new VBox(8);
        for(String s : comms.getClustNames()) {
            HBox holder = new HBox(8);

            CheckBox c = new CheckBox();
            c.setSelected(false);
            c.setAllowIndeterminate(false);
            boxes.add(c);
            c.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(newValue)
                    {
                        for(CheckBox g : boxes)
                        {
                            if(g != c)
                            {
                                g.setSelected(false);
                            }
                        }
                        selected = s;
                    }
                }
            });

            Button options = new Button(null, new ImageView(new Image(getClass().getResourceAsStream(optionsPath))));
            options.setTooltip(new Tooltip(manager.getPropertyValue(AppPropertyTypes.OPTIONS_TOOLTIP.name())));
            options.setOnAction(e -> {
                ClusterDialog cl = new ClusterDialog(s, comms, applicationTemplate);

                cl.show(s + manager.getPropertyValue(AppPropertyTypes.OPTIONS.name()));
            });
            holder.getChildren().addAll(c, new Text(s), options);
            algPane.getChildren().add(holder);
        }

        a.setAlgPane(algPane);

    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        PropertyManager    manager = applicationTemplate.manager;
        ConfirmationDialog dialog  = ConfirmationDialog.getDialog();
        dialog.show(manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE.name()),
                    manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK.name()));

        if (dialog.getSelectedOption() == null) return false; // if user closes dialog using the window's close button

        if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)) {
            if (dataFilePath == null) {
                FileChooser fileChooser = new FileChooser();
                String      dataDirPath = "/" + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
                URL         dataDirURL  = getClass().getResource(dataDirPath);

                if (dataDirURL == null)
                    throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));

                fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
                fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

                String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
                String extension   = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
                ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                                                                String.format("*.%s", extension));

                fileChooser.getExtensionFilters().add(extFilter);
                File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                if (selected != null) {
                    dataFilePath = selected.toPath();
                    save();
                } else return false; // if user presses escape after initially selecting 'yes'
            } else
                save();
        }

        return !dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL);
    }

    private void save() throws IOException {
        applicationTemplate.getDataComponent().saveData(dataFilePath);
        isUnsaved.set(false);
    }

    private void errorHandlingHelper() {
        ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager  = applicationTemplate.manager;
        String          errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
        String          errMsg   = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
        String          errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
        dialog.show(errTitle, errMsg + errInput);
    }


}
