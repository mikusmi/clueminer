package org.clueminer.clustering.struct;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class ClusterList<E extends Instance> implements Clustering<Cluster<E>> {

    private static final long serialVersionUID = 5866077228917808995L;
    private Cluster<E>[] data;
    private Props params;
    /**
     * (n - 1) is index of last inserted item, n itself represents current
     * number of instances in this dataset
     */
    private int n = 0;
    //Lookup
    private final transient InstanceContent instanceContent;
    private final transient AbstractLookup lookup;
    private String name;

    public ClusterList(int capacity) {
        data = new Cluster[capacity];
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        params = new Props();
    }

    /**
     * Some identification of clustering doesn't have to be unique, but short
     *
     * @return
     */
    @Override
    public String getName() {
        if (name == null) {
            name = "|" + size() + "|";
        }
        return name;
    }

    /**
     * {@inheritDoc }
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    public final void ensureCapacity(int requested) {
        if (requested >= getCapacity()) {
            int capacity = (int) (requested * 1.618); //golden ratio :)
            if (capacity <= size()) {
                capacity = n * 3; // for small numbers due to int rounding we wouldn't increase the size
            }
            if (capacity > data.length) {
                Cluster[] tmp = new Cluster[capacity];
                System.arraycopy(data, 0, tmp, 0, data.length);
                data = tmp;
            }
        }
    }

    @Override
    public boolean hasAt(int index) {
        return index >= 0 && index < data.length && data[index] != null;
    }

    @Override
    public boolean add(Cluster<E> e) {
        ensureCapacity(n);
        //cluster numbers start from 0
        e.setClusterId(n);
        data[n++] = e;
        return true;
    }

    public int getCapacity() {
        return data.length;
    }

    @Override
    public String getClusterLabel(int i) {
        return get(i).getName();
    }

    /**
     * Numbers of clusters do now always start from 0, this method should return
     * first non-empty cluster
     *
     * @return first non-empty cluster, return null if no such cluster exists
     */
    public Cluster<E> first() {
        if (!isEmpty()) {

            for (Cluster c : data) {
                if (c != null) {
                    return c;
                }
            }
        }
        return null;
    }

    @Override
    public void put(Cluster<? extends Instance> d) {
        this.add((Cluster) d);
    }

    @Override
    public void put(int index, Cluster x) {
        ensureCapacity(index);

        if (data[index] == null) {
            n++;
        }
        data[index] = x;
        //cluster numbers start from 1
        x.setClusterId(index);
    }

    @Override
    public void merge(Cluster... datasets) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        return n;
    }

    /**
     *
     * @return total number of instances
     */
    @Override
    public int instancesCount() {
        int cnt = 0;
        for (Cluster c : data) {
            if (c != null) {
                cnt += c.size();
            }
        }
        return cnt;
    }

    @Override
    public E getCentroid() {
        Cluster<E> first = get(0);
        Instance centroid = first.builder().build(first.attributeCount());

        for (Cluster<E> c : this) {
            for (Instance inst : c) {
                //sum all features
                for (int i = 0; i < inst.size(); i++) {
                    centroid.set(i, inst.value(i) + centroid.value(i));
                }
            }
        }

        //average of features
        for (int i = 0; i < first.attributeCount(); i++) {
            centroid.set(i, centroid.value(i) / instancesCount());
        }
        return (E) centroid;
    }

    @Override
    public Iterator<Instance> instancesIterator() {
        return new InstancesIterator();
    }

    @Override
    public int[] clusterSizes() {
        int[] clusterSizes = new int[this.size()];
        for (int i = 0; i < this.size(); i++) {
            clusterSizes[i] = get(i).size();
        }
        return clusterSizes;
    }

    /**
     * Returns ID of assigned cluster, if ID not found it could be caused by
     * using methods which didn't supply original mapping (or it's really not
     * present)
     *
     * @param instanceId
     * @return original instance ID
     */
    @Override
    public int assignedCluster(int instanceId) {
        for (Cluster<E> cluster : this) {
            if (cluster.contains(instanceId)) {
                return cluster.getClusterId();
            }
        }
        return -1;
    }

    @Override
    public Cluster<E> get(int index) {
        return data[index];
    }

    @Override
    public Iterator<Cluster<E>> iterator() {
        return new ClusterIterator();
    }

    @Override
    public boolean isEmpty() {
        return (data == null || size() == 0);
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Cluster) {
            for (Cluster c : this) {
                if (c.equals(o)) {
                    return true;
                }
            }
        } else if (o instanceof Instance) {
            Instance inst;
            for (Iterator<Instance> iter = this.instancesIterator(); iter.hasNext();) {
                inst = iter.next();
                if (inst.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        return data;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) data;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object elem : c) {
            if (!contains(elem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Cluster<E>> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        int capacity = getCapacity();
        data = new Cluster[capacity];
        n = 0;
    }

    /**
     * Create new cluster and inserts it at given position
     *
     * @param clusterId
     * @return
     */
    @Override
    public Cluster<? extends Instance> createCluster(int clusterId) {
        int attrSize = guessAttrCnt();
        Cluster<? extends Instance> c = new BaseCluster(5, attrSize);
        c.setClusterId(clusterId);
        c.setName("cluster " + (clusterId + 1));
        put(clusterId, c);
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cluster<? extends Instance> createCluster() {
        int attrSize = guessAttrCnt();
        Cluster<? extends Instance> c = new BaseCluster(5, attrSize);
        int clusterId = size();
        c.setClusterId(clusterId);
        c.setName("cluster " + (clusterId + 1));
        put(clusterId, c);
        return c;
    }

    /**
     * Create new cluster with given ID and capacity
     *
     * @param clusterId
     * @param capacity
     * @return
     */
    @Override
    public Cluster<? extends Instance> createCluster(int clusterId, int capacity) {
        int attrSize = guessAttrCnt();
        Cluster<? extends Instance> c = new BaseCluster(capacity, attrSize);
        c.setClusterId(clusterId);
        c.setName("cluster " + (clusterId + 1));
        put(clusterId, c);
        return c;
    }

    private int guessAttrCnt() {
        int attrCnt = 5; //some default value
        if (data != null && !isEmpty()) {
            if (data[0] != null) {
                attrCnt = data[0].attributeCount();
            }
        }
        return attrCnt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ClusterList(" + size() + ")");
        return sb.append(getName()).toString();
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void lookupAdd(Object instance) {
        instanceContent.add(instance);
    }

    @Override
    public void lookupRemove(Object instance) {
        instanceContent.remove(instance);
    }

    @Override
    public Props getParams() {
        return params;
    }

    @Override
    public void setParams(Props params) {
        this.params = params;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Cluster<E> elem : this) {
            hash += elem.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final ClusterList<?> other = (ClusterList<?>) obj;
        if (this.size() != other.size()) {
            return false;
        }
        return Arrays.deepEquals(this.data, other.data);
    }

    /**
     * Will overwrite this with other
     *
     * @param other
     */
    @Override
    public void mergeParams(Props other) {
        for (String key : other.keySet()) {
            params.put(key, other.get(key));
        }
    }

    class ClusterIterator implements Iterator<Cluster<E>> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public Cluster<E> next() {
            return get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from dataset using the iterator.");

        }
    }

    /**
     * Should iterate over all instances in all clusters
     */
    private class InstancesIterator implements Iterator<Instance> {

        private int i = 0;
        private int j = 0;
        private int k = 0;
        private Cluster current;

        InstancesIterator() {
            current = get(k++);
        }

        @Override
        public boolean hasNext() {
            return i < instancesCount();
        }

        @Override
        public Instance next() {
            if (j < current.size()) {
                i++;
                return current.instance(j++);
            } else {
                i++;
                j = 0;
                current = get(k++);
                return current.instance(j++);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
