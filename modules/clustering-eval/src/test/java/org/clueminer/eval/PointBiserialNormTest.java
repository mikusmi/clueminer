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
package org.clueminer.eval;

import org.clueminer.clustering.api.ScoreException;
import org.clueminer.fixtures.clustering.FakeClustering;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class PointBiserialNormTest {

    private final PointBiserialNorm subject;
    private static final double DELTA = 1e-9;

    public PointBiserialNormTest() {
        subject = new PointBiserialNorm();
    }

    @Test
    public void testIris() throws ScoreException {
        double scoreBetter = subject.score(FakeClustering.iris());
        double scoreWorser = subject.score(FakeClustering.irisMostlyWrong());

        System.out.println("better: " + scoreBetter);
        System.out.println("worser: " + scoreWorser);

        //should recognize better clustering
        assertEquals(true, subject.isBetter(scoreBetter, scoreWorser));
        //NbClust
        //assertEquals(0.680019167896978, scoreBetter, DELTA);
    }

}
