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
import uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding.DecimalPlaces;
import uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding.Rounder;
import uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding.SignificantFigures;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class UtilTest extends IntervalTest {

    private static Logger sLogger = LogManager.getLogger(UtilTest.class);

    @Test
    public void bestRounder() {
        Rounder best = Util.bestRounder(0d, 0d, 0);
        assertEquals(null, best);

        best = Util.bestRounder(1d, 10d, 0);
        assertEquals(DecimalPlaces.class, best.getClass());

        best = Util.bestRounder(0.001d, 0.009d, 2);
        assertEquals(SignificantFigures.class, best.getClass());

        best = Util.bestRounder(1.001d, 2.009d, 2);
        assertEquals(DecimalPlaces.class, best.getClass());

        best = Util.bestRounder(54.001d, 1000.009d, 2);
        assertEquals(DecimalPlaces.class, best.getClass());

        best = Util.bestRounder(54.009d, 1000.001d, 2);
        assertEquals(DecimalPlaces.class, best.getClass());

        best = Util.bestRounder(0.000000005d, 0.000000006d, 2);
        assertEquals(SignificantFigures.class, best.getClass());

        best = Util.bestRounder(100.005d, 110.005d, 2);
        assertEquals(DecimalPlaces.class, best.getClass());
    }

    /**
     * We test using Decimal Places _but_ are testing the update logic here and
     * there it shouldn't make any difference if, say, significant figures were
     * used instead
     */
    @Test
    public void roundMaxMin() {
        // subtest 1 - expect to round the min down and the max up
        ArrayList<Interval> sourceI = new ArrayList<Interval>();
        sourceI.add(new Interval(3.645, 9.454d));
        sourceI.add(new Interval(9.454d, 19.453d));
        ArrayList<Interval> actualI = Util.roundMaxMin(sourceI, new DecimalPlaces(2));

        // assert that min/max are good _and_ other intervals are not altered
        ArrayList<Interval> expectedI = new ArrayList<Interval>();
        expectedI.add(new Interval(3.64/*expected min*/, 9.454d));
        expectedI.add(new Interval(9.454d, 19.46d/*expected max*/));
        checkIntervals(expectedI, actualI);

        // subtest 2 - check that only the first and last interval are modified
        sourceI = new ArrayList<Interval>();
        sourceI.add(new Interval(3.98765d, 9.454d));
        sourceI.add(new Interval(9.454d, 13.12345d));
        sourceI.add(new Interval(13.12345d, 19.12345d));

        expectedI = new ArrayList<Interval>();
        expectedI.add(new Interval(3.98d, 9.454d));
        expectedI.add(new Interval(9.454d, 13.12345d));
        expectedI.add(new Interval(13.12345d, 19.13d));

        actualI = Util.roundMaxMin(sourceI, new DecimalPlaces(2));
        checkIntervals(expectedI, actualI);
    }

    @Test
    public void cleanedManualInterval() {
        // subtest 1 - min and max inside all break values
        double min = 20d;
        double max = 30d;
        double[] breaks = new double[]{10d, 40d, 100d};
        ManualInterval manualInterval = Util.cleanedManualInterval(min, breaks, max);
        ArrayList<Interval> expected = new ArrayList<Interval>();
        expected.clear();
        expected.add(new Interval(10d, 40d));
        expected.add(new Interval(40d, 100d));
        ArrayList<Interval> actual = manualInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 2 - min value and break value the same BUT no zero width class
        breaks = new double[]{20d, 25d};
        manualInterval = Util.cleanedManualInterval(min, breaks, max);
        expected = new ArrayList<Interval>();
        expected.clear();
        expected.add(new Interval(20d, 25d));
        expected.add(new Interval(25d, 30d));
        actual = manualInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 3 - max value and break value the same
        breaks = new double[]{25d, 30d};
        manualInterval = Util.cleanedManualInterval(min, breaks, max);
        expected = new ArrayList<Interval>();
        expected.clear();
        expected.add(new Interval(20d, 25d));
        expected.add(new Interval(25d, 30d));
        actual = manualInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 4 - extreme break points override max and min data values
        breaks = new double[]{10d, 40d, 100d};
        manualInterval = Util.cleanedManualInterval(min, breaks, max);
        expected = new ArrayList<Interval>();
        expected.clear();
        expected.add(new Interval(10d, 40d));
        expected.add(new Interval(40d, 100d));
        actual = manualInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 5 - normal behaviour is not overridden.
        breaks = new double[]{23d, 26d, 28d};
        manualInterval = Util.cleanedManualInterval(min, breaks, max);
        expected = new ArrayList<Interval>();
        expected.clear();
        expected.add(new Interval(20d, 23d));
        expected.add(new Interval(23d, 26d));
        expected.add(new Interval(26d, 28d));
        expected.add(new Interval(28d, 30d));
        actual = manualInterval.getIntervals();
        checkIntervals(expected, actual);
    }

    @Override
    protected Logger getLogger() {
        return sLogger;
    }
}
