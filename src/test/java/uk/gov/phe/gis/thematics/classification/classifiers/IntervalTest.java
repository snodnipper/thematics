/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers;

import org.apache.log4j.Logger;
import uk.gov.phe.gis.thematics.classification.Interval;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public abstract class IntervalTest {

    protected abstract Logger getLogger();

    protected void checkBreaks(double[] actual, double... expected) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            getLogger().debug("Checking: " + i + ", " + expected[i]);
            assertEquals(expected[i], actual[i]);
        }
        getLogger().debug("---");
    }

    protected void checkIntervals(List<Interval> expected, List<Interval> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            getLogger().debug("Checking: " + expected.get(i));
            assertEquals(expected.get(i), actual.get(i));
        }
        getLogger().debug("---");
    }
}
