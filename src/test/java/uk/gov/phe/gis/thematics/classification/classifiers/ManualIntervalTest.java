/*
 * Copyright (c) 2013 Public Health England
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import uk.gov.phe.gis.thematics.classification.Interval;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class ManualIntervalTest extends IntervalTest {

    private static Logger sLogger = LogManager.getLogger(ManualIntervalTest.class);

    @Test
    public void breaks() {
        double min = 10d;
        double max = 100d;
        double[] breaks = new double[]{15d, 20d, 40d, 80d};
        ManualInterval manualInterval = new ManualInterval(min, breaks, max);

        double[] expected = breaks;
        double[] actual = manualInterval.getBreaks();
        checkBreaks(expected, actual);
    }

    @Test
    public void classes() {
        double[] breaks = new double[]{6d, 9d};
        ManualInterval manualInterval = new ManualInterval(3, breaks, 12);

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
    }

    @Test
    public void intervals() {
        double min = 10d;
        double max = 100d;
        double[] breaks = new double[]{15d, 20d, 40d};

        ManualInterval manualInterval = new ManualInterval(min, breaks, max);

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
