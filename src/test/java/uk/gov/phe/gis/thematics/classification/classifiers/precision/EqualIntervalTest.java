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
import uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding.SignificantFigures;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class EqualIntervalTest extends IntervalTest {

    private static Logger sLogger = LogManager.getLogger(EqualIntervalTest.class);

    @Test
    public void breaks() {

        // subtest 1
        uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(1.00, 3, 11.00, new DecimalPlaces(2));
        double[] breaks = equalInterval.getBreaks();
        checkBreaks(breaks, 4.33d, 7.67d);

        // subtest 2
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 9, new DecimalPlaces(0));
        breaks = equalInterval.getBreaks();
        checkBreaks(breaks, 3d, 6d);

        // subtest 3
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(1, 5/*classes*/, 3, new DecimalPlaces(1));
        breaks = equalInterval.getBreaks();
        checkBreaks(breaks, 1.4d, 1.8d, 2.2d, 2.6d);

        // subtest 4
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(1/*min*/, 3 /*classes*/, 3 /*max*/, new DecimalPlaces(2));
        breaks = equalInterval.getBreaks();
        checkBreaks(breaks, 1.67d, 2.33d);

        // subtest 5
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(-80d, 6, -20d, new DecimalPlaces(0));
        breaks = equalInterval.getBreaks();
        checkBreaks(breaks, -70d, -60d, -50d, -40d, -30d);

        // ### Precision specific tests ###
        // as before, nice numbers
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(1, 5/*classes*/, 3, new DecimalPlaces(1));
        breaks = equalInterval.getBreaks();
        checkBreaks(breaks, 1.4d, 1.8d, 2.2d, 2.6d);

        // as before, ram down the precision - this is nasty!
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(1, 5/*classes*/, 3, new DecimalPlaces(0));
        breaks = equalInterval.getBreaks();
        checkBreaks(breaks, 2.0d);
    }

    @Test
    public void classes() {
        // subtest 1 -  test boundaries
        // subtest 1.1 - min value should be in the first class
        uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(5, 3, 20, new DecimalPlaces(2));
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
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 15, new DecimalPlaces(2));
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
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(1, 3, 15, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(13);
        expected = 3;
        assertEquals(expected, actual);

        // ### Precision specific tests ###

        // subtest 4

        // test the minimum value
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 10, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(0);
        expected = 1;
        assertEquals(expected, actual);

        // test the first natural break
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 10, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(3.33);
        expected = 1;
        assertEquals(expected, actual);

        // test the first actual break
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 10, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(3.3);
        expected = 1;
        assertEquals(expected, actual);

        // test before first break
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 10, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(3.329);
        expected = 1;
        assertEquals(expected, actual);

        // test after first break
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 10, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(3.331);
        expected = 2;
        assertEquals(expected, actual);

        // natural end of the second class
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 10, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(6.66);
        expected = 2;
        assertEquals(expected, actual);

        // actual end of the second class based on precision
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 10, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(6.67);
        expected = 2;
        assertEquals(expected, actual);

        // beginning of the last class
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 10, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(6.68);
        expected = 3;
        assertEquals(expected, actual);

        // end of the last class
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 10, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(10);
        expected = 3;
        assertEquals(expected, actual);

        // subtest 5
        // same as subtest 4 with offset

        // test the minimum value
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(5, 3, 15, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(5);
        expected = 1;
        assertEquals(expected, actual);

        // test the first break
        actual = equalInterval.getClassNumber(8.33);
        expected = 1;
        assertEquals(expected, actual);

        // test before first break
        actual = equalInterval.getClassNumber(8.329);
        expected = 1;
        assertEquals(expected, actual);

        // test after first break
        actual = equalInterval.getClassNumber(8.331);
        expected = 2;
        assertEquals(expected, actual);

        // natural end of the second class
        actual = equalInterval.getClassNumber(11.66);
        expected = 2;
        assertEquals(expected, actual);

        // actual end of the second class based on precision
        actual = equalInterval.getClassNumber(11.67);
        expected = 2;
        assertEquals(expected, actual);

        // beginning of the last class
        actual = equalInterval.getClassNumber(11.68);
        expected = 3;
        assertEquals(expected, actual);

        // end of the last class
        actual = equalInterval.getClassNumber(15);
        expected = 3;
        assertEquals(expected, actual);


        // subtest 5
        // same as subtest 4 with offset

        // test the minimum value
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(5, 3, 15, new DecimalPlaces(2));
        actual = equalInterval.getClassNumber(5);
        expected = 1;
        assertEquals(expected, actual);

        // test the first break
        actual = equalInterval.getClassNumber(8.33);
        expected = 1;
        assertEquals(expected, actual);

        // test before first break
        actual = equalInterval.getClassNumber(8.329);
        expected = 1;
        assertEquals(expected, actual);

        // test after first break
        actual = equalInterval.getClassNumber(8.331);
        expected = 2;
        assertEquals(expected, actual);

        // natural end of the second class
        actual = equalInterval.getClassNumber(11.66);
        expected = 2;
        assertEquals(expected, actual);

        // actual end of the second class based on precision
        actual = equalInterval.getClassNumber(11.67);
        expected = 2;
        assertEquals(expected, actual);

        // beginning of the last class
        actual = equalInterval.getClassNumber(11.68);
        expected = 3;
        assertEquals(expected, actual);

        // end of the last class
        actual = equalInterval.getClassNumber(15);
        expected = 3;
        assertEquals(expected, actual);

        //####################
        // subtest 6 - ensure modifying breaks are used

        // test the minimum value
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0, 3, 10, new DecimalPlaces(0));
        actual = equalInterval.getClassNumber(0);
        expected = 1;
        assertEquals(expected, actual);

        // test the first natural break is in the second class because the modified break is '3'
        actual = equalInterval.getClassNumber(3.33);
        expected = 2;
        assertEquals(expected, actual);

        // test the first modified break
        actual = equalInterval.getClassNumber(3.0);
        expected = 1;
        assertEquals(expected, actual);

        // test before first natural break is in the second class because the modified break is '3'
        actual = equalInterval.getClassNumber(3.329);
        expected = 2;
        assertEquals(expected, actual);

        // test before first modified break
        actual = equalInterval.getClassNumber(2.99);
        expected = 1;
        assertEquals(expected, actual);

        // test after first natural break
        actual = equalInterval.getClassNumber(3.331);
        expected = 2;
        assertEquals(expected, actual);

        // test after first modified break
        actual = equalInterval.getClassNumber(3.01);
        expected = 2;
        assertEquals(expected, actual);

        // natural end of the second class - remember, to zero decimal places the true break is 7
        actual = equalInterval.getClassNumber(6.66);
        expected = 2;
        assertEquals(expected, actual);

        // modified end of the second class
        actual = equalInterval.getClassNumber(7);
        expected = 2;
        assertEquals(expected, actual);

        // beginning of the last natural class - remember, to zero decimal places the true break is 7
        actual = equalInterval.getClassNumber(6.67);
        expected = 2;
        assertEquals(expected, actual);

        // beginning of the last modified class
        actual = equalInterval.getClassNumber(7.01);
        expected = 3;
        assertEquals(expected, actual);

        // test before end of measurement space
        actual = equalInterval.getClassNumber(9.999);
        expected = 3;
        assertEquals(expected, actual);

        // end of the last natural and modified class (measurement space 'max')
        actual = equalInterval.getClassNumber(10);
        expected = 3;
        assertEquals(expected, actual);

        //####################
        // subtest 7 - negative values
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(-80.555d, 6, -20.555d, new DecimalPlaces(2));

        // test the minimum value
        actual = equalInterval.getClassNumber(-80.555d);
        expected = 1;
        assertEquals(expected, actual);

        // test first break
        actual = equalInterval.getClassNumber(-70.56d);
        expected = 1;
        assertEquals(expected, actual);

        // test before first break
        actual = equalInterval.getClassNumber(-70.561d);
        expected = 1;
        assertEquals(expected, actual);

        // test after first break
        actual = equalInterval.getClassNumber(-70.559d);
        expected = 2;
        assertEquals(expected, actual);

        // test the maximum value
        actual = equalInterval.getClassNumber(-20.555);
        expected = 6;
        assertEquals(expected, actual);

        // test last break
        actual = equalInterval.getClassNumber(-30.56);
        expected = 5;
        assertEquals(expected, actual);

        // test before last break
        actual = equalInterval.getClassNumber(-30.561);
        expected = 5;
        assertEquals(expected, actual);

        // test after last break
        actual = equalInterval.getClassNumber(-30.559);
        expected = 6;
        assertEquals(expected, actual);

        // subtest 7 - significant figure classes
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(0.0010d, 5, 0.0020d, new SignificantFigures(2));
        // test minimum
        actual = equalInterval.getClassNumber(0.0010d);
        expected = 1;
        assertEquals(expected, actual);

        // test first break
        actual = equalInterval.getClassNumber(0.0012d);
        expected = 1;
        assertEquals(expected, actual);

        // test after first break
        actual = equalInterval.getClassNumber(0.00121d);
        expected = 2;
        assertEquals(expected, actual);

        // test second break
        actual = equalInterval.getClassNumber(0.0014d);
        expected = 2;
        assertEquals(expected, actual);

        // test after second break
        actual = equalInterval.getClassNumber(0.00141d);
        expected = 3;
        assertEquals(expected, actual);

        // test last break
        actual = equalInterval.getClassNumber(0.0018d);
        expected = 4;
        assertEquals(expected, actual);

        // test after last break
        actual = equalInterval.getClassNumber(0.00181d);
        expected = 5;
        assertEquals(expected, actual);

        // test max
        actual = equalInterval.getClassNumber(0.002d);
        expected = 5;
        assertEquals(expected, actual);
    }

    @Test
    public void intervals() {
        // subtest 1
        uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(1/*min*/, 3 /*classes*/, 11 /*max*/, new DecimalPlaces(2));

        ArrayList<Interval> expected = new ArrayList<Interval>();
        expected.add(new Interval(1d, 4.33d));
        expected.add(new Interval(4.33d, 7.67d));
        expected.add(new Interval(7.67d, 11.0d));

        ArrayList<Interval> actual = equalInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 2 - fails in ESRI / Omair's algorithm but works in QGIS.
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(1/*min*/, 5 /*classes*/, 3 /*max*/, new DecimalPlaces(2));
        expected.clear();
        expected.add(new Interval(1d, 1.4d));
        expected.add(new Interval(1.4d, 1.8d));
        expected.add(new Interval(1.8d, 2.2d));
        expected.add(new Interval(2.2d, 2.6d));
        expected.add(new Interval(2.6d, 3.0d));
        actual = equalInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 3 - no breaks
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(1/*min*/, 1 /*classes*/, 3 /*max*/, new DecimalPlaces(2));
        expected.clear();
        expected.add(new Interval(1d, 3d));
        actual = equalInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 4 - minor difference to Omair's Java algorithm; probably only rounding [1].
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(1/*min*/, 3 /*classes*/, 3 /*max*/, new DecimalPlaces(2));
        expected.clear();
        expected.add(new Interval(1d, 1.67d));
        expected.add(new Interval(1.67d, 2.33d));
        expected.add(new Interval(2.33d, 3d));
        actual = equalInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 5 - negative values
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(-80d, 6, -20d, new DecimalPlaces(2));
        expected.clear();
        expected.add(new Interval(-80d, -70d));
        expected.add(new Interval(-70d, -60d));
        expected.add(new Interval(-60d, -50d));
        expected.add(new Interval(-50d, -40d));
        expected.add(new Interval(-40d, -30d));
        expected.add(new Interval(-30d, -20d));
        actual = equalInterval.getIntervals();
        checkIntervals(expected, actual);

        // ### Precision specific tests ###
        // subtest 6
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(-80.555d, 6, -20.555d, new DecimalPlaces(2));
        expected.clear();
        expected.add(new Interval(-80.56d, -70.56d));
        expected.add(new Interval(-70.56d, -60.56d));
        expected.add(new Interval(-60.56d, -50.56d));
        expected.add(new Interval(-50.56d, -40.56d));
        expected.add(new Interval(-40.56d, -30.56d));
        expected.add(new Interval(-30.56d, -20.55d));
        actual = equalInterval.getIntervals();
        checkIntervals(expected, actual);

        // subtest 7 - intervals were checked by hand in Excel and are good
        equalInterval = new uk.gov.phe.gis.thematics.classification.classifiers.precision.EqualInterval(-0.0006d, 6, -0.0005d, new SignificantFigures(2));
        expected.clear();
        expected.add(new Interval(-6.0E-4d, -5.8E-4d));
        expected.add(new Interval(-5.8E-4d, -5.7E-4d));
        expected.add(new Interval(-5.7E-4d, -5.5E-4d));
        expected.add(new Interval(-5.5E-4d, -5.3E-4d));
        expected.add(new Interval(-5.3E-4d, -5.2E-4d));
        expected.add(new Interval(-5.2E-4d, -5.0E-4d));

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
