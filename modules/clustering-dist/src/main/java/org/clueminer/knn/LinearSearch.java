/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.knn;

import java.lang.reflect.Array;
import java.util.Arrays;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.neighbor.KNNSearch;
import org.clueminer.neighbor.NearestNeighborSearch;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.sort.MaxHeap;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Naive k-NN algorithm implementation. Searching for k nearest neighbors
 * requires O(n) comparisons.
 *
 * @author deric
 * @param <T>
 */
@ServiceProvider(service = KNNSearch.class)
public class LinearSearch<T extends Instance> extends AbstractKNN<T> implements NearestNeighborSearch<T>, KNNSearch<T> {

    public static final String NAME = "linear k-nn";

    public LinearSearch() {
        this.dm = EuclideanDistance.getInstance();
    }

    public LinearSearch(Dataset<T> dataset) {
        this.dataset = dataset;
        this.dm = EuclideanDistance.getInstance();
    }

    public LinearSearch(Dataset<T> dataset, Distance distance) {
        this.dataset = dataset;
        this.dm = distance;
    }

    @Override
    public String getName() {
        return NAME;
    }
  
    @Override
    public Neighbor[] knn(T q, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("Invalid k: " + k);
        }

        if (k > dataset.size()) {
            throw new IllegalArgumentException("Neighbor array length is larger than the dataset size");
        }
        double dist;
        Neighbor<T> neighbor = new Neighbor<>(null, 0, Double.MAX_VALUE);
        @SuppressWarnings("unchecked")
        Neighbor<T>[] neighbors = (Neighbor<T>[]) Array.newInstance(neighbor.getClass(), k);
        MaxHeap<Neighbor<T>> heap = new MaxHeap<>(neighbors);
        for (int i = 0; i < k; i++) {
            heap.add(neighbor);
            neighbor = new Neighbor<>(null, 0, Double.MAX_VALUE);
        }

        for (int i = 0; i < dataset.size(); i++) {
            if (q.equals(dataset.get(i)) && identicalExcluded) {
                continue;
            }

            //filter out noise
            if (exclude != null && exclude.contains(i)) {
                continue;
            }

            //TODO: distance is expected to be minimized (e.g Euclidean)
            // it won't be true in case of correlation etc.
            dist = dm.measure(q, dataset.get(i));
            //replace largest value in the heap
            Neighbor<T> datum = heap.peek();
            if (dm.compare(dist, datum.distance)) {
                datum.distance = dist;
                datum.index = i;
                datum.key = (T) dataset.get(i);
                heap.heapify();
            }
        }
        //heap.sort();
        //TODO: array is sorted from max to min
        Arrays.sort(neighbors);
        return neighbors;
    }

    @Override
    public Neighbor[] knn(T q, int k, Props params) {
        String dmProvider = params.get(Algorithm.DISTANCE, "Euclidean");
        this.dm = DistanceFactory.getInstance().getProvider(dmProvider);
        return knn(q, k);
    }

}
