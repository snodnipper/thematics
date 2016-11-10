/*
 * This code has been adapted from GeoTools.
 * The license is shown below.
 *
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package uk.gov.phe.gis.thematics.classification.classifiers.analysis;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NaturalInterval {

    private NaturalInterval(){}

    /**
     * Note: the natural jenks breaks might be different to 'natural
     * breaks' as classes can be zero width in this classification scheme.
     * @param classes number of classes to divide the data into
     * @param values the data values to classify
     * @return natural jenks class breaks
     */
    public static double[] breaks(int classes, double[] values) {
        // no data then no breaks
        if (values.length == 0) {
            return new double[]{};
        }

        // cannot have more classes than unique values in Natural Jenks
        Double[] list = ArrayUtils.toObject(values);
        Set<Double> set = new HashSet(Arrays.asList(list));

        if (classes > set.size()) {
            classes = set.size(); // The UI 'classes' input should NOT be updated as this is CORRECT.
        }

        return naturalIntervals(classes, values);
    }

    /**
     * Source based on:
     * https://github.com/geotools/geotools/blob/master/modules/library/main/src/main/java/org/geotools/filter/function/JenksNaturalBreaksFunction.java
     */
    private static double[] naturalIntervals(int numclass, double[] data) {
        Arrays.sort(data);
        //int numclass;
        int numdata = data.length;
        double[][] mat1 = new double[numdata + 1][numclass + 1];
        double[][] mat2 = new double[numdata + 1][numclass + 1];

        for (int i = 1; i <= numclass; i++) {
            mat1[1][i] = 1;
            mat2[1][i] = 0;
            for (int j = 2; j <= numdata; j++) {
                mat2[j][i] = Double.MAX_VALUE;
            }
        }

        double ssd = 0;
        for (int rangeEnd = 2; rangeEnd <= numdata; rangeEnd++) {
            double sumX = 0;
            double sumX2 = 0;
            double w = 0;
            int dataId;
            for (int m = 1; m <= rangeEnd; m++) {
                dataId = rangeEnd - m + 1;

                double val = data[dataId - 1];
                sumX2 += val * val;
                sumX += val;
                w++;
                ssd = sumX2 - (sumX * sumX) / w;

                for (int j = 2; j <= numclass; j++) {
                    if (!(mat2[rangeEnd][j] < (ssd + mat2[dataId - 1][j - 1]))) {
                        mat1[rangeEnd][j] = dataId;
                        mat2[rangeEnd][j] = ssd + mat2[dataId - 1][j - 1];
                    }
                }
            }
            mat1[rangeEnd][1] = 1;
            mat2[rangeEnd][1] = ssd;
        }
        double[] kbreaks = new double[numclass - 1];
        int k = numdata;
        for (int j = numclass; j >= 2; j--) {
            int id = (int)(mat1[k][j]) - 2;
            kbreaks[j - 2] = data[id];
            k = (int)mat1[k][j] - 1;
        }
        return kbreaks;
    }
}
