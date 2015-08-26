package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DClusterLeaf;
import org.clueminer.partitioning.api.Bisection;

/**
 *
 * @author Tomas Bruna
 */
public abstract class Merger {

    /**
     * Original, not partitioned graph.
     */
    protected Graph graph;

    protected Bisection bisection;

    /**
     * Assigns each node to cluster.
     */
    protected int nodeToCluster[];

    /**
     * Number of clusters.
     */
    protected int clusterCount;

    /**
     * If bigger than 1, algorithm gives a higher importance to the relative
     * closeness, otherwise, if lesser than 1, to interconnectivity.
     */
    protected double closenessPriority;

    /**
     * Clusters to merge.
     */
    protected ArrayList<Partition> clusters;

    protected SimilarityMeasure similarityMeasure;

    /**
     * Matrix containing external properties of every 2 clusters.
     */
    protected ArrayList<ArrayList<ExternalProperties>> clusterMatrix;

    public Merger(Graph g, Bisection bisection, double closenessPriority) {
        this.graph = g;
        this.bisection = bisection;
        this.closenessPriority = closenessPriority;
    }

    /**
     * Creates clusters from lists of nodes
     *
     * @param clusterList
     * @param bisection
     */
    protected void createClusters(ArrayList<LinkedList<Node>> clusterList, Bisection bisection) {
        clusterCount = clusterList.size();
        clusters = new ArrayList<>();
        int i = 0;
        for (LinkedList<Node> cluster : clusterList) {
            clusters.add(new Partition(cluster, graph, i, bisection));
            i++;
        }
        assignNodesToClusters(clusterList);
    }

    /**
     * Creates empty structure of external properties between every two
     * clusters.
     */
    protected void inititateClusterMatrix() {
        clusterMatrix = new ArrayList<>();
        for (int i = 0; i < clusterCount; i++) {
            clusterMatrix.add(new ArrayList<ExternalProperties>());
            for (int j = 0; j < i; j++) {
                clusterMatrix.get(i).add(new ExternalProperties());
            }
        }
    }

    /**
     * Assigns clusters to nodes according to list of clusters in each node.
     * Having clusters assigned to nodes can be advantageous in some cases
     *
     * @param clusterList
     */
    protected void assignNodesToClusters(ArrayList<LinkedList<Node>> clusterList) {
        nodeToCluster = new int[graph.getNodeCount()];
        int i = 0;
        for (LinkedList<Node> cluster : clusterList) {
            for (Node node : cluster) {
                nodeToCluster[graph.getIndex(node)] = i;
            }
            i++;
        }
    }

    /**
     * Computes external interconnectivity and closeness between every two
     * clusters. Computed values are stored in a triangular matrix.
     *
     * Goes through all edges and if the edge connects different clusters, the
     * external values are updated
     *
     */
    protected void computeExternalProperties() {
        inititateClusterMatrix();
        for (Edge edge : graph.getEdges()) {
            int firstClusterID = nodeToCluster[graph.getIndex(edge.getSource())];
            int secondClusterID = nodeToCluster[graph.getIndex(edge.getTarget())];
            if (firstClusterID != secondClusterID) {
                //Swap values if the first is bigger. Matrix is symmetric so only a half has to be filled.
                if (secondClusterID > firstClusterID) {
                    int temp = firstClusterID;
                    firstClusterID = secondClusterID;
                    secondClusterID = temp;
                }
                //Update the values
                ExternalProperties properties = clusterMatrix.get(firstClusterID).get(secondClusterID);
                properties.EIC += edge.getWeight();
                properties.counter++;
                properties.ECL = properties.EIC / properties.counter;
            }
        }
    }

    protected double getEIC(int firstClusterID, int secondClusterID) {
        if (secondClusterID > firstClusterID) {
            int temp = firstClusterID;
            firstClusterID = secondClusterID;
            secondClusterID = temp;
        }
        return clusterMatrix.get(firstClusterID).get(secondClusterID).EIC;
    }

    protected double getECL(int firstClusterID, int secondClusterID) {
        if (secondClusterID > firstClusterID) {
            int temp = firstClusterID;
            firstClusterID = secondClusterID;
            secondClusterID = temp;
        }
        return clusterMatrix.get(firstClusterID).get(secondClusterID).ECL;
    }

    protected class ExternalProperties {

        public double EIC, ECL;
        public int counter;

        public ExternalProperties() {
            EIC = ECL = counter = 0;
        }
    }

    protected double getRIC(int i, int j) {
        if (j > i) {
            int temp = i;
            i = j;
            j = temp;
        }
        return clusterMatrix.get(i).get(j).EIC / ((clusters.get(i).getIIC() + clusters.get(j).getIIC()) / 2);
    }

    protected double getRCL(int i, int j) {
        if (j > i) {
            int temp = i;
            i = j;
            j = temp;
        }
        double nc1 = clusters.get(i).getNodeCount();
        double nc2 = clusters.get(j).getNodeCount();
        return clusterMatrix.get(i).get(j).ECL / ((nc1 / (nc1 + nc2)) * clusters.get(i).getICL() + (nc2 / (nc1 + nc2)) * clusters.get(j).getICL());
    }

    protected double min(double a, double b) {
        if (a < b) {
            return a;
        }
        return b;
    }

    protected double max(double a, double b) {
        if (a > b) {
            return a;
        }
        return b;
    }

    /**
     * Creates tree leaves and fills them with nodes.
     *
     * @param clusterList Initial clusters
     * @return
     */
    protected DendroNode[] initiateTree(ArrayList<LinkedList<Node>> clusterList) {
        DendroNode[] nodes = new DendroNode[(2 * clusterList.size() - 1)];
        clusterCount = clusterList.size();
        for (int i = 0; i < clusterList.size(); i++) {
            nodes[i] = new DClusterLeaf(i, createInstanceList(clusterList.get(i)));
            nodes[i].setHeight(0);
            nodes[i].setLevel(0);
        }
        return nodes;
    }

    protected LinkedList<Instance> createInstanceList(LinkedList<Node> nodes) {
        LinkedList<Instance> out = new LinkedList<>();
        for (Node node : nodes) {
            out.add(node.getInstance());
        }
        return out;
    }
}
