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

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;


/**
 * Created by NightMadness on 11/23/2016.
 * <p>
 * This test class will test overlaps of values with appraisal selector.
 */
@RunWith(Parameterized.class)
@SmallTest
public class AppraisalHelperTest {
    private final int howManyCheckboxesChecked;
    private final int appraisalPercentageRangeSelected;
    private final int appraisalIvRangeSelected;
    private AppraisalPercentPrefect actual;
    private final AppraisalPercentPrefect expected;

    public static ArrayList<String> results = new ArrayList<>();

    @Parameters(name = "Test {index}: Percent {0}, Checkbox {1}, IV {2}")
    public static Iterable<Object[]> data() {

        /* will generate object with all results, I kept it commented out in case we want to regenerate tests.
        ArrayList<Object[]> list = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 5; j++) {
                for (int k = 0; k <= 3; k++) {
                    list.add(new Object[]{i, j, k, null});
                }
            }
        }
        return list;*/

        return Arrays.asList(new Object[][]{
                /* AppraisalPercentage, howManyCheckboxesChecked, appraisalIvRange, expected */
                {0, 0, 0, null}, //Too many possibilities
                {0, 0, 1, new AppraisalPercentPrefect( 33,  67, 100)},
                {0, 0, 2, new AppraisalPercentPrefect( 29,  61,  93)},
                {0, 0, 3, new AppraisalPercentPrefect( 18,  49,  80)},
                {0, 0, 4, new AppraisalPercentPrefect(  0,  24,  47)},
                {0, 0, 5, new AppraisalPercentPrefect(  0,  50, 100)},
                {0, 1, 0, new AppraisalPercentPrefect(  0,  48,  96)},
                {0, 1, 1, new AppraisalPercentPrefect( 33,  65,  96)},
                {0, 1, 2, new AppraisalPercentPrefect( 29,  59,  89)},
                {0, 1, 3, new AppraisalPercentPrefect( 18,  47,  76)},
                {0, 1, 4, new AppraisalPercentPrefect(  0,  21,  42)},
                {0, 1, 5, new AppraisalPercentPrefect(  0,  48,  96)},
                {0, 2, 0, new AppraisalPercentPrefect(  0,  49,  98)},
                {0, 2, 1, new AppraisalPercentPrefect( 67,  83,  98)},
                {0, 2, 2, new AppraisalPercentPrefect( 58,  75,  91)},
                {0, 2, 3, new AppraisalPercentPrefect( 36,  57,  78)},
                {0, 2, 4, new AppraisalPercentPrefect(  0,  22,  44)},
                {0, 2, 5, new AppraisalPercentPrefect(  0,  49,  98)},
                {0, 3, 0, new AppraisalPercentPrefect(  0,  50, 100)},
                {0, 3, 1, new AppraisalPercentPrefect(100, 100, 100)},
                {0, 3, 2, new AppraisalPercentPrefect( 87,  90,  93)},
                {0, 3, 3, new AppraisalPercentPrefect( 53,  67,  80)},
                {0, 3, 4, new AppraisalPercentPrefect(  0,  24,  47)},
                {0, 3, 5, new AppraisalPercentPrefect(  0,  50, 100)},
                {1, 0, 0, new AppraisalPercentPrefect( 81,  91, 100)},
                {1, 0, 1, new AppraisalPercentPrefect( 81,  91, 100)},
                {1, 0, 2, new AppraisalPercentPrefect( 81,  87,  93)},
                {1, 0, 3, null},
                {1, 0, 4, null},
                {1, 0, 5, new AppraisalPercentPrefect( 81,  91, 100)},
                {1, 1, 0, new AppraisalPercentPrefect( 81,  89,  96)},
                {1, 1, 1, new AppraisalPercentPrefect( 81,  89,  96)},
                {1, 1, 2, new AppraisalPercentPrefect( 81,  85,  89)},
                {1, 1, 3, null},
                {1, 1, 4, null},
                {1, 1, 5, new AppraisalPercentPrefect( 81,  89,  96)},
                {1, 2, 0, new AppraisalPercentPrefect( 81,  90,  98)},
                {1, 2, 1, new AppraisalPercentPrefect( 81,  90,  98)},
                {1, 2, 2, new AppraisalPercentPrefect( 81,  86,  91)},
                {1, 2, 3, null},
                {1, 2, 4, null},
                {1, 2, 5, new AppraisalPercentPrefect( 81,  90,  98)},
                {1, 3, 0, new AppraisalPercentPrefect( 81,  91, 100)},
                {1, 3, 1, new AppraisalPercentPrefect(100, 100, 100)},
                {1, 3, 2, new AppraisalPercentPrefect( 87,  90,  93)},
                {1, 3, 3, null},
                {1, 3, 4, null},
                {1, 3, 5, new AppraisalPercentPrefect( 81,  91, 100)},
                {2, 0, 0, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 0, 1, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 0, 2, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 0, 3, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 0, 4, null},
                {2, 0, 5, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 1, 0, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 1, 1, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 1, 2, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 1, 3, new AppraisalPercentPrefect( 66,  71,  76)},
                {2, 1, 4, null},
                {2, 1, 5, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 2, 0, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 2, 1, new AppraisalPercentPrefect( 67,  74,  80)},
                {2, 2, 2, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 2, 3, new AppraisalPercentPrefect( 66,  72,  78)},
                {2, 2, 4, null},
                {2, 2, 5, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 3, 0, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 3, 1, null},
                {2, 3, 2, null},
                {2, 3, 3, new AppraisalPercentPrefect( 66,  73,  80)},
                {2, 3, 4, null},
                {2, 3, 5, new AppraisalPercentPrefect( 66,  73,  80)},
                {3, 0, 0, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 0, 1, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 0, 2, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 0, 3, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 0, 4, null},
                {3, 0, 5, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 1, 0, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 1, 1, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 1, 2, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 1, 3, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 1, 4, null},
                {3, 1, 5, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 2, 0, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 2, 1, null},
                {3, 2, 2, new AppraisalPercentPrefect( 58,  62,  65)},
                {3, 2, 3, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 2, 4, null},
                {3, 2, 5, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 3, 0, new AppraisalPercentPrefect( 51,  58,  65)},
                {3, 3, 1, null},
                {3, 3, 2, null},
                {3, 3, 3, new AppraisalPercentPrefect( 53,  59,  65)},
                {3, 3, 4, null},
                {3, 3, 5, new AppraisalPercentPrefect( 51,  58,  65)},
                {4, 0, 0, new AppraisalPercentPrefect(  0,  25,  50)},
                {4, 0, 1, new AppraisalPercentPrefect( 33,  42,  50)},
                {4, 0, 2, new AppraisalPercentPrefect( 29,  40,  50)},
                {4, 0, 3, new AppraisalPercentPrefect( 18,  34,  50)},
                {4, 0, 4, new AppraisalPercentPrefect(  0,  24,  47)},
                {4, 0, 5, new AppraisalPercentPrefect(  0,  25,  50)},
                {4, 1, 0, new AppraisalPercentPrefect(  0,  25,  50)},
                {4, 1, 1, new AppraisalPercentPrefect( 33,  42,  50)},
                {4, 1, 2, new AppraisalPercentPrefect( 29,  40,  50)},
                {4, 1, 3, new AppraisalPercentPrefect( 18,  34,  50)},
                {4, 1, 4, new AppraisalPercentPrefect(  0,  21,  42)},
                {4, 1, 5, new AppraisalPercentPrefect(  0,  25,  50)},
                {4, 2, 0, new AppraisalPercentPrefect(  0,  25,  50)},
                {4, 2, 1, null},
                {4, 2, 2, null},
                {4, 2, 3, new AppraisalPercentPrefect( 36,  43,  50)},
                {4, 2, 4, new AppraisalPercentPrefect(  0,  22,  44)},
                {4, 2, 5, new AppraisalPercentPrefect(  0,  25,  50)},
                {4, 3, 0, new AppraisalPercentPrefect(  0,  25,  50)},
                {4, 3, 1, null},
                {4, 3, 2, null},
                {4, 3, 3, null},
                {4, 3, 4, new AppraisalPercentPrefect(  0,  24,  47)},
                {4, 3, 5, new AppraisalPercentPrefect(  0,  25,  50)},
                {5, 0, 0, new AppraisalPercentPrefect(  0,  50, 100)},
                {5, 0, 1, new AppraisalPercentPrefect( 33,  67, 100)},
                {5, 0, 2, new AppraisalPercentPrefect( 29,  61,  93)},
                {5, 0, 3, new AppraisalPercentPrefect( 18,  49,  80)},
                {5, 0, 4, new AppraisalPercentPrefect(  0,  24,  47)},
                {5, 0, 5, new AppraisalPercentPrefect(  0,  50, 100)},
                {5, 1, 0, new AppraisalPercentPrefect(  0,  48,  96)},
                {5, 1, 1, new AppraisalPercentPrefect( 33,  65,  96)},
                {5, 1, 2, new AppraisalPercentPrefect( 29,  59,  89)},
                {5, 1, 3, new AppraisalPercentPrefect( 18,  47,  76)},
                {5, 1, 4, new AppraisalPercentPrefect(  0,  21,  42)},
                {5, 1, 5, new AppraisalPercentPrefect(  0,  48,  96)},
                {5, 2, 0, new AppraisalPercentPrefect(  0,  49,  98)},
                {5, 2, 1, new AppraisalPercentPrefect( 67,  83,  98)},
                {5, 2, 2, new AppraisalPercentPrefect( 58,  75,  91)},
                {5, 2, 3, new AppraisalPercentPrefect( 36,  57,  78)},
                {5, 2, 4, new AppraisalPercentPrefect(  0,  22,  44)},
                {5, 2, 5, new AppraisalPercentPrefect(  0,  49,  98)},
                {5, 3, 0, new AppraisalPercentPrefect(  0,  50, 100)},
                {5, 3, 1, new AppraisalPercentPrefect(100, 100, 100)},
                {5, 3, 2, new AppraisalPercentPrefect( 87,  90,  93)},
                {5, 3, 3, new AppraisalPercentPrefect( 53,  67,  80)},
                {5, 3, 4, new AppraisalPercentPrefect(  0,  24,  47)},
                {5, 3, 5, new AppraisalPercentPrefect(  0,  50, 100)}
        });
    }

