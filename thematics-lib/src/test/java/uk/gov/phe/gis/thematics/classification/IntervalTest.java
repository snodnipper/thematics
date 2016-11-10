/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class IntervalTest {

    @Test
    public void hashesCorrectly() {
        Interval a = new Interval(5d, 10d);
        Interval b = new Interval(5d, 10d);

        assertEquals(a.hashCode(), b.hashCode());

        Interval c = new Interval(15d, 100d);
        assertThat(a, not(equalTo(c)));

        Interval d = new Interval(15d, 100d);
        assertEquals(c.hashCode(), d.hashCode());
    }

    @Test
    public void stringValue() {
        Interval interval = new Interval(45d, 67d);
        String actual = interval.toString();
        String expected = "45.0 - 67.0";
        assertEquals(expected, actual);

        interval = new Interval(0d, 15.5d);
        actual = interval.toString();
        expected = "0.0 - 15.5";
        assertEquals(expected, actual);
    }
}
