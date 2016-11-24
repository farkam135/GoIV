package com.kamron.pogoiv.logic;

import android.support.test.filters.SmallTest;

import com.kamron.pogoiv.logic.AppraisalHelper.AppraisalPercentPrefect;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;


/**
 * Created by NightMadness on 11/23/2016.
 * <p>
 * This test class will test overlaps of values with appraisal selector.
 */
@RunWith(Parameterized.class)
@SmallTest
public class AppraisalHelperTest{
    int appraisalPercentageRangeSelected;
    int appraisalIvRangeSelected;
    AppraisalPercentPrefect actual;
    AppraisalPercentPrefect expected;
    public static TreeMap<String, AppraisalPercentPrefect> results = new TreeMap<>();

    @Parameters(name = "Test {index}: Percent {0}, IV {1}")
    public static Iterable<Object[]> data() {

        /* will generate object with all results, I kept it commented out in case we want to regenerate tests.
        ArrayList<Object[]> list = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 5; j++) {
                list.add(new Object[]{i,  j, null});
            }
        }
       return list;*/

        return Arrays.asList(new Object[][]{
                {0, 0, null}, //Too many possibilities
                {0, 1, new AppraisalPercentPrefect(33, 67, 100)},
                {0, 2, new AppraisalPercentPrefect(29, 61, 93)},
                {0, 3, new AppraisalPercentPrefect(18, 49, 80)},
                {0, 4, new AppraisalPercentPrefect(0, 24, 47)},
                {0, 5, new AppraisalPercentPrefect(0, 50, 100)},
                {1, 0, new AppraisalPercentPrefect(81, 91, 100)},
                {1, 1, new AppraisalPercentPrefect(81, 91, 100)},
                {1, 2, new AppraisalPercentPrefect(81, 87, 93)},
                {1, 3, null},
                {1, 4, null},
                {1, 5, new AppraisalPercentPrefect(81, 91, 100)},
                {2, 0, new AppraisalPercentPrefect(66, 73, 80)},
                {2, 1, new AppraisalPercentPrefect(66, 73, 80)},
                {2, 2, new AppraisalPercentPrefect(66, 73, 80)},
                {2, 3, new AppraisalPercentPrefect(66, 73, 80)},
                {2, 4, null},
                {2, 5, new AppraisalPercentPrefect(66, 73, 80)},
                {3, 0, new AppraisalPercentPrefect(51, 58, 65)},
                {3, 1, new AppraisalPercentPrefect(51, 58, 65)},
                {3, 2, new AppraisalPercentPrefect(51, 58, 65)},
                {3, 3, new AppraisalPercentPrefect(51, 58, 65)},
                {3, 4, null},
                {3, 5, new AppraisalPercentPrefect(51, 58, 65)},
                {4, 0, new AppraisalPercentPrefect(0, 25, 50)},
                {4, 1, new AppraisalPercentPrefect(33, 42, 50)},
                {4, 2, new AppraisalPercentPrefect(29, 40, 50)},
                {4, 3, new AppraisalPercentPrefect(18, 34, 50)},
                {4, 4, new AppraisalPercentPrefect(0, 24, 47)},
                {4, 5, new AppraisalPercentPrefect(0, 25, 50)},
                {5, 0, new AppraisalPercentPrefect(0, 50, 100)},
                {5, 1, new AppraisalPercentPrefect(33, 67, 100)},
                {5, 2, new AppraisalPercentPrefect(29, 61, 93)},
                {5, 3, new AppraisalPercentPrefect(18, 49, 80)},
                {5, 4, new AppraisalPercentPrefect(0, 24, 47)},
                {5, 5, new AppraisalPercentPrefect(0, 50, 100)}
        });
    }


    public AppraisalHelperTest(int appraisalPercentageRangeSelected, int appraisalIvRangeSelected,
                               AppraisalPercentPrefect expected) {
        this.appraisalPercentageRangeSelected = appraisalPercentageRangeSelected;
        this.appraisalIvRangeSelected = appraisalIvRangeSelected;
        this.expected = expected;
    }

    @Before
    public void calculateAppraisalPercentPrefect() {
        actual = AppraisalHelper
                .calculateAppraisalPercentPrefect(appraisalPercentageRangeSelected,
                        appraisalIvRangeSelected);

        //will put all tested parameters in an object with actual results so we can read those later
        results.put(
                String.format("%1d, %2d", appraisalPercentageRangeSelected, appraisalIvRangeSelected)
                , actual);

    }

    //display all results for an easy way to update the parameter list
    @AfterClass
    public static void displayResults() {
        Assume.assumeTrue(results != null);
        for (HashMap.Entry<String, AppraisalPercentPrefect> x : results.entrySet()) {

            final AppraisalPercentPrefect result = x.getValue();
            if (result == null) {
                System.out.println(String.format("{%1s, null}, ", x.getKey()));

            } else if (result != null) {
                System.out.println(String.format("{%1s, new AppraisalHelper.AppraisalPercentPrefect(%2s, %3s, %4s)},",
                        x.getKey(), result.getLow(), result.getAve(), result.getHigh()));
            }
        }
    }


    @Test
    // Check that the Nullability of both expected and actual are the same.
    public void nullCheck() {
        Assert.assertEquals("One the values is null.", expected == null, actual == null);
    }

    //if both are null we can ignore the test since nullCheck is already checked
    public boolean isActualAndExpectedNotNull() {
        return (actual != null && expected != null);
    }

    //Assume costs 15 MS! No thank you...
    @Test
    public void lowCheck() {
        if (!isActualAndExpectedNotNull()) {
            return;
        }
        //Assume.assumeTrue("Ignore test if both expected and actual are null.", isActualAndExpectedNotNull());
        assertEquals("Low: ", actual.getLow(), expected.getLow());
    }

    @Test
    public void aveCheck() {
        if (!isActualAndExpectedNotNull()) {
            return;
        }
        //Assume.assumeTrue("Ignore test if both expected and actual are null.", isActualAndExpectedNotNull());
        assertEquals("Ave: ", actual.getAve(), expected.getAve());
    }

    @Test
    public void highCheck() {
        if (!isActualAndExpectedNotNull()) {
            return;
        }
        //Assume.assumeTrue("Ignore test if both expected and actual are null.", isActualAndExpectedNotNull());
        assertEquals("High: ", actual.getHigh(), expected.getHigh());
    }

}
