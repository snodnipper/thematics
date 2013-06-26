/*
 * Copyright (c) 2013 Public Health England
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers.precision;

import uk.gov.phe.gis.thematics.classification.Interval;
import uk.gov.phe.gis.thematics.classification.classifiers.Util;
import uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding.Rounder;

import java.util.ArrayList;

public class EqualInterval extends uk.gov.phe.gis.thematics.classification.classifiers.EqualInterval {

    private final Rounder rounder;

    /**
     * An EqualInterval that operates according the defined precision
     *
     * The number of requested classes can easily be destroyed with this class.
     * For instance, take the classes defined by 1.0, 1.2, 1.6, 1.8 and 2.0.
     * Setting a precision of zero decimal places would wipe out all of the break points.
     */
    public EqualInterval(double min, int classes, double max, Rounder rounder) {
        super(min, classes, max);
        this.rounder = rounder;
    }

    /**
     * Return the breaks - please note, these can be different to the standard 'classes - 1' as the precision
     * and converge breakpoints.
     */
    @Override
    public double[] getBreaks() {
        return rounder.round(super.getBreaks(), new double[]{min, max});
    }

    @Override
    public int getClassNumber(double value) {
        int naturalClass = super.getClassNumber(value);

        double naturalUpperBound = min + (naturalClass * interval);
        double roundedUpperBound = rounder.round(naturalUpperBound);

        if (value > Math.min(roundedUpperBound, naturalUpperBound)) {
            return naturalClass + 1;
        } else {
            double previousUpper = rounder.round(roundedUpperBound - interval);
            if (value <= previousUpper) {
                return Math.max(naturalClass - 1, 1);
            }
            return naturalClass;
        }
    }

    @Override
    public ArrayList<Interval> getIntervals() {
        return Util.roundMaxMin(super.getIntervals(), rounder);
    }

}
