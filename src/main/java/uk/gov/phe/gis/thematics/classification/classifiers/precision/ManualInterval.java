/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers.precision;

import uk.gov.phe.gis.thematics.classification.Interval;
import uk.gov.phe.gis.thematics.classification.classifiers.Util;
import uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding.Rounder;

import java.util.ArrayList;

public class ManualInterval extends uk.gov.phe.gis.thematics.classification.classifiers.ManualInterval {

    private final Rounder rounder;

    public ManualInterval(double min, double[] breaks, double max, Rounder rounder) {
        super(min, rounder.round(breaks, new double[]{min,max}), max);
        this.rounder = rounder;
    }

    @Override
    public ArrayList<Interval> getIntervals() {
        return Util.roundMaxMin(super.getIntervals(), rounder);
    }
}
