package com.bro2.b2lib;

import com.bro2.util.ReflectUtil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static class Rep {
        private static int sVal;

        private byte byteVal;
        private boolean boolVal;
        private char charVal = '0';
        private short shortVal;
        private int intVal;
        private long longVal;
        private float floatVal;
        private double doubleVal;
        private String objVal = "obj";

        int rep;

        @Override
        public String toString() {
            return "b: " + byteVal + " z: " + boolVal + " c: " + charVal + " s: " + shortVal
                    + " i: " + intVal + " j: " + longVal + " f: " + floatVal + " d: " + doubleVal
                    + " obj: " + objVal + " static: " + sVal + " rep: " + rep;
        }
    }

    private static class Child extends Rep {

    }

    @Test
    public void refRepTest() throws Exception {
        Rep rep = new Rep();

        System.out.println("before: " + rep);

        ReflectUtil.replaceField(rep, "byteVal", 1);
        ReflectUtil.replaceField(rep, "boolVal", true);
        ReflectUtil.replaceField(rep, "charVal", 65);
        ReflectUtil.replaceField(rep, "shortVal", 1);
        ReflectUtil.replaceField(rep, "intVal", 1);
        ReflectUtil.replaceField(rep, "longVal", 1);
        ReflectUtil.replaceField(rep, "floatVal", 1);
        ReflectUtil.replaceField(rep, "doubleVal", 1);
        ReflectUtil.replaceField(rep, "objVal", "1");

        ReflectUtil.replaceStaticField(Rep.class, "sVal", 1);

        System.out.println(" after: " + rep);

        ReflectUtil.replaceField(rep, "byteVal", null);
        ReflectUtil.replaceField(rep, "boolVal", null);
        ReflectUtil.replaceField(rep, "charVal", null);
        ReflectUtil.replaceField(rep, "shortVal", null);
        ReflectUtil.replaceField(rep, "intVal", null);
        ReflectUtil.replaceField(rep, "longVal", null);
        ReflectUtil.replaceField(rep, "floatVal", null);
        ReflectUtil.replaceField(rep, "doubleVal", null);
        ReflectUtil.replaceField(rep, "objVal", null);

        ReflectUtil.replaceStaticField(Rep.class, "sVal", null);

        System.out.println("after1: " + rep);

        ReflectUtil.replaceStaticField("com.bro2.b2lib.ExampleUnitTest$Rep", "sVal", 2);

        System.out.println("after2: " + rep);

        ReflectUtil.replaceField(rep, "none", 1);
    }

    @Test
    public void refGetTest() {
        Rep rep = new Rep();

        System.out.println("before: " + rep);

        assertEquals(ReflectUtil.getField(rep, "byteVal"), (byte) 0);
        assertEquals(ReflectUtil.getField(rep, "boolVal"), false);
        assertEquals(ReflectUtil.getField(rep, "charVal"), '0');
        assertEquals(ReflectUtil.getField(rep, "shortVal"), (short) 0);
        assertEquals(ReflectUtil.getField(rep, "intVal"), 0);
        assertEquals(ReflectUtil.getField(rep, "longVal"), (long) 0);
        assertEquals(ReflectUtil.getField(rep, "floatVal"), 0f);
        assertEquals(ReflectUtil.getField(rep, "doubleVal"), 0d);
        assertEquals(ReflectUtil.getField(rep, "objVal"), "obj");

        assertEquals(ReflectUtil.getStaticField(rep.getClass(), "sVal"), 0);

        assertEquals(ReflectUtil.getStaticField("com.bro2.b2lib.ExampleUnitTest$Rep", "sVal"), 0);

        ReflectUtil.replaceField(rep, "byteVal", 1);
        ReflectUtil.replaceField(rep, "boolVal", true);
        ReflectUtil.replaceField(rep, "charVal", 97);
        ReflectUtil.replaceField(rep, "shortVal", 1);
        ReflectUtil.replaceField(rep, "intVal", 1);
        ReflectUtil.replaceField(rep, "longVal", 1);
        ReflectUtil.replaceField(rep, "floatVal", 1);
        ReflectUtil.replaceField(rep, "doubleVal", 1);
        ReflectUtil.replaceField(rep, "objVal", "1");

        ReflectUtil.replaceStaticField(Rep.class, "sVal", 1);

        System.out.println(" after: " + rep);

        assertEquals(ReflectUtil.getField(rep, "byteVal"), (byte) 1);
        assertEquals(ReflectUtil.getField(rep, "boolVal"), true);
        assertEquals(ReflectUtil.getField(rep, "charVal"), 'a');
        assertEquals(ReflectUtil.getField(rep, "shortVal"), (short) 1);
        assertEquals(ReflectUtil.getField(rep, "intVal"), 1);
        assertEquals(ReflectUtil.getField(rep, "longVal"), (long) 1);
        assertEquals(ReflectUtil.getField(rep, "floatVal"), 1f);
        assertEquals(ReflectUtil.getField(rep, "doubleVal"), 1d);
        assertEquals(ReflectUtil.getField(rep, "objVal"), "1");

        assertEquals(ReflectUtil.getStaticField(rep.getClass(), "sVal"), 1);

        assertEquals(ReflectUtil.getStaticField("com.bro2.b2lib.ExampleUnitTest$Rep", "sVal"), 1);
    }

    @Test
    public void refExtTest() {
        Child child = new Child();

        System.out.println("before : " + child);

        ReflectUtil.replaceField(child, "rep", 1);

        System.out.println(" after : " + child);

        ReflectUtil.replaceField(child, "none", 1);

        System.out.println("after1 : " + child);
    }
}