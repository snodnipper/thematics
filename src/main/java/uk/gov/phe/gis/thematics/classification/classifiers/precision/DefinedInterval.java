/*
 * Copyright (c) 2013 Public Health England
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers.precision;

import uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding.Rounder;

public class DefinedInterval extends EqualInterval {

    public DefinedInterval(double min, double interval, double max, Rounder rounder) {
        super(min, classes(min, interval, max),
                measurementSpaceMax(min, interval, max), rounder);
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
