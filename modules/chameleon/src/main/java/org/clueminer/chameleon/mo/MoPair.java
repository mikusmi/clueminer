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
package org.clueminer.chameleon.mo;

import org.clueminer.utils.PairValue;

/**
 * A pair of objects evaluated by N objectives (N is typically between 2 and 5).
 *
 * @author deric
 */
public class MoPair<T> extends PairValue<T> {

    private double[] objectives;

    public MoPair(T A, T B) {
        super(A, B);
        setNumObjectives(2);
    }

    public MoPair(T A, T B, int numObjectives) {
        super(A, B);
        setNumObjectives(numObjectives);
    }

    private void setNumObjectives(int numObjectives) {
        objectives = new double[numObjectives];
    }

    public double getObjective(int i) {
        return objectives[i];
    }

    public void setObjective(int i, double value) {
        objectives[i] = value;
    }

    /**
     * Express multiple objectives by single value
     *
     * @return
     */
    @Override
    public double getValue() {
        double value = 0.0;
        for (int i = 0; i < objectives.length; i++) {
            value += objectives[i];
        }
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Pair{ ");
        for (int i = 0; i < objectives.length; i++) {
            sb.append(i).append(":").append(objectives[i]);
        }
        //sb.append("A: ").append(A.toString()).append(", ");
        //sb.append("B: ").append(B.toString());
        sb.append("}");
        return sb.toString();
    }

}