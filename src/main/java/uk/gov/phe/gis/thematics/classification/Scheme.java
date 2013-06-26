/* The naturalIntervals method uses code from geoTools which has been adapted.
 * The following is the acknowledgement to their code.
 *
 * Licensed under LGPL v. 2.1 or any later version;
 * see GNU LGPL for details.
 * Authors: Hisaji ONO, James Macgill, Diansheng Guo, Frank Hardisty
 *
 * Hisaji ONO hi_ono2001 at ybb.ne.jp Fri Mar 3 18:04:05 CET 2006 As you
 * might know, most of Jenks' optimization method depends on Fischer's
 * "EXACT OPTIMIZATION" METHOD in (Fisher, W. D., 1958, On grouping for
 * maximum homogeneity. Journal of the American Statistical Association, 53,
 * 789, 98)
 *
 * This source code is available from following CMU's statlib site in
 * fortran code.
 *
 * http://lib.stat.cmu.edu/cmlib/src/cluster/fish.f
 *
 * Jenks' one is available in following paper media. Probably its in Basic.
 *
 * Jenks, G. F. (1977). Optimal data classification for choropleth maps,
 * Occasional paper No. 2. Lawrence, Kansas: University of Kansas,
 * Department of Geography.
 *
 * I've ported above code into
 * Geotools-lite(http://sourceforge.net/project/showfiles.php?group_id=4091)
 * in Java by modified by J. Macgill.
 *
 * Most of Jenks'
 * code(uk.ac.leeds.ccg.geotools.classification.NaturalBreaks.java) as
 * follows.
 *
 */

package uk.gov.phe.gis.thematics.classification;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.*;

public final class Scheme {

    private static Logger sLogger = LogManager.getLogger(Scheme.class);

    private Scheme() {
    }

    /**Returns Equal class intervals for given data set
     * @param noClasses
     * @param data
     * @return
     */
    public static double[] equalIntervals(int noClasses, double[] data) {
        final double range = range(data);
        final double classWidth = range / noClasses;
        final double minValue = minValue(data);
        final double[] classIntervals = getClassIntervals(classWidth, noClasses, minValue);
        //for(int i=0;i<classIntervals.length;i++)
        //sLogger.debug(classIntervals[i]);
        return classIntervals;
    }


    /** Returns Defined class inverals
     * @param classWidth
     * @param data
     * @return
     */
    public static double[] definedIntervals(double classWidth, double[] data) {
        final double range = range(data);
        final int noClasses = (int)Math.ceil(range / classWidth);
        final double minValue = minValue(data);
        final double[] classIntervals = getClassIntervals(classWidth, noClasses, minValue);
        return classIntervals;
    }


    /** Returns Natural jenks class intervals.
     * @param numclass
     * @param data
     * @return
     */
    public static double[] naturalIntervals(int numclass, double[] data) {
        Arrays.sort(data);
        //int numclass;
        int numdata = data.length;
        if (numclass >
            numdata) //in this resort to data lenght since it won't work otherwise coz no classes should be less than or equal to data length otherwise why classify
            numclass = numdata;

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
        double[] kbreaks = new double[numclass];
        int k = numdata;
        for (int j = numclass; j >= 2; j--) {
            int id = (int)(mat1[k][j]) - 2;
            kbreaks[j - 2] = data[id];
            k = (int)mat1[k][j] - 1;
        }
        kbreaks[numclass - 1] = data[numdata - 1]; // the last
        return kbreaks;
    }

