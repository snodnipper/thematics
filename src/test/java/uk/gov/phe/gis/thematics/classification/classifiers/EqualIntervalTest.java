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

public class EqualIntervalTest extends IntervalTest {

    private static Logger sLogger = LogManager.getLogger(EqualIntervalTest.class);

    @Test
    public void breaks() {
        // subtest 1
        EqualInterval equalInterval = new EqualInterval(1, 3, 11);
        double[] breaks = equalInterval.getBreaks();
        checkBreaks(breaks, 4.333333333333334d, 7.666666666666667d);

        // subtest 2
        equalInterval = new EqualInterval(0, 3, 9);
        breaks = equalInterval.getBreaks();
        checkBreaks(breaks, 3d, 6d);

        // subtest 3
        equalInterval = new EqualInterval(1, 5/*classes*/, 3);
        breaks = equalInterval.getBreaks();
        checkBreaks(breaks, 1.4d, 1.8d, 2.2d, 2.6d);

        // subtest 4
        equalInterval = new EqualInterval(1/*min*/, 3 /*classes*/, 3 /*max*/);
        breaks = equalInterval.getBreaks();
        checkBreaks(breaks, 1.6666666666666665d, 2.333333333333333d);

        // subtest 5
        equalInterval = new EqualInterval(-80d, 6, -20d);
        breaks = equalInterval.getBreaks();
        checkBreaks(breaks, -70d, -60d, -50d, -40d, -30d);
    }

    @Test
    public void classes() {
        // subtest 1 -  test boundaries
        // subtest 1.1 - min value should be in the first class
        EqualInterval equalInterval = new EqualInterval(5, 3, 20);
        int actual = equalInterval.getClassNumber(5);
        int expected = 1;
        assertEquals(expected, actual);

        // subtest 1.2
        actual = equalInterval.getClassNumber(5.1);
        expected = 1;
        assertEquals(expected, actual);

        // subtest 1.3 - end of first class
        actual = equalInterval.getClassNumber(10);
        expected = 1;
        assertEquals(expected, actual);

        // subtest 1.4 - start of second class
        actual = equalInterval.getClassNumber(10.1d);
        expected = 2;
        assertEquals(expected, actual);

        // subtest 1.5 - end of second class
        actual = equalInterval.getClassNumber(15d);
        expected = 2;
        assertEquals(expected, actual);

        // subtest 1.6 - start of last class
        actual = equalInterval.getClassNumber(15.1d);
        expected = 3;
        assertEquals(expected, actual);

        // subtest 1.7 - end of last class
        actual = equalInterval.getClassNumber(20d);
        expected = 3;
        assertEquals(expected, actual);

        // subtest 2 - zero min value
        // subtest 2.1 - min value should be in the first class
        equalInterval = new EqualInterval(0, 3, 15);
        actual = equalInterval.getClassNumber(0);
        expected = 1;
        assertEquals(expected, actual);

        // subtest 2.2 - test end of first class
        actual = equalInterval.getClassNumber(5);
        expected = 1;
        assertEquals(expected, actual);

        // subtest 2.3 - test end of second class
        actual = equalInterval.getClassNumber(10);
        expected = 2;
        assertEquals(expected, actual);

        // subtest 2.3 - test start of last class
        actual = equalInterval.getClassNumber(10.1);
        expected = 3;
        assertEquals(expected, actual);

        // subtest 2.3 - test end of last class
        actual = equalInterval.getClassNumber(15);
        expected = 3;
        assertEquals(expected, actual);

        // subtest 3 - just an example
        equalInterval = new EqualInterval(1, 3, 15);
        actual = equalInterval.getClassNumber(13);
        expected = 3;
        assertEquals(expected, actual);
    }

    @Test
    public void intervals() {
        // subtest 1
        EqualInterval equalInterval = new EqualInterval(1/*min*/, 3 /*classes*/, 11 /*max*/);

        ArrayList<Interval> expected = new ArrayList<Interval>();
        expected.add(new Interval(1d, 4.333333333333334d));
        expected.add(new Interval(4.333333333333334d, 7.666666666666667d));
        expected.add(new Interval(7.666666666666667d, 11.0d));

        ArrayList<Interval> actual = equalInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 2 - fails in ESRI / Omair's algorithm but works in QGIS.
        equalInterval = new EqualInterval(1/*min*/, 5 /*classes*/, 3 /*max*/);
        expected.clear();
        expected.add(new Interval(1d, 1.4d));
        expected.add(new Interval(1.4d, 1.8d));
        expected.add(new Interval(1.8d, 2.2d));
        expected.add(new Interval(2.2d, 2.6d));
        expected.add(new Interval(2.6d, 3.0d));
        actual = equalInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 3 - no breaks
        equalInterval = new EqualInterval(1/*min*/, 1 /*classes*/, 3 /*max*/);
        expected.clear();
        expected.add(new Interval(1d, 3d));
        actual = equalInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 4 - minor difference to Omair's Java algorithm; probably only rounding [1].
        equalInterval = new EqualInterval(1/*min*/, 3 /*classes*/, 3 /*max*/);
        expected.clear();
        expected.add(new Interval(1d, 1.6666666666666665d));
        expected.add(new Interval(1.6666666666666665d, 2.333333333333333d));
        expected.add(new Interval(2.333333333333333d, 3d));
        actual = equalInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 5 - negative values
        equalInterval = new EqualInterval(-80d, 6, -20d);
        expected.clear();
        expected.add(new Interval(-80d, -70d));
        expected.add(new Interval(-70d, -60d));
        expected.add(new Interval(-60d, -50d));
        expected.add(new Interval(-50d, -40d));
        expected.add(new Interval(-40d, -30d));
        expected.add(new Interval(-30d, -20d));
        actual = equalInterval.getIntervals();
        checkIntervals(expected, actual);
    }

    @Override
    protected Logger getLogger() {
        return sLogger;
    }
}

/*
[1]: output:
    =< 1.67
        Value: 1.0
    =< 2.34
        Value: 2.0
    =< 3.01
        Value: 3.0
 */
