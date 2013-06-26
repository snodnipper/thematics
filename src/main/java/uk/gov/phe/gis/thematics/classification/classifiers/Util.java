/*
 * Copyright (c) 2013 Public Health England
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers;

import org.apache.commons.lang3.ArrayUtils;
import uk.gov.phe.gis.thematics.classification.Interval;
import uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding.DecimalPlaces;
import uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding.Rounder;
import uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding.SignificantFigures;

import java.util.ArrayList;

public class Util {

    public static Rounder bestRounder(double min, double max, int precision) {
        DecimalPlaces dp = new DecimalPlaces(precision);
        double dpRange = dp.roundUp(max) - dp.roundDown(min);
        SignificantFigures sf = new SignificantFigures(precision);
        double sfRange = sf.roundUp(max) - sf.roundDown(min);

        if (dpRange == 0 && sfRange == 0) {
            return null;
        }
        double acceptableDifference = 9 / Math.pow(10, precision);
        if (dpRange < acceptableDifference ) {
            return sf;
        }
        return dp;
    }

    /**
     * Constructs a valid manual interval from invalid input
     *
     * Given extreme breakpoints that are beyond the dataset max or min
     * values, this method will assign extreme breakpoints to the
     * respective max or min value - therefore overriding the max/min
     * data values.
     *
     * It also removes break points that are maximum or minimum values
     * and thus does not support zero width classes.
     *
     * Note: this method is required only because the UI may attempt to
     * create an invalid ManualInterval from user input by incorrectly
     * using the dataset max and min values to define the measurement
     * space, which may be invalid for the breakpoints.
     * This code is therefore a helper method for the UI code.
     *
     * @param min the possibly invalid minimum value for the measurement space that may be adjusted
     * @param breaks an ascending sorted list of break points
     * @param max the possibly invalid maximum value for the measurement space that may be adjusted
     */
    public static ManualInterval cleanedManualInterval(double min, double[] breaks, double max) {
        if (breaks.length > 1 && breaks[breaks.length - 1] >= max) {
            max = breaks[breaks.length - 1];
            breaks = ArrayUtils.remove(breaks, breaks.length - 1);
        }
        if (breaks.length > 1 && breaks[0] <= min) {
            min = breaks[0];
            breaks = ArrayUtils.remove(breaks, 0);
        }
        return new ManualInterval(min, breaks, max);
    }

    public static ArrayList<Interval> roundMaxMin(ArrayList<Interval> intervals, Rounder rounder) {
        int size = intervals.size();
        if (size > 1) {
            Interval i = intervals.get(0);
            double previous = i.getLowerBound();
            double rounded = rounder.roundDown(i.getLowerBound());
            if (previous!=rounded) {
                Interval newInterval = new Interval(rounded, i.getUpperBound());
                intervals.remove(i);
                intervals.add(0, newInterval);
            }
        }
        if (size > 1) {
            Interval i = intervals.get(size - 1);
            double previous = i.getUpperBound();
            double rounded = rounder.roundUp(i.getUpperBound());
            if (previous!=rounded) {
                Interval newInterval = new Interval(i.getLowerBound(), rounded);
                intervals.remove(i);
                intervals.add(newInterval);
            }
        }
        return intervals;
    }
}