    /**
     * This gets parameters from parametrized test.
     * @param appraisalPercentageRangeSelected Selected percent appraisal
     * @param howManyCheckboxesChecked Selected appraisal checkboxes
     * @param appraisalIvRangeSelected Selected IV appraisal
     * @param expected expect object value for the test.
     */
    public AppraisalHelperTest(int appraisalPercentageRangeSelected, int howManyCheckboxesChecked,
                               int appraisalIvRangeSelected,
                               AppraisalPercentPrefect expected) {
        this.appraisalPercentageRangeSelected = appraisalPercentageRangeSelected;
        this.howManyCheckboxesChecked = howManyCheckboxesChecked;
        this.appraisalIvRangeSelected = appraisalIvRangeSelected;
        this.expected = expected;
    }

    /**
     * This setups the actual variable, it is the in the setUp method since it *could* fail.
     */
    @Before
    public void setUp() {
        actual = AppraisalHelper.calculateAppraisalPercentPrefect(
                appraisalPercentageRangeSelected,
                howManyCheckboxesChecked,
                appraisalIvRangeSelected);
    }

    //Display all results for an easy way to update the parameter list
    @AfterClass
    public static void displayResults() {
        Assume.assumeTrue(results != null && !results.isEmpty());
        System.out.println("Actual Results:");
        System.out.println(String.format("{%1s, %2s, %3s, %4s}",
                "appraisalPercentage", "howManyCheckboxesChecked",
                "appraisalIvRange", "expected(Low, Ave, High)"));
        for (String result: results) {
            System.out.println(result);
        }
    }

