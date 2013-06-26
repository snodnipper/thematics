/*
 * Copyright (c) 2013 Public Health England
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers;

import uk.gov.phe.gis.thematics.classification.Interval;

import java.util.ArrayList;

public abstract class AbstractInterval {

    protected final double max;
    protected final double min;

    /**
     * @param min value of the measurement space
     * @param max value of the measurement space
     */
    public AbstractInterval(double min, double max) {
        this.max = max;
        this.min = min;
    }

    public abstract double[] getBreaks();

    public abstract int getClassNumber(double value);

    public ArrayList<Interval> getIntervals() {
        double[] breaks = getBreaks();
        final int classes = breaks.length + 1;
        ArrayList<Interval> intervals = new ArrayList<Interval>();

        if (breaks.length == 0) {
            intervals.add(new Interval(min, max));
            return intervals;
        }

        // First interval
        intervals.add(new Interval(min, breaks[0]));
        // Mid intervals
        for (int clas = 2; clas < classes; clas++) {
            intervals.add(new Interval(breaks[clas - 2], breaks[clas - 1]));
        }
        // Last interval
        intervals.add((new Interval(breaks[classes - 2], max)));
        return intervals;
    }

    protected void checkRange(double value) {
        if (value < min || value > max) {
            throw new IllegalStateException("Value outside of classification range (" + value +")");
        }
    }
}
