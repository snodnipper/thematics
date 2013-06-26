/*
 * Copyright (c) 2013 Public Health England
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

import java.lang.reflect.Field;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class DefinedIntervalTest extends IntervalTest {

    private static Logger sLogger = LogManager.getLogger(DefinedIntervalTest.class);

    @Test
    public void intervals() throws NoSuchFieldException, IllegalAccessException {
        // Access private field
        Field classCount = uk.gov.phe.gis.thematics.classification.classifiers.EqualInterval.class.getDeclaredField("classes");
        classCount.setAccessible(true);

        // subtest 1 - perfect match
        DefinedInterval definedInterval = new DefinedInterval(50d /*min*/, 50d /*interval size*/, 500d /*max*/, new DecimalPlaces(1));
        int actual = classCount.getInt(definedInterval);
        int expected = 9;
        assertEquals(expected, actual);

        // subtest 2 - extra class required
        definedInterval = new DefinedInterval(50d /*min*/, 50d /*interval size*/, 501d /*max*/, new DecimalPlaces(1));
        actual = classCount.getInt(definedInterval);
        expected = 10;
        assertEquals(expected, actual);

        // subtest 3 - nasty decimal fraction
        definedInterval = new DefinedInterval(1d /*min*/, 3.333333333333334d /*interval size*/, 11d /*max*/, new DecimalPlaces(1));
        actual = classCount.getInt(definedInterval);
        expected = 3;
        assertEquals(expected, actual);

        // subtest 4 - zero based minimum
        definedInterval = new DefinedInterval(0d/*min*/, 5d /*interval size*/, 25d /*max*/, new DecimalPlaces(1));
        actual = classCount.getInt(definedInterval);
        expected = 5;
        assertEquals(expected, actual);

        // subtest 5 - negative minimum
        definedInterval = new DefinedInterval(-10d/*min*/, 5d /*interval size*/, 25d /*max*/, new DecimalPlaces(1));
        actual = classCount.getInt(definedInterval);
        expected = 7;
        assertEquals(expected, actual);

        // subtest 6 - negative measurement space
        definedInterval = new DefinedInterval(-80d/*min*/, 10d /*interval size*/, -20d /*max*/, new DecimalPlaces(1));
        actual = classCount.getInt(definedInterval);
        expected = 6;
        assertEquals(expected, actual);

        // subtest 7 - expected intervals
        definedInterval = new DefinedInterval(1d/*min*/, 5d /*interval*/, 13d /*max*/, new DecimalPlaces(1));

        ArrayList<Interval> expectedI = new ArrayList<Interval>();
        expectedI.add(new Interval(1d, 6d));
        expectedI.add(new Interval(6d, 11d));
        expectedI.add(new Interval(11d, 16d));

        ArrayList<Interval> actualI = definedInterval.getIntervals();
        checkIntervals(expectedI, actualI);

        // subtest 8 -
        definedInterval = new DefinedInterval(3d/*min*/, 3d /*interval*/, 12d /*max*/, new DecimalPlaces(1));

        expectedI = new ArrayList<Interval>();
        expectedI.add(new Interval(3d, 6d));
        expectedI.add(new Interval(6d, 9d));
        expectedI.add(new Interval(9d, 12d));

        actualI = definedInterval.getIntervals();
        checkIntervals(expectedI, actualI);

        // subtest 9 - redundant test _but_ shows correct behaviour for these numbers
        definedInterval = new DefinedInterval(3d/*min*/, 5000001d /*interval*/, 12d /*max*/, new DecimalPlaces(1));

        expectedI = new ArrayList<Interval>();
        expectedI.add(new Interval(3d, 5000004d));

        actualI = definedInterval.getIntervals();
        checkIntervals(expectedI, actualI);

        // ### Precision specific tests ###
        // as before, nice numbers
        definedInterval = new DefinedInterval(3d/*min*/, 3.25d /*interval*/, 16d /*max*/, new DecimalPlaces(1));
        expectedI = new ArrayList<Interval>();
        expectedI.add(new Interval(3.0d, 6.3d));
        expectedI.add(new Interval(6.3d, 9.5d));
        expectedI.add(new Interval(9.5d, 12.8d));
        expectedI.add(new Interval(12.8d, 16.0d));

        actualI = definedInterval.getIntervals();
        checkIntervals(expectedI, actualI);
    }

    @Override
    protected Logger getLogger() {
        return sLogger;
    }
}
