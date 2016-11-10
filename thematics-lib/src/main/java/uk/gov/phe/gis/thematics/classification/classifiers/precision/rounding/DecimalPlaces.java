/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers.precision.rounding;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalPlaces extends Generic implements Rounder {

    final int precision;

    /**
     * @param precision the number of decimal places to round to
     */
    public DecimalPlaces(int precision) {
        this.precision = precision;
    }

    public double round(double value) {
        // We must NOT construct a BigDecimal from a double as BigDecimal parses the binary values and will _not_
        // necessary store the intended value.  See: https://forums.oracle.com/forums/thread.jspa?threadID=2059572
        BigDecimal cleaned = new BigDecimal(String.valueOf(value)).setScale(precision, RoundingMode.HALF_UP);
        return cleaned.doubleValue();
    }

    @Override
    public double roundDown(double value) {
//        BigDecimal cleaned = new BigDecimal(value).setScale(precision, RoundingMode.DOWN);
        BigDecimal cleaned = new BigDecimal(String.valueOf(value)).setScale(precision, RoundingMode.FLOOR);
        return cleaned.doubleValue();
    }

    @Override
    public double roundUp(double value) {
//        BigDecimal cleaned = new BigDecimal(value).setScale(precision, RoundingMode.UP);
        BigDecimal cleaned = new BigDecimal(String.valueOf(value)).setScale(precision, RoundingMode.CEILING);
        return cleaned.doubleValue();
    }
}
