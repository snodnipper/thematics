/*
 * Copyright (c) 2013 Public Health England
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers;

public class DefinedInterval extends EqualInterval {

    /**
     * Define an Defined Equal Interval Classification, such that there
     * are equally sized intervals (gaps) between the max and min
     * values.
     *
     * This method will, however, correct the max value if it is not a
     * multiple of the interval.
     *
     * Consider this two class example:
     *     3 -> 6 -> 9
     * The interval (gap / class width) is three.  The min is 3 and
     * the max is 9.
     * Note: there is ONLY one break in the above and that is 6.
     *
     * @param min the start of the measurement space
     * @param interval the interval size for the measurement space
     * @param max the end of the measurement space, which is correct as necessary.
     */
    public DefinedInterval(double min, double interval, double max) {
        super(min, classes(min, interval, max),
                measurementSpaceMax(min, interval, max));
    }

    private static int classes(double min, double interval, double max) {
        final double range = max - min;
        return (int) Math.ceil( range / interval);
    }

    private static double measurementSpaceMax(double min, double interval, double max) {
        final int classes = classes(min, interval, max);
        return classes * interval + min;
    }
}