    /** Classifies given data set based on given class intervals
     * @param breakPoints
     * @param data
     * @return
     */
    public static SortedMap<Double, List<Double>> classify(double[] breakPoints, double[] data) {
        SortedMap<Double, List<Double>> sortedMap = new TreeMap<Double, List<Double>>();
        double nextbreakPoint = Double.MAX_VALUE;
        DecimalFormat twoDForm = new DecimalFormat("#.##"); //format to two decimal places
        for (int i = 0; i < data.length; i++) {
            double value = data[i];
            for (int j = 0; j < breakPoints.length; j++) {
                double breakPoint = breakPoints[j];
                breakPoint = Double.valueOf(twoDForm.format(breakPoint)); //format to two decimal places
                //sLogger.debug(breakPoint);
                if (j + 1 < breakPoints.length) {
                    nextbreakPoint = breakPoints[j + 1];
                    nextbreakPoint = Double.valueOf(twoDForm.format(nextbreakPoint)); //format to two decimal places
                } //all classes are mutaully exclusive
                //check if in the first class range
                if (j == 0 && value <= breakPoint) {

                    List<Double> list = getList(breakPoint, sortedMap);
                    list.add(value);
                }
                //check if in middle
                else if (breakPoint < value && value <= nextbreakPoint) {
                    List<Double> list = getList(nextbreakPoint, sortedMap);
                    list.add(value);
                }
                //else if no value in the break point record null
                else {
                    List<Double> list = getList(nextbreakPoint, sortedMap);
                }
            }
        }

        Set<Double> bands = sortedMap.keySet();
        for (Double band : bands) {
            sLogger.debug("=< " + band);
            List<Double> values = sortedMap.get(band);
            for (Double value : values) {
                sLogger.debug("\tValue: " + value);
            }
        }

        return sortedMap;
    }

    /**Runs all classification techniques and returns break point from the most optimal one
     * @param noClasses
     * @param classWidth
     * @param data
     * @return
     */
    public static double[] optimalBreakPoints(int noClasses, double classWidth, double[] data) {

        double min_val = Double.MAX_VALUE;
        double optimalbreakPoints[] = null;
        String bestMethod = null;

        //check equal interval
        double[] breakPoints = equalIntervals(noClasses, data);
        SortedMap<Double, List<Double>> result = classify(breakPoints, data);
        double diff = getClassifiedDifference(result, data);
        if (diff < min_val) {
            min_val = diff;
            optimalbreakPoints = breakPoints;
            bestMethod = "Equal Interval";
        }

        breakPoints = definedIntervals(classWidth, data);
        result = classify(breakPoints, data);
        diff = getClassifiedDifference(result, data);
        if (diff < min_val) {
            min_val = diff;
            optimalbreakPoints = breakPoints;
            bestMethod = "Defined Interval";
        }

        breakPoints = naturalIntervals(noClasses, data);
        result = classify(breakPoints, data);
        diff = getClassifiedDifference(result, data);
        if (diff < min_val) {
            min_val = diff;
            optimalbreakPoints = breakPoints;
            bestMethod = "Natural Interval";
        }
        sLogger.debug(bestMethod);
        return optimalbreakPoints;

    }

    /**Calculates the the difference between the classified data (already classified by a method equal,defined, natural etc) and the unclassified data.
     * @param classifiedData
     * @param data
     * @return
     */
    private static double getClassifiedDifference(SortedMap<Double, List<Double>> classifiedData, double data[]) {
        Arrays.sort(data);
        //first calcualte average values in each class then store in an array
        Set<Double> intervals = classifiedData.keySet();
        int last_count = 0;
        double classAverageVals[] = new double[data.length];
        for (Double interval : intervals) {
            //get all data for this interval
            double total_value = 0;
            int count = 0;
            List<Double> values = classifiedData.get(interval);
            for (Double value : values) {
                total_value = total_value + value;
                count++;
            }
            double average = total_value / count;
            int k = 0;
            for (k = last_count; k < last_count + count; k++) {

                classAverageVals[k] = average;
            }
            last_count = k;
        }

        //calculate difference between average of each class and actual data values for that class
        double difference[] = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            double data_val = data[i];
            double avgVal = classAverageVals[i];
            difference[i] = Math.abs(data_val - avgVal);
            //sLogger.debug(difference[i]);
        }

        //calculate overall Difference
        double result = 0;
        for (int i = 0; i < difference.length; i++)
            result += difference[i];

