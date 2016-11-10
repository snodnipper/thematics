/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification.classifiers.analysis;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import uk.gov.phe.gis.thematics.classification.classifiers.IntervalTest;

public class NaturalIntervalTest extends IntervalTest {

    private static Logger sLogger = LogManager.getLogger(NaturalIntervalTest.class);

    @Test
    public void naturalJenks() {
        // subtest 1 - even number of data items
        double[] data = {0, 10, 19, 20, 40, 50, 60, 70, 80, 90, 100, 101};
        int classes = 5;

        double[] actual = NaturalInterval.breaks(classes, data);
        double[] expected = new double[]{10d, 20d, 50d, 80d};
        checkBreaks(expected, actual);

        // subtest 2
        data = new double[]{1d, 2d, 3d};
        classes = 3;
        expected = new double[]{1d, 2d};
        actual = NaturalInterval.breaks(classes, data);
        checkBreaks(expected, actual);

        // subtest 3 - fails in QGIS & ESRI.  QGIS has -3 to -1 as the first value (weird!), ESRI have rounding
        // inconsistencies.
        data = new double[]{-1d, -2d, -3d};
        classes = 3;
        expected = new double[]{-3d, -2d};
        actual = NaturalInterval.breaks(classes, data);
        checkBreaks(expected, actual);

        // subtest 4 - test returned classes is two (breaks one) when specifying three.
        data = new double[]{1d, 3d};
        classes = 3;
        expected = new double[]{1d};
        actual = NaturalInterval.breaks(classes, data);
        checkBreaks(expected, actual);

        // subtest 5 - odd number of data items
        data = new double[]{1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d, 10d, 11d};
        classes = 3;
        expected = new double[]{3d, 7d};
        actual = NaturalInterval.breaks(classes, data);
        checkBreaks(expected, actual);

        // subtest 6 - no data, expect no breaks
        data = new double[]{};
        classes = 3;
        expected = new double[]{};
        actual = NaturalInterval.breaks(classes, data);
        checkBreaks(expected, actual);

        //subtest 7 - three data items _but_ only two classes possible because two are the same!
        data = new double[]{3d, 3d, 4d};
        classes = 3;
        actual = NaturalInterval.breaks(classes, data);
        expected = actual;
        checkBreaks(expected, actual);
    }

    @Override
    protected Logger getLogger() {
        return sLogger;
    }
}
