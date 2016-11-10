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

import static junit.framework.Assert.assertEquals;

public class SignificantFiguresTest extends IntervalTest {

    private static Logger sLogger = LogManager.getLogger(SignificantFiguresTest.class);

    @Test
    public void rounding() throws NoSuchFieldException, IllegalAccessException {
        double delta = 0.0000001d;

        // subtest 1 - round off decimal places
        SignificantFigures precision = new SignificantFigures(5);
        double value = 23456.987654321d;
        double expected = 23457;
        double actual = precision.round(value);
        assertEquals(expected, actual, delta);

        // subtest 2 - round down
        precision = new SignificantFigures(2);
        value = 0.741;
        expected = 0.74;
        actual = precision.round(value);
        assertEquals(expected, actual, delta);

        // subtest 3 - round up
        precision = new SignificantFigures(2);
        value = 0.739;
        expected = 0.74;
        actual = precision.round(value);
        assertEquals(expected, actual, delta);

        // subtest 4 - round up
        precision = new SignificantFigures(1);
        value = 0.739;
        expected = 0.7;
        actual = precision.round(value);
        assertEquals(expected, actual, delta);

        // subtest 5 - round down
        precision = new SignificantFigures(1);
        value = 3.739;
        expected = 4;
        actual = precision.round(value);
        assertEquals(expected, actual, delta);

        // subtest 5 - round down
        precision = new SignificantFigures(2);
        value = 3.739;
        expected = 3.7;
        actual = precision.round(value);
        assertEquals(expected, actual, delta);
    }

    @Override
    protected Logger getLogger() {
        return sLogger;
    }
}
