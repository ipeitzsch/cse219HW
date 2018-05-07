package actions;

import dataprocessors.TSDProcessor;
import javafx.scene.control.TextArea;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class AppActionsTest {

    String text = "";
    private boolean checkValid()
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
                        double i = Double.parseDouble(pair[0]);
                        double j = Double.parseDouble(pair[1]);
                        a.add(0);
                    } catch (Exception e) {
                        b.set(false);
                    }
                });
        return b.get();
    }
    @Test
    /*
        This is a normally formatted tsd file.
     */
    public void loadData()
    {
        try{
            Scanner sc = new Scanner("@a\tlabel1\t4,5\n");
            while(sc.hasNextLine()) {
                String s = sc.nextLine();
                text = s + "\n";
            }
            if(checkValid()) {
                TSDProcessor proc = new TSDProcessor();
                proc.processString(text);
                System.out.println(proc.getLabels());
            }
            else
            {
                System.out.println("Did not get passed.");
            }

        }
        catch(IOException e)
        {
        } catch (Exception e) {
        }
    }

    @Test
    /*
        This is a file separated with spaces instead of tabs.
        The test should not have any exceptions because checkValid will be false.
     */
    public void loadImproperData()
    {

        try{
            Scanner sc = new Scanner("@a label1 4,5\n");
            while(sc.hasNextLine()) {
                text = sc.nextLine() + "\n";
            }

            if(checkValid()) {
                TSDProcessor proc = new TSDProcessor();
                proc.processString(text);
                System.out.println(proc.getLabels());
            }
            else
            {
                System.out.println("Did not get passed.");
            }
        }
        catch(IOException e)
        {
        } catch (Exception e) {
        }
    }

    @Test
    /*
        This is a file that contains empty labels.
        This program considers this to be a null label and should not cause any exceptions
     */
    public void emptyLabels() {
        try {
            Scanner sc = new Scanner("@a\t\t4,5\n");
            while (sc.hasNextLine()) {
                text = sc.nextLine() + "\n";
            }

            if (checkValid()) {
                TSDProcessor proc = new TSDProcessor();
                proc.processString(text);
                System.out.println(proc.getLabels());
                System.out.println(proc.isNull());
            } else {
                System.out.println("Did not get passed.");
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    @Test
    /*
        This is a file containing the null label.
        The program will accept this label and mark the that there are null labels.
     */
    public void nullLabel()
    {
        try {
            Scanner sc = new Scanner("@a\tnull\t4,5\n");
            while (sc.hasNextLine()) {
                text = sc.nextLine() + "\n";
            }

            if (checkValid()) {
                TSDProcessor proc = new TSDProcessor();
                proc.processString(text);
                System.out.println(proc.getLabels());
                System.out.println(proc.isNull());
            } else {
                System.out.println("Did not get passed.");
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    @Test
    /*
        This is a file containing only numbers for labels.
        The program should handle this normally and have the labels be those numbers.
     */
    public void hasNumber()
    {
        try {
            Scanner sc = new Scanner("@a\t7\t4,5\n");
            while (sc.hasNextLine()) {
                text = sc.nextLine() + "\n";
            }

            if (checkValid()) {
                TSDProcessor proc = new TSDProcessor();
                proc.processString(text);
                System.out.println(proc.getLabels());
            } else {
                System.out.println("Did not get passed.");
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    @Test
    /*
        This is a file with labels containing special characters.
        The program will be able to handle this label normally.
     */
    public void specChar()
    {
        try {
            Scanner sc = new Scanner("@a\t@d\t4,5\n");
            while (sc.hasNextLine()) {
                text = sc.nextLine() + "\n";
            }

            if (checkValid()) {
                TSDProcessor proc = new TSDProcessor();
                proc.processString(text);
                System.out.println(proc.getLabels());
            } else {
                System.out.println("Did not get passed.");
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    @Test
    /*
        This is an empty file.
        The program should not pass anything to the TSDProcessor.
     */
    public void empty()
    {
        try {
            Scanner sc = new Scanner("");
            while (sc.hasNextLine()) {
                text = sc.nextLine() + "\n";
            }

            if (checkValid()) {
                TSDProcessor proc = new TSDProcessor();
                proc.processString(text);
                System.out.println(proc.getLabels());

            } else {
                System.out.println("Did not get passed.");
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    @Test
    /*
        This is a file that does not exist.
        The program should throw a FileNotFound exception, however the exception will be caught and handled and not pass anything to the TSDProcessor.
     */
    public void dne()
    {
        try {
            Scanner sc = new Scanner(new File("this\\file\\does\\not\\exist.txt"));
            while (sc.hasNextLine()) {
                text = sc.nextLine() + "\n";
            }

            if (checkValid()) {
                TSDProcessor proc = new TSDProcessor();
                proc.processString(text);
                System.out.println(proc.getLabels());
            } else {
                System.out.println("Did not get passed.");
            }
        } catch (IOException e) {
            System.out.println("Exception was caught.");
        } catch (Exception e) {
        }
    }

    @Test
    /*
        Saves properly formatted data to a file.
     */
    public void save()
    {
        text = "@a\tlabel1\t4,5\n";
        if(checkValid())
        {
            try
            {

                Path p = Paths.get("testRes\\testData\\output.tsd");
                p = p.toAbsolutePath();
                PrintWriter pw =  new PrintWriter(Files.newOutputStream(p));
                pw.write(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //@Test
    /*
        Saving data to a file with improperly formatted data
     */
}