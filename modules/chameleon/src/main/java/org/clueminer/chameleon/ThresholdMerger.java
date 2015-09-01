package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.utils.Props;

/**
 * This class merges pairs of clusters exceeding given thresholds for relative
 * interconnectivity and closeness. Merging stops when there is no pair of
 * clusters exceeding the thresholds.
 *
 *
 * @author Tomas Bruna
 * @param <E>
 */
public class ThresholdMerger<E extends Instance> extends AbstractMerger<E> {

    private final double RICThreshold;
    private final double RCLThreshold;
    private boolean merged;
    private final RiRcSimilarity eval;
    private static final String name = "threshold merger";

    public ThresholdMerger(Graph g, Bisection bisection, double RICThreshold, double RCLThreshold) {
        super(g, bisection);
        this.RICThreshold = RICThreshold;
        this.RCLThreshold = RCLThreshold;
        eval = new RiRcSimilarity<>();
    }

    @Override
    public String getName() {
        return name;
    }

    public ArrayList<LinkedList<Node<E>>> merge(ArrayList<LinkedList<Node<E>>> clusterList) {
        ArrayList<LinkedList<Node<E>>> result = clusterList;
        merged = true;
        while (merged) {
            merged = false;
            result = singleMerge(result);
        }
        return result;
    }

    private ArrayList<LinkedList<Node<E>>> singleMerge(ArrayList<LinkedList<Node<E>>> clusterList) {
        createClusters(clusterList, bisection);
        computeExternalProperties();
        initiateClustersForMerging();

        for (int i = 0; i < clusterCount; i++) {
            double maxRIC = Double.NEGATIVE_INFINITY;
            int index = -1;
            for (int j = 0; j < clusterCount; j++) {
                if (i == j) {
                    continue;
                }

                double RIC = eval.getRIC(clusters.get(i), clusters.get(j));
                double RCL = eval.getRCL(clusters.get(i), clusters.get(j));
                if (RIC > RICThreshold && RCL > RCLThreshold && RIC > maxRIC) {
                    maxRIC = RIC;
                    index = j;
                }
            }
            if (index != -1) {
                merged = true;
                mergeTwoClusters(clusters.get(i), clusters.get(index));
            }
        }
        return getNewClusters();
    }

    /**
     * Prepares clusters for merging
     */
    public void initiateClustersForMerging() {
        for (int i = 0; i < clusterCount; i++) {
            clusters.get(i).offsprings = new LinkedList<>();
            clusters.get(i).offsprings.add(clusters.get(i));
            clusters.get(i).setParent(clusters.get(i));
        }
    }

    protected void mergeTwoClusters(GraphCluster<E> cluster1, GraphCluster<E> cluster2) {
        if (cluster1.getParent().getClusterId() == cluster2.getParent().getClusterId()) {
            return;
        }
        if (cluster1.getParent().offsprings.size() < cluster2.getParent().offsprings.size()) {
            GraphCluster temp = cluster1;
            cluster1 = cluster2;
            cluster2 = temp;
        }
        cluster1.getParent().offsprings.addAll(cluster2.getParent().offsprings);
        GraphCluster<E> parent = cluster2.getParent();
        for (GraphCluster<E> cluster : parent.offsprings) {
            cluster.setParent(cluster1.getParent());
        }
        parent.offsprings = null;
    }

    /**
     * Creates lists of nodes according to new clusters
     *
     * @return lists of nodes in clusters
     */
    public ArrayList<LinkedList<Node<E>>> getNewClusters() {
        ArrayList<LinkedList<Node<E>>> result = new ArrayList<>();
        for (int i = 0; i < clusterCount; i++) {
            if (clusters.get(i).offsprings != null) {
                LinkedList<Node<E>> list = new LinkedList<>();
                for (GraphCluster<E> cluster : clusters.get(i).offsprings) {
                    for (Node<E> node : cluster.getNodes()) {
                        list.add(node);
                    }
                }
                result.add(list);
            }
        }
        return result;
    }

    @Override
    public HierarchicalResult getHierarchy(ArrayList<LinkedList<Node<E>>> clusterList, Dataset<E> dataset, Props pref) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
