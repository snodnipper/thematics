/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */
package uk.gov.phe.gis.thematics.classification.classifiers;

public class EqualInterval extends AbstractInterval {

    protected final int classes;
    protected final double interval;

    public EqualInterval(double min, int classes, double max) {
        super(min, max);
        this.classes = classes;
        double range = max - min;
        interval = range / classes;  // interval = range / classes, e.g. 100 = 1000 / 10
    }

    public double[] getBreaks() {
        double[] breaks = new double[classes - 1];

        // This is essentially a very simple arithmetic series algorithm.
        //  - the difference, d, in this case is called the interval.
        //  a[n] = a[1] + (n - 1) d
        for (int i = 1; i < classes; i++) {
            breaks[i - 1] = min + (i * interval);
        }
        return breaks;
    }

    /**
     * Calculate the class number
     *
     * <h2>Overview</h2>
     * The arithmetic series formula is useful for this as the
     * relationship between class and term is: 'class = term - 1', with
     * the exception of the first term, which is included in the first
     * class.
     *
     * Arithmetic Series:
     *   a<sub>n</sub> = a<sub>1</sub> + (n - 1) d
     *   or:
     *   a[n] = a[1] + (n - 1) d
     *
     * where 'n' is the required sequence term and 'd' is the common difference.
     *
     * Example values and terms:
     *    Value: 5, 10, 15, 20
     *    Term:  1,  2,  3,  4.
     *
     * <h2>Rearrange to find 'n'</h2>
     * We can rearrange the formula to find 'n':
     * n = ((a[n] - a[1]) / d) + 1
     *
     * Assuming: 'class = term - 1' for all values greater than 1
     * class + 1 = term
     * ∴ class + 1 = ((a[n] - a[1]) / d) + 1
     * ∴ class = ((a[n] - a[1]) / d)
     *
     * To handle the exception of class == 0 for the first term, which
     * is an 'inclusive exception' and should be 1, it is possible to
     * simply define class using the max function:
     *
     * class = max(n - 1, 1);
     *
     * <h2>Support values between terms</h2>
     * Because we must support values not in the arithmetic series
     * we define the sequence term 'n' with the ceiling function:
     *    n = ⌈(a[n] - a[1] / d) + 1⌉
     *    n = ceiling( (a[n] - a[1] / d) + 1 )
     *
     * Ceiling essentially is equivalent to snapping a given value
     * to the nearest term up (when not a term itself).
     *
     * Thus:
     * class = max(⌈(a[n] - a[1] / d)⌉, 1);
     *
     * In verbose code the above could be expressed as:
     *    double firstTerm = min;
     *    int term = (int) Math.ceil((value - firstTerm) / interval) + 1;
     *    if (term == 1) {
     *       return 1;
     *    } else {
     *       return term - 1;
     *    }
     *
     * @param value
     * @return the class for the given value
     */
    public int getClassNumber(double value) {
        checkRange(value);

        double firstTerm = min;
        return Math.max(
                (int)(Math.ceil((value - firstTerm) / interval)), 1);
    }
}
