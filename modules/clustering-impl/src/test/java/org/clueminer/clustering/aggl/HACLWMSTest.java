package org.clueminer.clustering.aggl;

import org.clueminer.attributes.BasicAttrType;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.hclust.linkage.SingleLinkage;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HACLWMSTest {

    private final HACLWMS subject = new HACLWMS();
    private static final double delta = 1e-9;

    public HACLWMSTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private Dataset<? extends Instance> simpleData() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{0, 0}, "A");
        data.builder().create(new double[]{1, 3}, "B");
        data.builder().create(new double[]{2, 2}, "C");
        data.builder().create(new double[]{2, 1}, "D");
        return data;
    }

    /**
     * Testing dataset from Kumar (chapter 8, page 519)
     *
     * @return
     */
    protected Dataset<? extends Instance> kumarData() {
        Dataset<Instance> data = new ArrayDataset<>(4, 2);
        data.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        data.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        data.builder().create(new double[]{0.40, 0.53}, "1");
        data.builder().create(new double[]{0.22, 0.38}, "2");
        data.builder().create(new double[]{0.35, 0.32}, "3");
        data.builder().create(new double[]{0.26, 0.19}, "4");
        data.builder().create(new double[]{0.08, 0.41}, "5");
        data.builder().create(new double[]{0.45, 0.30}, "6");
        return data;
    }

    @Test
    public void testUpdateProximity() {
        Dataset<? extends Instance> dataset = simpleData();
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult result = subject.hierarchy(dataset, pref);
        Matrix similarityMatrix = result.getProximityMatrix();
        assertNotNull(similarityMatrix);
        assertEquals(similarityMatrix.rowsCount(), dataset.size());
        assertEquals(similarityMatrix.columnsCount(), dataset.size());
        System.out.println("simple data - 4 points");
        similarityMatrix.printLower(5, 2);
        result.getTreeData().print();
    }

    @Test
    public void testSingleLinkage() {
        Dataset<? extends Instance> dataset = kumarData();
        assertEquals(6, dataset.size());
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult result = subject.hierarchy(dataset, pref);
        Matrix similarityMatrix = result.getProximityMatrix();
        assertNotNull(similarityMatrix);
        assertEquals(similarityMatrix.rowsCount(), dataset.size());
        assertEquals(similarityMatrix.columnsCount(), dataset.size());
        similarityMatrix.printLower(5, 2);
        System.out.println("kumar - single");
        DendroTreeData tree = result.getTreeData();
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
        assertEquals(0.215870331449522, root.getHeight(), delta);
    }

    @Test
    public void testSingleLinkageSchool() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();
        assertEquals(17, dataset.size());
        Props pref = new Props();
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult result = subject.hierarchy(dataset, pref);
        System.out.println("school - single");
        DendroTreeData tree = result.getTreeData();
        Matrix similarityMatrix = result.getProximityMatrix();
        similarityMatrix.printLower(5, 2);
        tree.print();
        assertEquals(dataset.size(), tree.numLeaves());
        DendroNode root = tree.getRoot();
        //assertEquals(47.18370587395614, root.getHeight(), delta);
        assertEquals(32.54273498033004, root.getHeight(), delta);
    }

}