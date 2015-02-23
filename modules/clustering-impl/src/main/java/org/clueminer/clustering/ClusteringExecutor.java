package org.clueminer.clustering;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.struct.DendrogramData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import org.clueminer.utils.Props;

/**
 * Executor should be responsible of converting dataset into appropriate input
 * (e.g. a dense matrix) and then joining the original inputs with appropriate
 * clustering result
 *
 * @deprecated use cached executor @link{ClusteringExecutorCached}
 * @author Tomas Barton
 */
@Deprecated
public class ClusteringExecutor extends AbstractExecutor implements Executor {

    private static final Logger logger = Logger.getLogger(ClusteringExecutor.class.getName());

    public ClusteringExecutor() {
        algorithm = new HAC();
    }

    @Override
    public HierarchicalResult hclustRows(Dataset<? extends Instance> dataset, Props params) {
        if (dataset == null || dataset.isEmpty()) {
            throw new NullPointerException("no data to process");
        }
        Matrix input = Scaler.standartize(dataset.arrayCopy(), params.get(AgglParams.STD, Scaler.NONE), params.getBoolean(AgglParams.LOG, false));
        //TODO: not very efficient
        Dataset<? extends Instance> inData = new ArrayDataset<>(input.getArray());
        params.putBoolean(AgglParams.CLUSTER_ROWS, true);
        logger.log(Level.FINE, "clustering {0}", params.toString());
        HierarchicalResult rowsResult = algorithm.hierarchy(inData, params);
        rowsResult.setInputData(input);
        CutoffStrategy strategy = getCutoffStrategy(params);
        double cut = rowsResult.findCutoff(strategy);
        logger.log(Level.FINE, "found cutoff {0} with strategy {1}", new Object[]{cut, strategy.getName()});
        return rowsResult;
    }

    @Override
    public HierarchicalResult hclustColumns(Dataset<? extends Instance> dataset, Props params) {
        if (dataset == null || dataset.isEmpty()) {
            throw new NullPointerException("no data to process");
        }
        Matrix input = Scaler.standartize(dataset.arrayCopy(), params.get(AgglParams.STD, Scaler.NONE), params.getBoolean(AgglParams.LOG, false));
        params.putBoolean(AgglParams.CLUSTER_ROWS, false);
        Dataset<? extends Instance> inData = new ArrayDataset<>(input.getArray());
        HierarchicalResult columnsResult = algorithm.hierarchy(inData, params);
        //CutoffStrategy strategy = getCutoffStrategy(params);
        //columnsResult.findCutoff(strategy);
        return columnsResult;
    }

    @Override
    public Clustering<Cluster> clusterRows(Dataset<? extends Instance> dataset, Props params) {
        HierarchicalResult rowsResult = hclustRows(dataset, params);
        DendrogramMapping mapping = new DendrogramData(dataset, rowsResult.getInputData(), rowsResult);

        Clustering clustering = rowsResult.getClustering();
        clustering.mergeParams(params);
        clustering.lookupAdd(mapping);
        return clustering;
    }

    /**
     * Cluster both - rows and columns
     *
     * @param dataset data to be clustered
     * @param params
     * @return
     */
    @Override
    public DendrogramMapping clusterAll(Dataset<? extends Instance> dataset, Props params) {
        HierarchicalResult rowsResult = hclustRows(dataset, params);
        HierarchicalResult columnsResult = hclustColumns(dataset, params);

        DendrogramMapping mapping = new DendrogramData(dataset, rowsResult.getInputData(), rowsResult, columnsResult);
        rowsResult.getClustering().lookupAdd(mapping);
        return mapping;
    }
}
