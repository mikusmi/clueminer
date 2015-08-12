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
package org.clueminer.clustering.api;

/**
 * Method for computing cost of merging two clusters
 *
 * @author deric
 */
public interface MergeEvaluation {

    /**
     * Method identification
     *
     * @return unique name
     */
    String getName();

    /**
     * Compute cost of merging cluster A and cluster B and thus forming larger
     * cluster C
     *
     * @param a
     * @param b
     * @return
     */
    double score(Cluster a, Cluster b);

    /**
     * Whether bigger values are better
     *
     * @return
     */
    boolean isMaximized();

}
