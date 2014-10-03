package org.clueminer.clustering.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public interface AgglomerativeClustering extends ClusteringAlgorithm {

    /**
     * Run hierarchical clustering on dataset
     *
     * @param dataset
     * @param pref
     * @return
     */
    public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, Props pref);

    /**
     *
     * @param input
     * @param dataset
     * @param pref
     * @return
     */
    public HierarchicalResult hierarchy(Matrix input, Dataset<? extends Instance> dataset, Props pref);

    public HierarchicalResult hierarchy(Matrix matrix, Props props);

}