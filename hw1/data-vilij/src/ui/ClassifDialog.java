package ui;

import algorithm.Classifier;
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

public class ClassifDialog {
    private String selection;
    private AppComms comms;
    private ApplicationTemplate apt;
    private Stage stage;

    public ClassifDialog(String s, AppComms comms, ApplicationTemplate apt){
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
        Classifier classif = comms.getClassif(selection);

        VBox holder = new VBox(50);
        HBox max = new HBox(500);
        TextField maxIter = new TextField();
        maxIter.setText(classif.getMaxIterations() > 0 ? classif.getMaxIterations() + "" : "");
        max.getChildren().addAll(new Text(manager.getPropertyValue(AppPropertyTypes.MAX_ITERATIONS.name())), maxIter);

        HBox refresh = new HBox(500);
        TextField update = new TextField();
        update.setText(classif.getUpdateInterval() > 0 ? classif.getUpdateInterval() + "" : "");
        refresh.getChildren().addAll(new Text(manager.getPropertyValue(AppPropertyTypes.REFRESH.name())), update);

        HBox cont = new HBox(500);
        CheckBox run = new CheckBox();
        run.setIndeterminate(false);
        run.setSelected(classif.tocontinue());
        cont.getChildren().addAll(new Text(manager.getPropertyValue(AppPropertyTypes.CONTINUOUS.name())), run);

        Button done = new Button("Done");
        done.setOnAction(e -> {
            try{
                comms.setClasssif(selection, Integer.parseInt(maxIter.getText()), Integer.parseInt(update.getText()), run.isSelected());
                stage.close();
            }
            catch(Exception xe)
            {
                ErrorDialog er = (ErrorDialog)(apt.getDialog(Dialog.DialogType.ERROR));
                er.show("Error", manager.getPropertyValue(AppPropertyTypes.INTEGERS_ONLY.name()));
            }
        });

        holder.getChildren().addAll(max, refresh, cont, done);
        Scene sc = new Scene(holder);
        stage.setScene(sc);
    }
}
