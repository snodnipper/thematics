/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers.precision;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import uk.gov.phe.gis.thematics.classification.Interval;
import uk.gov.phe.gis.thematics.classification.classifiers.IntervalTest;
import uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding.DecimalPlaces;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class ManualIntervalTest extends IntervalTest {

    private static Logger sLogger = LogManager.getLogger(ManualIntervalTest.class);

    @Test
    public void breaks() {
        double min = 10d;
        double max = 100d;
        double[] breaks = new double[]{15d, 20d, 40d, 80d};
        ManualInterval manualInterval = new ManualInterval(min, breaks, max, new DecimalPlaces(0));

        double[] expected = breaks;
        double[] actual = manualInterval.getBreaks();
        checkBreaks(expected, actual);

        // ### Precision specific tests ###
        breaks = new double[]{15.1d, 20.9d, 40.5d, 80.0d};
        manualInterval = new ManualInterval(min, breaks, max, new DecimalPlaces(0));
        expected = new double[]{15d, 21d, 41d, 80d};
        actual = manualInterval.getBreaks();
        checkBreaks(expected, actual);

        breaks = new double[]{15.16d, 20.91d, 40.59d, 80.01d};
        manualInterval = new ManualInterval(min, breaks, max, new DecimalPlaces(1));
        expected = new double[]{15.2d, 20.9d, 40.6d, 80.0d};
        actual = manualInterval.getBreaks();
        checkBreaks(expected, actual);
    }

    @Test
    public void classes() {
        double[] breaks = new double[]{6d, 9d};
        ManualInterval manualInterval = new ManualInterval(3, breaks, 12, new DecimalPlaces(1));

        int actual = manualInterval.getClassNumber(3d);
        int expected = 1;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(3.1d);
        expected = 1;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(6d);
        expected = 1;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(6.1d);
        expected = 2;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(9d);
        expected = 2;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(9.1d);
        expected = 3;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(12d);
        expected = 3;
        assertEquals(expected, actual);

        // ### Precision specific tests ###
        breaks = new double[]{6.94d, 9.65d}; // => 6.9 and 9.7
        manualInterval = new ManualInterval(3, breaks, 12, new DecimalPlaces(1));

        actual = manualInterval.getClassNumber(3d);
        expected = 1;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(3.1d);
        expected = 1;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(6.90d);
        expected = 1;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(6.91d);
        expected = 2;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(9.70d);
        expected = 2;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(9.71d);
        expected = 3;
        assertEquals(expected, actual);

        actual = manualInterval.getClassNumber(12d);
        expected = 3;
        assertEquals(expected, actual);

    }

    @Test
    public void intervals() {
        double min = 10d;
        double max = 100d;
        double[] breaks = new double[]{15d, 20d, 40d};

        ManualInterval manualInterval = new ManualInterval(min, breaks, max, new DecimalPlaces(0));

        ArrayList<Interval> expected = new ArrayList<Interval>();
        expected.clear();
        expected.add(new Interval(10d, 15d));
        expected.add(new Interval(15d, 20d));
        expected.add(new Interval(20d, 40d));
        expected.add(new Interval(40d, 100d));
        ArrayList<Interval> actual = manualInterval.getIntervals();
        checkIntervals(expected, actual);
    }

    @Override
    protected Logger getLogger() {
        return sLogger;
    }
}