    @Test
    //Check that the Nullability of expected and actual are the same.
    public void nullCheck() {
        saveResults();
        Assert.assertEquals("One the values is null.", expected == null, actual == null);
    }

    /*
    will put all tested parameters in an object with actual results so we can read
    those later
    */
    private void saveResults() {
        if (actual == null) {
            results.add(String.format("{%1$1d, %2$1d, %3$1d, null},",
                    appraisalPercentageRangeSelected, howManyCheckboxesChecked, appraisalIvRangeSelected));
        } else if (actual != null) {
            results.add(String.format("{%1$1d, %2$1d, %3$1d, new AppraisalPercentPrefect(%4$3s, %5$3s, %6$3s)},",
                    appraisalPercentageRangeSelected, howManyCheckboxesChecked, appraisalIvRangeSelected,
                    actual.getLow(), actual.getAve(), actual.getHigh()));
        }
    }

    //If both are null we can ignore the test since nullCheck is already checked
    private boolean isActualAndExpectedNotNull() {
        return (actual != null && expected != null);
    }

    //Assume costs 60 MS! No thank you...
    @Test
    public void lowCheck() {
        if (!isActualAndExpectedNotNull()) {
            return;
        }
        //Assume.assumeTrue("Ignore test if both expected and actual are null.", isActualAndExpectedNotNull());
        assertEquals("Low: ", expected.getLow(), actual.getLow());
    }

    @Test
    public void aveCheck() {
        if (!isActualAndExpectedNotNull()) {
            return;
        }
        //Assume.assumeTrue("Ignore test if both expected and actual are null.", isActualAndExpectedNotNull());
        assertEquals("Ave: ", expected.getAve(), actual.getAve());
    }

    @Test
    public void highCheck() {
        if (!isActualAndExpectedNotNull()) {
            return;
        }
        //Assume.assumeTrue("Ignore test if both expected and actual are null.", isActualAndExpectedNotNull());
        assertEquals("High: ", expected.getHigh(), actual.getHigh());
    }

}
