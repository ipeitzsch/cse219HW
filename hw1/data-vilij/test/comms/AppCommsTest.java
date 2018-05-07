package comms;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppCommsTest {


    int max;    // maxIterations
    int refresh;    // refresh period
    int num;    // number of labels
    boolean continuous; // whether the algorithm is continuous or not
    @Test
    /*
        This has valid inputs for a classifier. All Classifier settings will be printed at the end.
     */
    public void setClasssif() {
        max = 100;
        refresh = 4;
        continuous = true;
        if(max < 1)
        {
            max = 1;
        }
        if(refresh < 1)
        {
            refresh = 1;
        }
        if(refresh > max)
        {
            refresh = max;
        }
        System.out.println(max +"\t" + refresh + "\t" + continuous);
    }

    @Test
    /*
        This test involves max and refresh values less than 1.
        This is an edge case because the algorithm will still have its values set, however they will each be set to 1.
     */
    public void negVals()
    {
        max = 0;
        refresh = -3;
        continuous = true;
        if(max < 1)
        {
            max = 1;
        }
        if(refresh < 1)
        {
            refresh = 1;
        }
        if(refresh > max)
        {
            refresh = max;
        }
        System.out.println(max +"\t" + refresh + "\t" + continuous);
    }

    @Test
    /*
        This test has max >= 1 and refresh >= 1. However, refresh > max. This is an edge case because if these values were passed to the algorithm,
        it would not run properly. The program, though, will set refresh to be equivalent to max. This way, the program will only refresh once.
     */
    public void greaterRef()
    {
        max = 4;
        refresh = 100;
        continuous = true;
        if(max < 1)
        {
            max = 1;
        }
        if(refresh < 1)
        {
            refresh = 1;
        }
        if(refresh > max)
        {
            refresh = max;
        }
        System.out.println(max +"\t" + refresh + "\t" + continuous);
    }

    @Test
    /*
        This is a normal test of setting values for a Cluster algorithm.
        All set values will be printed at the end.
     */
    public void setClust() {
        max = 100;
        refresh = 4;
        num = 3;
        continuous = true;
        if(max < 1)
        {
            max = 1;
        }
        if(refresh < 1)
        {
            refresh = 1;
        }
        if(num < 2)
        {
            num = 2;
        }
        if(num > 4)
        {
            num = 4;
        }
        if(refresh > max)
        {
            refresh = max;
        }
        System.out.println(max + "\t" + refresh + "\t" + num + "\t" + continuous);
    }

    @Test
    /*
        This is a test with normal num values, but refresh and max < 1.
        This is an edge case because if the algorithm's values were set to these, it would not run properly.
        Both max and refresh will be set to 1.
     */
    public void negValsClust()
    {
        max = 0;
        refresh = -5;
        num = 3;
        continuous = true;
        if(max < 1)
        {
            max = 1;
        }
        if(refresh < 1)
        {
            refresh = 1;
        }
        if(num < 2)
        {
            num = 2;
        }
        if(num > 4)
        {
            num = 4;
        }
        if(refresh > max)
        {
            refresh = max;
        }
        System.out.println(max + "\t" + refresh + "\t" + num + "\t" + continuous);
    }

    @Test
    /*
        This test will have normal num values but max < refresh.
        This is an edge case because the algorithm would not be able tor efresh if these values were passed to it.
        Refresh will be set to max, so the algorithm will only refresh once.
     */
    public void greaterRefClust()
    {
        max = 4;
        refresh = 100;
        num = 3;
        continuous = true;
        if(max < 1)
        {
            max = 1;
        }
        if(refresh < 1)
        {
            refresh = 1;
        }
        if(num < 2)
        {
            num = 2;
        }
        if(num > 4)
        {
            num = 4;
        }
        if(refresh > max)
        {
            refresh = max;
        }
        System.out.println(max + "\t" + refresh + "\t" + num + "\t" + continuous);
    }

    @Test
    /*
        This test uses normal max and refresh values but has num > 4.
        This is an edge case because these Cluster algorithms can only handle up to 4 labels.
        Num will be set to 4.
     */
    public void greaterNum()
    {
        max = 100;
        refresh = 4;
        num = 5;
        continuous = true;
        if(max < 1)
        {
            max = 1;
        }
        if(refresh < 1)
        {
            refresh = 1;
        }
        if(num < 2)
        {
            num = 2;
        }
        if(num > 4)
        {
            num = 4;
        }
        if(refresh > max)
        {
            refresh = max;
        }
        System.out.println(max + "\t" + refresh + "\t" + num + "\t" + continuous);
    }

    @Test
    /*
        This test will have normal max and refresh, but num < 2.
        This is an edge case because these Cluster algorithms must have at least 2 labels.
        Num will be set to 2.
     */
    public void lesserNum()
    {
        max = 100;
        refresh = 4;
        num = 1;
        continuous = true;
        if(max < 1)
        {
            max = 1;
        }
        if(refresh < 1)
        {
            refresh = 1;
        }
        if(num < 2)
        {
            num = 2;
        }
        if(num > 4)
        {
            num = 4;
        }
        if(refresh > max)
        {
            refresh = max;
        }
        System.out.println(max + "\t" + refresh + "\t" + num + "\t" + continuous);
    }
}