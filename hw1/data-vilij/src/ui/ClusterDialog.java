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
        Cluster cluster = comms.getClust(selection);

        VBox holder = new VBox(50);
        HBox max = new HBox(500);
        TextField maxIter = new TextField();
        maxIter.setText(cluster.getMaxIterations() > 0 ? cluster.getMaxIterations() + "" : "");
        max.getChildren().addAll(new Text(manager.getPropertyValue(AppPropertyTypes.MAX_ITERATIONS.name())), maxIter);

        HBox refresh = new HBox(500);
        TextField update = new TextField();
        update.setText(cluster.getUpdateInterval() > 0 ? cluster.getUpdateInterval() + "" : "");
        refresh.getChildren().addAll(new Text(manager.getPropertyValue(AppPropertyTypes.REFRESH.name())), update);

        HBox labels = new HBox(500);
        TextField num = new TextField();
        num.setText(cluster.getNumLabels() > 0 ? cluster.getUpdateInterval() + "" : "");
        labels.getChildren().addAll(new Text(manager.getPropertyValue(AppPropertyTypes.NUM_LABELS.name())), num);

        HBox cont = new HBox(500);
        CheckBox run = new CheckBox();
        run.setIndeterminate(false);
        run.setSelected(cluster.tocontinue());
        cont.getChildren().addAll(new Text(manager.getPropertyValue(AppPropertyTypes.CONTINUOUS.name())), run);

        Button done = new Button("Done");
        done.setOnAction(e -> {

            try{
                if(Integer.parseInt(num.getText()) >= 2 || Integer.parseInt(num.getText()) <= 4) {
                    comms.setClust(selection, Integer.parseInt(maxIter.getText()), Integer.parseInt(update.getText()), run.isSelected(), Integer.parseInt(num.getText()));
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

        holder.getChildren().addAll(max, refresh, cont, labels, done);
        Scene sc = new Scene(holder);
        stage.setScene(sc);
    }
}
