/*
 * Crown Copyright (c) 2013
 *
 * This file is licensed under the Open Government Licence.
 * http://www.nationalarchives.gov.uk/doc/open-government-licence
 */

package uk.gov.phe.gis.thematics.classification;

import com.google.gson.Gson;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class SchemeTest {

    private static Logger sLogger = LogManager.getLogger(SchemeTest.class);

    @Test
    public void equalInterval() {
        final double[] data = {0, 10, 19, 20, 40, 50, 60, 70, 80, 90, 100, 101};
        final int noClasses = 5;

        // Expected Result
        String json = "{20.2:[0,10,19,20],40.4:[40],60.6:[50,60],80.8:[70,80],101.0:[90,100,101]}";
        SortedMap<Double, List<Double>> expected = new Gson().fromJson(json, Banding.class);

        
        double[] classIntervals= Scheme.equalIntervals(noClasses, data);
        SortedMap<Double, List<Double>> actual=Scheme.classify(classIntervals,data);
        
        assertEquals(expected, actual);

        // Debugging
       printResult(actual);
    }

    @Test
    public void definedIntervals() {
        double[] data = {0, 10, 19, 20, 40, 50, 60, 70, 80, 90, 100, 101};
        double classWidth = 30;

        String json = "{30.0:[0,10,19,20],60.0:[40,50,60],90.0:[70,80,90],120.0:[100,101]}";
        SortedMap<Double, List<Double>> expected = new Gson().fromJson(json, Banding.class);

        double[] classIntervals= Scheme.definedIntervals(classWidth, data);
        SortedMap<Double, List<Double>> actual=Scheme.classify(classIntervals,data);
        assertEquals(expected, actual);

        // Debugging
        printResult(actual);

    }
    
    @Test
    public void naturalJenksInterval() {
        final double[] data = {0, 10, 19, 20, 40, 50, 60, 70, 80, 90, 100, 101};
        final int noClasses = 5;

        // Expected Result
        String json = "{10.0:[0,10],20.0:[19,20],50.0:[40,50],80.0:[60,70,80],101.0:[90,100,101]}";
        SortedMap<Double, List<Double>> expected = new Gson().fromJson(json, Banding.class);

        double[] classIntervals= Scheme.naturalIntervals(noClasses, data);
        SortedMap<Double, List<Double>> actual=Scheme.classify(classIntervals,data);
        assertEquals(expected, actual);

        // Debugging
       printResult(actual);
    }
    
    @Test
    public void manualInterval() {
        final double[] data = {0, 10, 19, 20, 40, 50, 60, 70, 80, 90, 100, 101};
        final double [] classIntervals = {30,50,70,90,150};

        // Expected Result
        String json = "{30.0:[0,10,19,20],50.0:[40,50],70.0:[60,70],90.0:[80,90],150.0:[100,101]}";
        SortedMap<Double, List<Double>> expected = new Gson().fromJson(json, Banding.class);

        SortedMap<Double, List<Double>> actual=Scheme.classify(classIntervals,data);
        assertEquals(expected, actual);

        // Debugging
       printResult(actual);
    }

    private static void printResult(SortedMap<Double, List<Double>> bandedItems) {
        Set<Double> bands = bandedItems.keySet();
        for (Double band : bands) {
            sLogger.debug("##############");
            sLogger.debug("Band: " + band);
            List<Double> values = bandedItems.get(band);
            for (Double value : values) {
                sLogger.debug("Value: " + value);
            }
        }
    }

    public static class Banding extends TreeMap<Double, List<Double>> { }
}
