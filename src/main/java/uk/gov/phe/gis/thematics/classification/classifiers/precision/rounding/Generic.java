/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class Generic {

    public abstract double round(double value);

    public double[] round(double[] values, double[] excludes) {
        SortedSet<Double> excludeSet = new TreeSet<Double>(Arrays.asList(ArrayUtils.toObject(excludes)));
        SortedSet<Double> breakSet = new TreeSet<Double>();

        for (double b : values) {
            double roundedBreak = round(b);
            if (excludeSet.contains(roundedBreak) == false) {
                breakSet.add(roundedBreak);
            }
        }
        return ArrayUtils.toPrimitive(breakSet.toArray(new Double[breakSet.size()]));
    }
}
