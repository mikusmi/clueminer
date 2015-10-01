/*
 * Copyright (C) 2011-2015 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.algorithm.cure;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class Cure<E extends Instance, C extends Cluster<E>> extends AbstractClusteringAlgorithm<E, C> implements ClusteringAlgorithm<E, C> {

    private int totalNumberOfPoints;
    private int numberOfClusters;
    private int minRepresentativeCount;
    private double shrinkFactor;
    private double requiredRepresentationProbablity;
    private int numberOfPartitions;
    private int reducingFactorForEachPartition;
    private ArrayList<E> outliers;
    private HashMap dataPointsMap;
    private static int currentRepAdditionCount;
    private HashSet<Integer> integerTable;

    public static final String name = "CURE";

    public Cure() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        initializeParameters(props);

        long beginTime = System.currentTimeMillis();

        int sampleSize = calculateSampleSize();
        ArrayList<E> randomPointSet = selectRandomPoints(dataset, sampleSize);
        ArrayList[] partitionedPointSet = partitionPointSet(randomPointSet);
        ArrayList subClusters = clusterSubPartitions(partitionedPointSet);
        if (reducingFactorForEachPartition >= 10) {
            eliminateOutliersFirstStage(subClusters, 1);
        } else {
            eliminateOutliersFirstStage(subClusters, 0);
        }
        ArrayList clusters = clusterAll(subClusters);
        clusters = labelRemainingDataPoints(clusters);

        long time = System.currentTimeMillis() - beginTime;

        System.out.println("The Algorithm took " + time + " milliseconds to complete.");

        showClusters(clusters);

        return null;
    }

    /**
     * Initializes the Parameters
     *
     * @param args The Command Line Argument
     */
    private void initializeParameters(Props props) {
        totalNumberOfPoints = Integer.parseInt(args[2]);
        numberOfClusters = Integer.parseInt(args[3]);
        minRepresentativeCount = Integer.parseInt(args[4]);
        shrinkFactor = Double.parseDouble(args[5]);
        requiredRepresentationProbablity = Double.parseDouble(args[6]);
        numberOfPartitions = Integer.parseInt(args[7]);
        reducingFactorForEachPartition = Integer.parseInt(args[8]);

        dataPointsMap = new HashMap();
        currentRepAdditionCount = totalNumberOfPoints;
        integerTable = new HashSet<>();
        outliers = new ArrayList();
    }

    /**
     * Calculates the Sample Size based on Chernoff Bounds Mentioned in the CURE
     * Algorithm
     *
     * @return
     * int The Sample Data Size to be worked on
     */
    private int calculateSampleSize() {
        return (int) ((0.5 * totalNumberOfPoints) + (numberOfClusters * Math.log10(1 / requiredRepresentationProbablity)) + (numberOfClusters * Math.sqrt(Math.pow(Math.log10(1 / requiredRepresentationProbablity), 2) + (totalNumberOfPoints / numberOfClusters) * Math.log10(1 / requiredRepresentationProbablity))));
    }

    /**
     * Select random points from the data set
     *
     * @param sampleSize The sample size selected
     * @return
     * ArrayList The Selected Random Points
     */
    private ArrayList selectRandomPoints(Dataset<E> dataset, int sampleSize) {
        ArrayList<E> randomPointSet = new ArrayList();
        Random random = new Random();
        for (int i = 0; i < sampleSize; i++) {
            int index = random.nextInt(totalNumberOfPoints);
            if (integerTable.contains(index)) {
                i--;
            } else {
                randomPointSet.add(dataset.get(index));
                integerTable.add(index);
            }
        }
        return randomPointSet;
    }

    /**
     * Partition the sampled data points to p partitions (p =
     * numberOfPartitions)
     *
     * @param pointSet Sample data point set
     * @return
     * ArrayList[] Data Set Partitioned Sets
     */
    private ArrayList[] partitionPointSet(ArrayList pointSet) {
        ArrayList partitionedSet[] = new ArrayList[numberOfPartitions];
        Iterator iter = pointSet.iterator();
        for (int i = 0; i < numberOfPartitions - 1; i++) {
            partitionedSet[i] = new ArrayList();
            int pointIndex = 0;
            while (pointIndex < pointSet.size() / numberOfPartitions) {
                partitionedSet[i].add(iter.next());
                pointIndex++;
            }
        }
        partitionedSet[numberOfPartitions - 1] = new ArrayList();
        while (iter.hasNext()) {
            partitionedSet[numberOfPartitions - 1].add(iter.next());
        }
        return partitionedSet;
    }

    /**
     * Cluster each partitioned set to n/pq clusters
     *
     * @param partitionedSet Data Point Set
     * @return
     * ArrayList Clusters formed
     */
    private ArrayList clusterSubPartitions(ArrayList partitionedSet[]) {
        ArrayList clusters = new ArrayList();
        int numberOfClusterInEachPartition = totalNumberOfPoints / (numberOfPartitions * reducingFactorForEachPartition);
        for (int i = 0; i < partitionedSet.length; i++) {
            ClusterSet clusterSet = new ClusterSet(partitionedSet[i], numberOfClusterInEachPartition, minRepresentativeCount, shrinkFactor, dataPointsMap);
            Cluster[] subClusters = clusterSet.getAllClusters();
            for (int j = 0; j < subClusters.length; j++) {
                clusters.add(subClusters[j]);
            }
        }
        return clusters;
    }

    /**
     * Eliminates outliers after pre-clustering
     *
     * @param clusters Clusters present
     * @param outlierEligibilityCount Min Threshold count for not being outlier
     * cluster
     */
    private void eliminateOutliersFirstStage(ArrayList clusters, int outlierEligibilityCount) {
        Iterator iter = clusters.iterator();
        ArrayList clustersForRemoval = new ArrayList();
        while (iter.hasNext()) {
            Cluster cluster = (Cluster) iter.next();
            if (cluster.size() <= outlierEligibilityCount) {
                updateOutlierSet(cluster);
                clustersForRemoval.add(cluster);
            }
        }
        while (!clustersForRemoval.isEmpty()) {
            Cluster c = (Cluster) clustersForRemoval.remove(0);
            clusters.remove(c);
        }
    }

    /**
     * Cluster all remaining clusters. Merge all clusters using CURE's
     * hierarchical clustering algorithm till specified number of clusters
     * remain.
     *
     * @param clusters Pre-clusters formed
     * @return
     * ArrayList Set of clusters
     */
    private ArrayList clusterAll(ArrayList clusters) {
        ClusterSet clusterSet = new ClusterSet(clusters, numberOfClusters, minRepresentativeCount, shrinkFactor, dataPointsMap, true);
        return clusterSet.mergeClusters();
    }

    /**
     * Assign all remaining data points which were not part of the sampled data
     * set to set of clusters formed
     *
     * @param clusters Set of clusters
     * @return
     * ArrayList Modified clusters
     */
    private ArrayList labelRemainingDataPoints(ArrayList clusters) {

        for (int index = 0; index < dataPoints.length; index++) {
            if (integerTable.containsKey(index)) {
                continue;
            }
            Point p = dataPoints[index];
            double smallestDistance = 1000000;
            int nearestClusterIndex = -1;
            for (int i = 0; i < clusters.size(); i++) {
                ArrayList rep = ((Cluster) clusters.get(i)).rep;
                for (int j = 0; j < rep.size(); j++) {
                    double distance = p.calcDistanceFromPoint((Point) rep.get(j));
                    if (distance < smallestDistance) {
                        smallestDistance = distance;
                        nearestClusterIndex = i;
                    }
                }
            }
            if (nearestClusterIndex != -1) {
                ((Cluster) clusters.get(nearestClusterIndex)).pointsInCluster.add(p);
            }
        }
        return clusters;
    }

    /**
     * Update the outlier set for the clusters which have been identified as
     * outliers
     *
     * @param cluster Outlier Cluster
     */
    private void updateOutlierSet(Cluster cluster) {
        ArrayList outlierPoints = cluster.getPointsInCluster();
        Iterator iter = outlierPoints.iterator();
        while (iter.hasNext()) {
            outliers.add(iter.next());
        }
    }

    private void debug(Exception e) {
        //e.printStackTrace(System.out);
    }

    /**
     * Gets the current representative count so that the new points added do not
     * conflict with older KD Tree indices
     *
     * @return
     * int Next representative count
     */
    public static int getCurrentRepCount() {
        return ++currentRepAdditionCount;
    }

    public void showClusters(ArrayList clusters) {
        for (int i = 0; i < numberOfClusters; i++) {
            Cluster cluster = (Cluster) clusters.get(i);
            logCluster(cluster, "cluster" + i);
        }
        logOutlier();
        logPlotScript(clusters.size());
    }

    private BufferedWriter getWriterHandle(String filename) {
        BufferedWriter out = null;
        try {
            FileWriter fw = new FileWriter(filename, true);
            out = new BufferedWriter(fw);
        } catch (Exception e) {
            debug(e);
        }
        return out;
    }

    private void closeWriterHandle(BufferedWriter out) {
        try {
            out.flush();
            out.close();
        } catch (Exception e) {
            debug(e);
        }
    }

    private void logCluster(Cluster cluster, String filename) {
        BufferedWriter out = getWriterHandle(filename);
        try {
            out.write("#\tX\tY\n");
            for (int j = 0; j < cluster.pointsInCluster.size(); j++) {
                Point p = (Point) cluster.pointsInCluster.get(j);
                out.write("\t" + p.x + "\t" + p.y + "\n");
            }
        } catch (Exception e) {
            debug(e);
        }
        closeWriterHandle(out);
    }

    private void logOutlier() {
        BufferedWriter out = getWriterHandle("outliers");
        try {
            out.write("#\tX\tY\n");
            for (int j = 0; j < outliers.size(); j++) {
                Point p = (Point) outliers.get(j);
                out.write("\t" + p.x + "\t" + p.y + "\n");
            }
        } catch (Exception e) {
            debug(e);
        }
        closeWriterHandle(out);
    }

    private void logPlotScript(int totalClusters) {
        BufferedWriter out = getWriterHandle("plotcure.txt");
        try {
            setPlotStyle(out);
            out.write("plot");
            for (int i = 0; i < totalClusters; i++) {
                out.write(" \"cluster" + i + "\",");
            }
            out.write(" \"outliers\"");
        } catch (Exception e) {
            debug(e);
        }
        closeWriterHandle(out);
    }

    private void setPlotStyle(BufferedWriter out) {
        try {
            out.write("reset\n");
            out.write("set size ratio 2\n");
            out.write("unset key\n");
            out.write("set title \"CURE\"\n");
        } catch (Exception e) {
            debug(e);
        }
    }

}