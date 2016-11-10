/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import uk.gov.phe.gis.thematics.classification.classifiers.IntervalTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static junit.framework.Assert.assertEquals;

public class DecimalPlacesTest extends IntervalTest {

    private static Logger sLogger = LogManager.getLogger(DecimalPlacesTest.class);

    @Test
    public void rounding() throws NoSuchFieldException, IllegalAccessException {
        double delta = 0.0000001d;

        // subtest 1 - round off decimal places
        DecimalPlaces dp = new DecimalPlaces(5);
        double value = 23456.987654321d;
        double expected = 23456.98765;
        double actual = dp.round(value);
        assertEquals(expected, actual, delta);

        // subtest 2 - round down
        dp = new DecimalPlaces(2);
        value = 0.741;
        expected = 0.74;
        actual = dp.round(value);
        assertEquals(expected, actual, delta);

        // subtest 3 - round up
        dp = new DecimalPlaces(2);
        value = 0.739;
        expected = 0.74;
        actual = dp.round(value);
        assertEquals(expected, actual, delta);

        // subtest 4 - round up
        dp = new DecimalPlaces(1);
        value = 0.739;
        expected = 0.7;
        actual = dp.round(value);
        assertEquals(expected, actual, delta);

        // subtest 5 - round down
        dp = new DecimalPlaces(1);
        value = 3.739;
        expected = 3.7;
        actual = dp.round(value);
        assertEquals(expected, actual, delta);

        // subtest 5 - round down
        dp = new DecimalPlaces(2);
        value = 3.739;
        expected = 3.74;
        actual = dp.round(value);
        assertEquals(expected, actual, delta);

        // subtest 6 - we always round 'up', which is defined in Java as away from zero.
        // JavaScript is the same with #toFixed.
        value = -76.555d;
        expected = -76.56d;
        actual = dp.round(value);
        assertEquals(expected, actual, delta);

        // subtest 7
        value = 76.555d;
        expected = 76.56d;
        actual = dp.round(value);
        assertEquals(expected, actual, delta);

        // subtest 8 - rounding this double has undesirable results when rounding floating values (Java and JavaScript)
        BigDecimal cleaned = new BigDecimal(String.valueOf(-40.555d)).setScale(2, RoundingMode.HALF_UP);
        actual = cleaned.doubleValue();
        assertEquals(-40.56d, actual);
    }

    @Override
    protected Logger getLogger() {
        return sLogger;
    }
}
