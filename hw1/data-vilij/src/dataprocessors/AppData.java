package dataprocessors;

import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.ConfirmationDialog;
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
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        File f = dataFilePath.toFile();
        try {
            Scanner sc = new Scanner(f);
            String s = "";
            int count = 0;
            SortedSet<String> set = new TreeSet<>();
            while(sc.hasNextLine())
            {
                String s1 = sc.nextLine();
                String t[] = s1.split("\t");
                if(t.length == 3 && set.add(t[0]))
                {
                    count++;
                    s = s + s1 + "\n";
                }
                else
                {
                    ErrorDialog dia = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                    PropertyManager manager  = applicationTemplate.manager;
                    String          errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
                    String message = "Error on line " + (count + 1) + " in " + manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
                    dia.show(errTitle, message);
                }
            }
            processor.processString(s);
            AppUI a = (AppUI)(applicationTemplate.getUIComponent());
            a.setText(s);
            a.setReadOnly(true);
            a.setRowCount(10);

            ErrorDialog c = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            int cc;
            if(count < 10)
            {
                cc = count;
            }
            else
            {
                cc = 10;
            }
            c.show("Load File", "Successfully loaded " + count + " lines. " + cc + " lines are shown in the text area.");

        }
        catch(Exception e){
            ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager  = applicationTemplate.manager;
            String          errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String          errMsg   = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String          errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
            dialog.show(errTitle, errMsg + errInput);
        }
        // TODO: NOT A PART OF HW 1
    }

    public void loadData(String dataString) {
        try {
            processor.processString(dataString);
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
        try (PrintWriter writer = new PrintWriter(Files.newOutputStream(dataFilePath))) {
            writer.write(((AppUI) applicationTemplate.getUIComponent()).getCurrentText());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
