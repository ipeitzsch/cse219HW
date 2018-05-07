package ui;

import algorithm.Classifier;
import algorithm.Cluster;
import comms.AppComms;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

public class ClusterDialog {
    private String selection;
    private AppComms comms;
    private ApplicationTemplate apt;
    private Stage stage;

    public ClusterDialog(String s, AppComms comms, ApplicationTemplate apt){
        this.comms = comms;
        this.apt = apt;
        selection = s;
        stage = new Stage();
        init();
    }



    public void setSelection(String s)
    {
        selection = s;
    }

    public void show(String title) {
        stage.setTitle(title);
        stage.showAndWait();
    }


    private void init() {
        PropertyManager manager = apt.manager;

        stage.initModality(Modality.WINDOW_MODAL);
        Cluster cluster = (Cluster)comms.getAlgorithm(selection);

        VBox text = new VBox(10);
        VBox input = new VBox();
        HBox holder = new HBox();
        TextField maxIter = new TextField();
        maxIter.setText(cluster.getMaxIterations() > 0 ? cluster.getMaxIterations() + "" : "");



        TextField update = new TextField();
        update.setText(cluster.getUpdateInterval() > 0 ? cluster.getUpdateInterval() + "" : "");



        TextField num = new TextField();
        num.setText(cluster.getNumLabels() > 0 ? cluster.getNumLabels() + "" : "");



        CheckBox run = new CheckBox();
        run.setIndeterminate(false);
        run.setSelected(cluster.tocontinue());


        Button done = new Button("Done");
        done.setOnAction(e -> {

            try{
                if(Integer.parseInt(num.getText()) >= 2 || Integer.parseInt(num.getText()) <= 4) {

                    comms.setClust(selection, Integer.parseInt(maxIter.getText()), Integer.parseInt(update.getText()), run.isSelected(), Integer.parseInt(num.getText()), apt);
                    stage.close();
                }
                else
                {
                    ErrorDialog er = (ErrorDialog)(apt.getDialog(Dialog.DialogType.ERROR));
                    er.show("Error", manager.getPropertyValue(AppPropertyTypes.NUM_LABELS_ERROR.name()));
                }
            }
            catch(Exception xe)
            {

                ErrorDialog er = (ErrorDialog)(apt.getDialog(Dialog.DialogType.ERROR));
                er.show("Error", manager.getPropertyValue(AppPropertyTypes.INTEGERS_ONLY.name()));
            }
        });
        text.getChildren().addAll(new Text(manager.getPropertyValue(AppPropertyTypes.MAX_ITERATIONS.name())), new Text(manager.getPropertyValue(AppPropertyTypes.REFRESH.name())),new Text(manager.getPropertyValue(AppPropertyTypes.NUM_LABELS.name())), new Text(manager.getPropertyValue(AppPropertyTypes.CONTINUOUS.name())), done);
        input.getChildren().addAll(maxIter, update, num, run);
        holder.getChildren().addAll(text, input);
        Scene sc = new Scene(holder);
        stage.setScene(sc);
    }
}
