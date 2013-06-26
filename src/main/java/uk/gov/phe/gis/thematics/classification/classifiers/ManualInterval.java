/*
 * Copyright (c) 2013 Public Health England
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers;

public class ManualInterval extends AbstractInterval {

    private double[] breaks;

    public ManualInterval(double min, double[] breaks, double max) {
        super(min, max);
        this.breaks = breaks;
    }

    public double[] getBreaks() {
        return breaks;
    }

    public int getClassNumber(double value) {
        checkRange(value);

        int classNo = 1; // classes start at 1
        for (Double b : breaks) {
            if (value <= b) {
                return classNo;
            }
            classNo++;
        }
        return classNo; // it must be the last class
    }
}
