/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.utils;

import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class PropsTest {

    private Props subject;
    private static final double DELTA = 1e-12;

    public PropsTest() {
    }

    @Before
    public void setUp() {
        subject = new Props();
    }

    @Test
    public void testGetGroupKeys() {
    }

    @Test
    public void testGetString_String() {
        subject.put("foo", "bar");
        assertEquals("bar", subject.get("foo"));
    }

    @Test
    public void testGetString_String_String() {
        assertEquals("value", subject.get("some-missing key", "value"));
    }

    @Test
    public void testGetInt_String() {
        String key = "an integer";
        int val = 123;
        subject.put(PropType.VISUAL, key, val);
        assertEquals(true, subject.containsKey(key));

        assertEquals(val, subject.getInt(PropType.VISUAL, key, 0));
        assertEquals(val, subject.getInt(PropType.VISUAL, key));
    }

    @Test
    public void testGetInt_String_int() {
    }

    @Test
    public void testGetBoolean_String() {
    }

    @Test
    public void testGetBoolean_String_boolean() {
        assertEquals(false, subject.getBoolean("some_bool", false));
        //intentionally
        assertEquals(false, subject.getBoolean("some_bool"));
    }

    @Test
    public void testGetLong_String() {
        String key = "long key";
        subject.putLong(key, 42L);
        assertEquals(42L, subject.getLong(key));
    }

    @Test
    public void testGetLong_String_long() {
    }

    @Test
    public void testGetDouble_String() {
        String key = "pi";
        double pi = 3.14159265358979323846264338327950288419716939937510;
        subject.put(key, pi);

        assertEquals(pi, subject.getDouble(key), DELTA);
        assertEquals(pi, subject.getDouble(key, -1), DELTA);
        //missing key
        assertEquals(-1.0, subject.getDouble("piii", -1), DELTA);
    }

    @Test
    public void testGet_PropType_String() {
        String key = "some double";
        double val = 2.7181;
        subject.put(key, val);
        assertEquals(true, subject.containsKey(key));

        assertEquals(val, subject.get(PropType.MAIN, key));
        Object obj = key;
        assertEquals("2.7181", subject.get(PropType.MAIN, obj));
    }

    @Test
    public void testToProperties() {
    }

    @Test
    public void testPutBooleanPt() {
        String key = "bool-pt-key";
        subject.put(PropType.PERFORMANCE, key, true);
        assertEquals(true, subject.getBoolean(PropType.PERFORMANCE, key));

        //get value, return default if missing
        key = "bool-pt-key2";
        subject.put(PropType.PERFORMANCE, key, true);
        assertEquals(true, subject.getBoolean(PropType.PERFORMANCE, key, false));

        key = "bool-pt-key3";
        assertEquals(true, subject.getBoolean(PropType.PERFORMANCE, key, true));
    }

    @Test
    public void testPutBoolean() {
        String key = "boolean-key";
        subject.putBoolean(key, true);
        assertEquals(true, subject.getBoolean(key));
        //overwrite key
        subject.putBoolean(key, false);
        assertEquals(false, subject.getBoolean(key));
    }

    @Test
    public void testGetString() {
        assertEquals(null, subject.get("really strange missing key"));
    }

    @Test
    public void testGetPerf() {
        Props p = new Props();
        p.put(PropType.PERFORMANCE, "keep-matrix", true);
        boolean b = p.getBoolean(PropType.PERFORMANCE, "keep-matrix");
        assertEquals(true, b);

        b = p.getBoolean(PropType.PERFORMANCE, "some-non-existing-key", true);
        assertEquals(true, b);
    }

    @Test
    public void testPutInt() {
        int i = 42;
        String key = "some integer";
        subject.putInt(key, i);
        assertEquals(subject.getInt(key), i);
    }

    @Test
    public void testPutDouble() {
        double d = 3.14159;
        String key = "some double";
        subject.putDouble(key, d);
        assertEquals(subject.getDouble(key), d, DELTA);
    }

    @Test
    public void testToString() {
        subject.put("some keeeey", "value");
        String str = subject.toString();
        System.out.println("toString: " + str);
    }

    @Test
    public void testLong() {
        long val = 15l;
        String key = "long number";
        subject.put(key, val);
        //store long, retrieve as int (with possible precision lost)
        assertEquals(subject.getInt(key), 15);
    }

    @Test
    public void testIntFromString() {
        String val = "25";
        String key = "int as string";
        subject.put(key, val);
        //store long, retrieve as int (with possible precision lost)
        assertEquals(subject.getInt(key), 25);
    }

    @Test
    public void testGetBoolean_PropType_String() {
        String key = "my_bool";
        //put boolean
        subject.put(key, true);
        //get boolean back
        assertEquals(true, subject.getBoolean(key));
        assertEquals(true, subject.containsKey(key));

        assertEquals(false, subject.getBoolean("misssing bool key", false));
    }

    @Test
    public void testCopy() {
        Props p = new Props();
        String key = "keep-matrix";
        p.put(PropType.PERFORMANCE, key, true);
        String strKey = "str";
        p.put(strKey, "foo");

        Props copy = p.copy();
        assertEquals(true, copy.getBoolean(PropType.PERFORMANCE, key));
        assertEquals(true, copy.getBoolean(PropType.PERFORMANCE, key, false));
        assertEquals("foo", copy.get(strKey));
        //change original value
        p.put(PropType.PERFORMANCE, key, false);
        p.put(strKey, "bar");
        //copy should be unchanged
        assertEquals(true, copy.getBoolean(PropType.PERFORMANCE, key));
        assertEquals("foo", copy.get(strKey));
    }

    @Test
    public void testCopyPropType() {
        Props p = new Props();
        String key = "keep-matrix";
        p.put(PropType.PERFORMANCE, key, true);
        String strKey = "str";
        p.put(strKey, "foo");
        p.put(PropType.VISUAL, "vis", "val");
        p.put(PropType.MAIN, "main", "do-not-copy");

        Props copy = p.copy(PropType.PERFORMANCE, PropType.VISUAL);
        assertEquals(true, copy.getBoolean(PropType.PERFORMANCE, key));
        assertEquals(true, copy.getBoolean(PropType.PERFORMANCE, key, false));
        //MAIN key should not be copied
        assertEquals(null, copy.get(strKey));
        assertEquals(null, copy.get("main"));
        assertEquals("val", copy.get(PropType.VISUAL, "vis"));
        //change original value
        p.put(PropType.PERFORMANCE, key, false);
        p.put(strKey, "bar");
        //copy should be unchanged
        assertEquals(true, copy.getBoolean(PropType.PERFORMANCE, key));
        assertEquals(null, copy.get(strKey));
    }

    @Test
    public void testClone() {
        Props p = new Props();
        p.put("foo", "foo");
        Props clone = p.clone();
        clone.put("foo", "bar");
        assertEquals("bar", clone.get("foo"));
        assertEquals("foo", p.get("foo"));
    }

    @Test
    public void testSize() {
        Props p = new Props();
        assertEquals(0, p.size());
        p.putBoolean("aaa", true);
        assertEquals(1, p.size());
    }

    @Test
    public void testIsEmpty() {
        Props p = new Props();
        assertEquals(true, p.isEmpty());
        p.put(PropType.PERFORMANCE, "foo", "foo");
        assertEquals(false, p.isEmpty());
    }

    @Test
    public void testContainsKey() {
    }

    @Test
    public void testContainsValue() {
    }

    @Test
    public void testGet_PropType_Object() {
    }

    @Test
    public void testRemove() {
    }

    @Test
    public void testClear() {
        Props p = new Props();
        p.put("foo", "x");
        assertEquals(1, p.size());
        p.clear();
        assertEquals(0, p.size());
    }

    @Test
    public void testKeySet() {
        Props p = new Props();
        p.put(PropType.MAIN, "foo", "bar");
        p.put(PropType.MAIN, "blah", "ble");
        p.put(PropType.VISUAL, "vis", "ble");
        Set<String> res = p.keySet(PropType.MAIN);
        Set<String> expected = new HashSet();
        expected.add("foo");
        expected.add("blah");
        assertEquals(expected, res);
        expected.add("vis");
        res = p.keySet();
        assertEquals(expected, res);
    }

    @Test
    public void testValues() {
    }

    @Test
    public void testDoubles() {
        Props p = new Props();
        p.put(PropType.VALIDATION, "fff", 123.0);
        assertEquals(123.0, p.getDouble(PropType.VALIDATION, "fff", Double.NaN), DELTA);
    }

    @Test
    public void testEntrySet() {
    }

    @Test
    public void testMerge() {
        Props p = new Props();
        p.put("foo", "x");
        p.put("bar", "y");
        Props other = new Props();
        other.put("foo", "a");
        other.put("baz", "c");
        other.put(PropType.PERFORMANCE, "perf", "ormance");

        p.merge(other);
        assertEquals("a", p.get("foo"));
        assertEquals("y", p.get("bar"));
        assertEquals("c", p.get("baz"));
        assertEquals("ormance", p.get(PropType.PERFORMANCE, "perf"));
    }

    @Test
    public void testToJson() {
        Props p = new Props();
        p.put("foo", "x");
        p.put("bar", "y");
        p.putInt("int", 123);
        p.putDouble("double", 3.14519);
        p.putBoolean("bool", true);

        assertEquals("{\"bar\":\"y\",\"bool\":\"true\",\"double\":3.14519,\"foo\":\"x\",\"int\":123}", p.toJson());
    }

    @Test
    public void testToJsonNonFinite() {
        Props p = new Props();
        p.putDouble("keey", Double.NaN);
        p.putDouble("ifninty", Double.NEGATIVE_INFINITY);
        String json = p.toJson();
        assertEquals("{\"ifninty\":-Infinity,\"keey\":NaN}", json);
        Props p2 = Props.fromJson(json);
        assertEquals(Double.NaN, p2.getDouble("keey"), DELTA);
        assertEquals(Double.NEGATIVE_INFINITY, p2.getDouble("ifninty"), DELTA);
    }

    @Test
    public void testFromJson() {
        Props p = Props.fromJson("{\"bar\":\"y\",\"bool\":\"true\",\"double\":3.14519,\"foo\":\"x\",\"int\":123}");
        assertEquals("x", p.get("foo"));
        assertEquals(123, p.getInt("int"));
    }

}
