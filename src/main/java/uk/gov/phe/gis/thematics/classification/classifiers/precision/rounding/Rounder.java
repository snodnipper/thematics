/*
 * Copyright (c) 2013 Public Health England
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding;

public interface Rounder {

    /**
     * @return a rounded value processed according to the rounding function
     */
    double round(double value);

    /**
     * @param values the values to round
     * @param excludes values to exclude from the returned set (e.g. rounded values that aren't required)
     * @return a set of rounded values, which may be fewer than the initial values.
     */
    double[] round(double[] values, double[] excludes);

    /**
     * Round up the measurement space, so precisely a ceiling rounding towards infinity
     */
    double roundUp(double value);

    /**
     * Round down the measurement space, so precisely a flooring rounding towards negative infinity
     */
    double roundDown(double value);
}
