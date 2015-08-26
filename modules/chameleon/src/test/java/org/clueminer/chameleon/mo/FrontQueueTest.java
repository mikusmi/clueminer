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

import java.util.ArrayList;
import java.util.LinkedList;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class FrontQueueTest {

    private FrontQueue subject;

    public FrontQueueTest() {
    }

    @Before
    public void setUp() {
        LinkedList<ArrayList<Integer>> fronts = new LinkedList<>();
        int n = 15;
        ArrayList<Integer> curr = null;
        for (int i = 0; i < n; i++) {
            if (i % 4 == 0) {
                curr = new ArrayList<>();
                fronts.add(curr);
            }
            curr.add(i);
        }
        subject = new FrontQueue(fronts);
    }

    @Test
    public void testSize() {
        assertEquals(15, subject.size());
    }

    @Test
    public void testIsEmpty() {
        assertEquals(false, subject.isEmpty());
        LinkedList<ArrayList<Integer>> fronts = new LinkedList<>();
        FrontQueue other = new FrontQueue(fronts);
        assertEquals(true, other.isEmpty());
    }

    @Test
    public void testPoll() {
        int item;
        for (int i = 0; i < 15; i++) {
            item = (int) subject.poll();
            assertEquals(i, item);
        }
    }

}
