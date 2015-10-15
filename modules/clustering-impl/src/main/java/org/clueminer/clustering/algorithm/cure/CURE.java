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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * CURE - CLustering Using REpresentatives
 *
 * @cite Guha, Sudipto, Rajeev Rastogi, and Kyuseok Shim. "CURE: an efficient
 * clustering algorithm for large databases." ACM SIGMOD Record. Vol. 27. No. 2.
 * ACM, 1998.
 *
 * @author deric
 * @param <E>
 * @param <C> CURE requires cluster structure with set of representatives
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class CURE<E extends Instance, C extends CureCluster<E>> extends AbstractClusteringAlgorithm<E, C> implements ClusteringAlgorithm<E, C> {

    /**
     * total number of points (instances) in the data set
     */
    private int n;
    /**
     * number of expected clusters
     */
    public static final String K = "k";
    @Param(name = K, description = "expected number of clusters", required = true, min = 2, max = 25)
    private int numberOfClusters;

    public static final String MIN_REPRESENTATIVES = "min_representatives";
    @Param(name = MIN_REPRESENTATIVES, description = "minimum number of representatives", required = false, min = 1, max = 1000)
    private int minRepresentativeCount;

    public static final String SHRINK_FACTOR = "shrink_factor";
    @Param(name = SHRINK_FACTOR, description = "shrink factor", required = false, min = 0.0, max = 1.0)
    private double shrinkFactor;

    public static final String REPRESENTATION_PROBABILITY = "representation_probablity";
    @Param(name = REPRESENTATION_PROBABILITY,
           description = "required representation probablity",
           min = 0.0, max = 1.0)
    private double representationProbablity;

    /**
     * For performance only. When clustering large datasets it is recommended to
     * split in so many parts, that each partition will fit into memory
     */
    public static final String NUM_PARTITIONS = "num_partitions";
    @Param(name = NUM_PARTITIONS, description = "number of partitions", min = 1, max = 1000)
    private int numberOfPartitions;

    public static final String REDUCE_FACTOR = "reduce_factor";
    @Param(name = REDUCE_FACTOR, description = "reduce factor for each partition", min = 1, max = 1000)
    private int reducingFactor;

    private static int currentRepAdditionCount;
    private HashSet<Integer> blacklist;

    public static final String name = "CURE";
    private static final Logger logger = Logger.getLogger(CURE.class.getName());

    public CURE() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        n = dataset.size();
        initializeParameters(props);
        ArrayList<E> outliers = new ArrayList<>();

        int sampleSize = calculateSampleSize();
        logger.log(Level.INFO, "using sample size {0}", sampleSize);
        Dataset<E> randomPointSet = selectRandomPoints(dataset, sampleSize);

        Dataset<E> partition;
        Clustering<E, C> clustering = new ClusterList<>(numberOfClusters);
        Iterator<E> iter = randomPointSet.iterator();
        for (int i = 0; i < numberOfPartitions; i++) {
            partition = new ArrayDataset<>(randomPointSet.size() / numberOfPartitions, dataset.attributeCount());
            partition.setAttributes(dataset.getAttributes());
            int pointIndex = 0;
            while (pointIndex < randomPointSet.size() / numberOfPartitions) {
                partition.add(iter.next());
                pointIndex++;
            }
            System.out.println("partition " + i + " size = " + partition.size());
            clusterPartition(partition, clustering, outliers);
        }
        if (iter.hasNext()) {
            partition = new ArrayDataset<>(randomPointSet.size() / numberOfPartitions, dataset.attributeCount());
            partition.setAttributes(dataset.getAttributes());
            while (iter.hasNext()) {
                partition.add(iter.next());
            }
            if (!partition.isEmpty()) {
                clusterPartition(partition, clustering, outliers);
            }
        }

        logger.log(Level.INFO, "left {0} outliers", outliers.size());

        labelRemainingDataPoints(dataset, clustering);

        return clustering;
    }

    private void clusterPartition(Dataset<E> partition, Clustering<E, C> clustering, ArrayList<E> outliers) {
        int numberOfClusterInEachPartition = n / (numberOfPartitions * reducingFactor);
        logger.log(Level.INFO, "clustering partititon, exp: {0}", numberOfClusterInEachPartition);
        ClusterSet<E, C> clusterSet = new ClusterSet(partition, numberOfClusterInEachPartition, minRepresentativeCount, shrinkFactor);
        C[] clusters = clusterSet.getAllClusters();
        if (reducingFactor >= 10) {
            eliminateOutliers(clusters, 1, clustering, outliers);
        } else {
            eliminateOutliers(clusters, 0, clustering, outliers);
        }
    }

    /**
     * Initializes the Parameters
     *
     * @param args The Command Line Argument
     */
    private void initializeParameters(Props props) {
        numberOfClusters = props.getInt(K);
        minRepresentativeCount = props.getInt(MIN_REPRESENTATIVES, 5);
        shrinkFactor = props.getDouble(SHRINK_FACTOR, 0.5);
        representationProbablity = props.getDouble(REPRESENTATION_PROBABILITY, 0.1);
        numberOfPartitions = props.getInt(NUM_PARTITIONS, 1);
        reducingFactor = props.getInt(REDUCE_FACTOR, 2);

        currentRepAdditionCount = n;
        blacklist = new HashSet<>();
    }

    /**
     * Calculates the Sample Size based on Chernoff Bounds Mentioned in the CURE
     * Algorithm
     *
     * @return int The Sample Data Size to be worked on
     */
    private int calculateSampleSize() {
        return (int) ((0.5 * n)
                + (numberOfClusters * Math.log10(1 / representationProbablity))
                + (numberOfClusters * Math.sqrt(Math.pow(Math.log10(1 / representationProbablity), 2)
                        + (n / numberOfClusters) * Math.log10(1 / representationProbablity))));
    }

    /**
     * Select random points from the data set
     *
     * @param sampleSize The sample size selected
     * @return ArrayList The Selected Random Points
     */
    private Dataset<E> selectRandomPoints(Dataset<E> dataset, int sampleSize) {
        if (dataset.size() == sampleSize) {
            return dataset;
        }
        Dataset<E> randomPointSet = new ArrayDataset<>(sampleSize, dataset.attributeCount());
        randomPointSet.setAttributes(dataset.getAttributes());
        Random random = new Random();
        for (int i = 0; i < sampleSize; i++) {
            int index = random.nextInt(n);
            if (blacklist.contains(index)) {
                i--;
            } else {
                randomPointSet.add(dataset.get(index));
                blacklist.add(index);
            }
        }
        return randomPointSet;
    }

    /**
     * Eliminates outliers after pre-clustering
     *
     * @param clusters Clusters present
     * @param outlierEligibilityCount Min Threshold count for not being outlier
     * cluster
     */
    private void eliminateOutliers(C[] clusters, int outlierEligibilityCount, Clustering<E, C> clustering, ArrayList<E> outliers) {
        logger.log(Level.INFO, "eliminating outliers in {0} clusters", clusters.length);
        for (C cluster : clusters) {
            if (cluster.size() > outlierEligibilityCount) {
                clustering.add(cluster);
            } else {
                outliers.addAll(cluster);
            }
        }
    }

    /**
     * Assign all remaining data points which were not part of the sampled data
     * set to set of clusters formed
     *
     * @param clusters Set of clusters
     * @return ArrayList Modified clusters
     */
    private Clustering<E, C> labelRemainingDataPoints(Dataset<E> dataset, Clustering<E, C> clusters) {
        for (int index = 0; index < dataset.size(); index++) {
            if (blacklist.contains(index)) {
                continue;
            }
            E inst = dataset.get(index);
            double smallestDistance = 1000000;
            int nearestClusterIndex = -1;
            for (int i = 0; i < clusters.size(); i++) {
                ArrayList<E> rep = clusters.get(i).rep;
                for (E other : rep) {
                    double distance = distanceFunction.measure(inst, other);
                    if (distance < smallestDistance) {
                        smallestDistance = distance;
                        nearestClusterIndex = i;
                    }
                }
            }
            if (nearestClusterIndex != -1) {
                clusters.get(nearestClusterIndex).add(inst);
            }
        }
        return clusters;
    }

    private void debug(Exception e) {
        //e.printStackTrace(System.out);
    }

    /**
     * Gets the current representative count so that the new points added do not
     * conflict with older KD Tree indices
     *
     * @return int Next representative count
     */
    public static int getCurrentRepCount() {
        return ++currentRepAdditionCount;
    }

}