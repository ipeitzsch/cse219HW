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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
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
            checkValid(s);
            processor.processString(s);
            AppUI a = (AppUI)(applicationTemplate.getUIComponent());
            s = "";
            for(int i = 0; i < 10; i++)
            {
                String q = S.get(i);
                s = s + q + "\n";
            }
            a.setText(s);
            a.setChange(S);
            a.setReadOnly(true);


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
            String message = "Error on line " + (count + 1) + " in " + manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name()) + "\n\"" + e.getMessage() + "\"";
            dialog.show(errTitle, errMsg + errInput + "\n" + message);
        }
        // TODO: NOT A PART OF HW 1
    }

    public boolean checkValid(String text)
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
                            throw new Exception("Invalid/Repeated name: " + list.get(0) + ".");
                        }
                        String[] pair  = list.get(2).split(",");
                        int i = Integer.parseInt(pair[0]);
                        int j = Integer.parseInt(pair[1]);
                        a.add(0);
                    } catch (Exception e) {
                        b.set(false);
                        ErrorDialog     dialog   = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                        PropertyManager manager  = applicationTemplate.manager;
                        String          errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
                        String          errMsg   = "Data in text area is not valid. ";
                        String          errInput = "Error on line " + (a.size() + 1) + ". " + e.getMessage();
                        dialog.show(errTitle, errMsg + errInput);
                    }
                });
        return b.get();
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