        return result;
    }


    private static double[] getClassIntervals(double classWidth, int noClasses, double minValue) {
        double classIntervals[] = new double[noClasses];
        //DecimalFormat twoDForm = new DecimalFormat("#.##");
        for (int i = 0; i < noClasses; i++) {
            double d = minValue + classWidth;
            //d = Double.valueOf(twoDForm.format(d));
            classIntervals[i] = d;
            minValue = classIntervals[i];
        }
        return classIntervals;

    }

    private static List<Double> getList(double threshold, SortedMap<Double, List<Double>> sortedMap) {
        List<Double> list = sortedMap.get(threshold);
        if (list == null) {
            list = new ArrayList<Double>();
            sortedMap.put(threshold, list);
        }
        return list;
    }

    private static double maxValue(double[] data) {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }

    private static double minValue(double[] data) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < min) {
                min = data[i];
            }
        }
        return min;
    }

    private static double range(double[] data) {
        return maxValue(data) - minValue(data);
    }


    public static void main(String args[]) {
        final double[] data = { 10,11,13,15,260,260,450,457,599,1000 };


        //test data from shapefile - C:\Omair\PythonImplementation\Classifier
        //  {123.0,93.0,161.0,90.0,115.0,101.0,136.0,200.0,211.0,133.0,157.0,78.0,160.0,107.0,56.0,147.0,191.0,133.0,135.0,199.0,143.0,202.0,117.0,73.0,133.0,103.0,85.0,206.0,156.0,235.0,113.0,101.0,112.0,133.0,119.0,94.0,108.0,125.0,143.0,207.0,152.0,166.0,126.0,91.0};
        //test data from shapefile - C:\Omair\PythonImplementation\Classifier
        // {15304.0293,11634.9651999,30229.14365,21218.8880503,16549.9031497,17032.804929,18033.6287468,24036.1035999,23864.4296001,18648.4977998,11064.54375,5878.55159992,55274.4245503,22850.2766534,18112.0019001,18443.0510031,40040.9779375,12403.4912002,27200.8618433,15877.5257936,22548.9531854,43681.3238346,20381.0199502,5005.95370016,20900.96925,74634.1563012,15062.985,51094.8032549,22942.7394933,29736.1396623,18671.0954624,25973.9539499,18561.97255,19946.6813126,14099.9195501,12727.5337502,14450.5982999,20846.5321996,24824.592469,36296.1123998,26358.4972498,37501.1137998,17992.5416,13865.51955};
        //{7185.00000000,163944.00000000,314564.00000000,218307.00000000,263464.00000000,295532.00000000,198020.00000000,330587.00000000,300948.00000000,273559.00000000,214403.00000000,202824.00000000,165242.00000000,216507.00000000,206814.00000000,224248.00000000,243006.00000000,212341.00000000,175797.00000000,158919.00000000,147273.00000000,266169.00000000,248922.00000000,187908.00000000,243891.00000000,238635.00000000,172335.00000000,244866.00000000,179768.00000000,196106.00000000,218341.00000000,260380.00000000,181286.00000000,261037.00000000,180608.00000000,392819.00000000,217273.00000000,205357.00000000,216103.00000000,284528.00000000,213043.00000000,210145.00000000,301415.00000000,150459.00000000,439473.00000000,176843.00000000,282958.00000000,312293.00000000,218063.00000000,286866.00000000,248175.00000000,513234.00000000,191151.00000000,259536.00000000,191659.00000000,152785.00000000,280807.00000000,977087.00000000,300848.00000000,305155.00000000,282904.00000000,199517.00000000,253499.00000000,236582.00000000,467665.00000000,192405.00000000,388567.00000000,715402.00000000,315172.00000000,88611.00000000,134855.00000000,139132.00000000,178408.00000000,97838.00000000,493470.00000000,307190.00000000,351817.00000000,118208.00000000,191080.00000000,321971.00000000,137470.00000000,142283.00000000,243589.00000000,314113.00000000,157979.00000000,152849.00000000,181094.00000000,221708.00000000,279921.00000000,34563.00000000,266988.00000000,174871.00000000,158325.00000000,283173.00000000,240636.00000000,169040.00000000,380615.00000000,188564.00000000,245641.00000000,499114.00000000,2153.00000000,240720.00000000,129706.00000000,163444.00000000,138288.00000000,180051.00000000,432973.00000000,156061.00000000,184371.00000000,147911.00000000,233661.00000000,160257.00000000,143128.00000000,249488.00000000,109617.00000000,144483.00000000,143096.00000000,119067.00000000,133626.00000000,150229.00000000,207057.00000000,247817.00000000,186701.00000000,217445.00000000,132731.00000000,66829.00000000,116843.00000000,109596.00000000,93065.00000000,148594.00000000,128476.00000000,126354.00000000,74941.00000000,114131.00000000,172842.00000000,223301.00000000,134468.00000000,128645.00000000,119292.00000000,231946.00000000,55981.00000000,169519.00000000,70064.00000000,90949.00000000,84885.00000000,137011.00000000,305353.00000000,165748.00000000,89228.00000000,61945.00000000,162105.00000000,108863.00000000,73214.00000000,83519.00000000,156954.00000000,130108.00000000,93492.00000000,71980.00000000,100739.00000000,69318.00000000,49777.00000000,102301.00000000,116471.00000000,71766.00000000,98845.00000000,69469.00000000,110099.00000000,89433.00000000,96940.00000000,81562.00000000,125520.00000000,111076.00000000,69774.00000000,87508.00000000,81849.00000000,120958.00000000,58965.00000000,48843.00000000,44865.00000000,83786.00000000,61905.00000000,44416.00000000,92360.00000000,63648.00000000,89667.00000000,85029.00000000,92177.00000000,85428.00000000,76468.00000000,111387.00000000,107713.00000000,107570.00000000,111787.00000000,98181.00000000,106273.00000000,105599.00000000,131785.00000000,134248.00000000,128188.00000000,115627.00000000,95640.00000000,103869.00000000,105881.00000000,150969.00000000,102299.00000000,35075.00000000,92126.00000000,103770.00000000,93232.00000000,122030.00000000,105896.00000000,120670.00000000,94489.00000000,74531.00000000,83461.00000000,55510.00000000,117069.00000000,86837.00000000,98193.00000000,115141.00000000,112342.00000000,121936.00000000,67059.00000000,129701.00000000,80287.00000000,126523.00000000,78033.00000000,90390.00000000,80314.00000000,79267.00000000,115665.00000000,89840.00000000,61860.00000000,119132.00000000,87453.00000000,111484.00000000,125931.00000000,59627.00000000,140759.00000000,106450.00000000,99744.00000000,122088.00000000,127378.00000000,97568.00000000,87837.00000000,72172.00000000,78807.00000000,93353.00000000,112957.00000000,96981.00000000,140023.00000000,165668.00000000,132179.00000000,68456.00000000,86608.00000000,157072.00000000,155796.00000000,120896.00000000,78768.00000000,59418.00000000,78489.00000000,138539.00000000,68946.00000000,110013.00000000,80376.00000000,79982.00000000,109885.00000000,107898.00000000,76405.00000000,152573.00000000,109274.00000000,116169.00000000,107977.00000000,76415.00000000,83505.00000000,116849.00000000,169331.00000000,90987.00000000,109801.00000000,107222.00000000,87054.00000000,137799.00000000,128919.00000000,94450.00000000,116908.00000000,129005.00000000,79715.00000000,82848.00000000,79726.00000000,97553.00000000,102661.00000000,135278.00000000,85911.00000000,104566.00000000,95717.00000000,138948.00000000,109305.00000000,96238.00000000,122801.00000000,126702.00000000,107561.00000000,104030.00000000,89542.00000000,100449.00000000,73217.00000000,81496.00000000,133914.00000000,89248.00000000,129633.00000000,53960.00000000,65652.00000000,103867.00000000,108378.00000000,105618.00000000,90252.00000000,153462.00000000,76559.00000000,100141.00000000,47866.00000000,85503.00000000,55795.00000000,55750.00000000,130447.00000000,85595.00000000,94024.00000000,76522.00000000,124792.00000000,79515.00000000,121418.00000000,118513.00000000,90810.00000000,135345.00000000,98382.00000000,121550.00000000,110710.00000000,53174.00000000,71838.00000000,76550.00000000,81844.00000000,194458.00000000,79293.00000000,72519.00000000,53620.00000000,84111.00000000,151336.00000000,47010.00000000,50872.00000000,106243.00000000};
        //Arrays.sort(data);
        final int noClasses = 3;
        final double classWidth = 300000;

        double[] classIntervals = equalIntervals(noClasses, data);
        SortedMap<Double, List<Double>> result = classify(classIntervals, data);
        sLogger.debug("Equal Interval " + getClassifiedDifference(result, data) + "\n");

        classIntervals = definedIntervals(classWidth, data);
        result = classify(classIntervals, data);
        sLogger.debug("Defined Interval " + getClassifiedDifference(result, data) + "\n");

        classIntervals = naturalIntervals(noClasses, data);

        result = classify(classIntervals, data);
        sLogger.debug("Natural Jenks " + getClassifiedDifference(result, data) + "\n");

        //manual classification
        /*double [] classIntervals = {30,50,70,90,150};
        classify(classIntervals, data);*/

        //optimalBreakPoints(noClasses, classWidth, data);

    }


}
