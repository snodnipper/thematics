/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification;

public class Interval {

    private final double lowerBound;
    private final double upperBound;

    private volatile int hashCode;

    public Interval(double lowerBound, double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Interval))
            return false;
        Interval interval = (Interval)o;
        return interval.getLowerBound() == lowerBound && interval.getUpperBound() == upperBound;
    }

    /**
     * Item 9, Effective Java by Bloch.
     */
    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = 17;
            result = 31 * result + intHash(lowerBound);
            result = 31 * result + intHash(upperBound);
            hashCode = result;
        }
        return result;
    }

    @Override
    public String toString() {
        return lowerBound + " - " + upperBound;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    /**
     * Field hash
     */
    private int intHash(double d) {
        long l = Double.doubleToLongBits(lowerBound);
        return (int)(l ^ (l >>> 32));
    }
}
