/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class SignificantFigures extends Generic implements Rounder {

    final int precision;

    /**
     * @param precision the number of significant figures to round to
     */
    public SignificantFigures(int precision) {
        this.precision = precision;
    }

    @Override
    public double round(double value) {
        BigDecimal cleaned = new BigDecimal(String.valueOf(value)).round(new MathContext(precision));
        return cleaned.doubleValue();
    }

    @Override
    public double roundDown(double value) {
        BigDecimal cleaned = new BigDecimal(String.valueOf(value)).round(new MathContext(precision, RoundingMode.FLOOR));
        return cleaned.doubleValue();
    }

    @Override
    public double roundUp(double value) {
        BigDecimal cleaned = new BigDecimal(String.valueOf(value)).round(new MathContext(precision, RoundingMode.CEILING));
        return cleaned.doubleValue();
    }
}
