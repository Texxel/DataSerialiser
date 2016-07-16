package com.github.texxel.data;

import org.junit.Test;

import java.util.*;

import static com.github.texxel.AssertHelpers.*;

public class JavaObjectSerializationTests {

    private static class TestClass implements DataSerializable {
        String value;

        @Constructor
        TestClass(DataIn in) {
            value = in.read(String.class, "value");
        }

        public TestClass(String value) {
            this.value = value;
        }

        @Override
        public void bundleInto (DataOut data) {
            data.write("value", value);
        }

        @Override
        public boolean equals (Object o) {
            return o instanceof TestClass && value.equals(((TestClass) o).value);
        }
    }
    private enum TestEnum {
        A, B;
    }

    @Test
    public void testCollectionSerialize() {
        List<TestClass> original = new LinkedList<>();
        original.add(new TestClass("Hello"));
        original.add(new TestClass("Pizza"));

        DataOutRoot out = new DataOutRoot();
        out.write("list", original);
        DataIn in = new DataIn(out.toPrimitiveData());

        LinkedList read = in.read(LinkedList.class, "list");
        assertListEquals(original, read);
    }

    @Test
    public void testClassSerialize() {
        Class clazz = TestClass.class;

        DataOutRoot out = new DataOutRoot();
        out.write("class", clazz);
        DataIn in = new DataIn(out.toPrimitiveData());

        Class classRead = in.read(Class.class, "class");
        assertEquals(clazz, classRead);
    }

    @Test
    public void testObjectArraySerialize() {
        Object[] objects = { "Hello", new TestClass("one") };

        DataOutRoot out = new DataOutRoot();
        out.write("objs", objects);
        DataIn in = new DataIn(out.toPrimitiveData());

        Object[] objsRead = in.read(Object[].class, "objs");
        assertArrayEquals(objects, objsRead);
    }

    @Test
    public void testCircularReferencedObjectArray() {
        Object[] array = new Object[2];
        array[0] = "first";
        array[1] = array;

        DataOutRoot out = new DataOutRoot();
        out.write("objs", array);
        DataIn in = new DataIn(out.toPrimitiveData());

        Object[] objsRead = in.read(Object[].class, "objs");

        assertEquals("first", objsRead[0]);
        assertSame(objsRead, objsRead[1]);
    }

    @Test
    public void testPrimitiveArraySerialization() {
        int[] ints = { 1, 2, 3, 4 };

        DataOutRoot out = new DataOutRoot();
        out.write("ints", ints);

        DataIn in = new DataIn(out.toPrimitiveData());

        int[] read = in.read(int[].class, "ints");

        assertArrayEquals(ints, read);
    }

    @Test
    public void testMultidimensionalArraysAreConverted() {
        int[][] write = {
            { 11, 12, 13 },
            { 21, 22, 23, 24 },
        };

        DataOutRoot out = new DataOutRoot();
        out.write("ints", write);
        DataIn in = new DataIn(out.toPrimitiveData());

        int[][] read = in.read(int[][].class, "ints");

        assertArrayEquals(write, read);
    }

    @Test
    public void testPrimitiveArraysAreCondensed() {
        int[] array = {1, 2, 3};

        DataOutRoot out = new DataOutRoot();
        out.write("array", array);
        PData data = out.toPrimitiveData();

        assertEquals(PData.Type.LONG, data.getSection("array").getType("1"));
    }

    @Test
    public void testMapsReadAndWrite() {
        HashMap<String, String> strs = new HashMap<>();
        strs.put("one", "einse");
        strs.put("two", "drie");
        TreeMap<String, TestClass> objs = new TreeMap<>();
        objs.put("a", new TestClass("A"));
        objs.put("b", new TestClass("B"));

        DataOutRoot out = new DataOutRoot();
        out.write("strings", strs);
        out.write("objs", objs);

        DataIn in = new DataIn(out.toPrimitiveData());

        Map<String, Integer> intsRead = in.read(Map.class, "strings");
        Map<String, Integer> objsRead = in.read(Map.class, "objs");

        assertMapEquals(strs, intsRead);
        assertMapEquals(objs, objsRead);
    }

    @Test
    public void testEnumsReadAndWrite() {
        DataOutRoot out = new DataOutRoot();
        out.write("a", TestEnum.A);
        out.write("b", TestEnum.B);

        DataIn in = new DataIn(out.toPrimitiveData());

        assertEquals(TestEnum.A, in.read(TestEnum.class, "a"));
        assertEquals(TestEnum.B, in.read(Object.class, "b"));
    }

    @Test
    public void testIntegersInCollections() {
        Set<Integer> intsWrite = new HashSet<>();
        intsWrite.add(1);
        intsWrite.add(2);
        intsWrite.add(4);

        DataOutRoot out = new DataOutRoot();
        out.write("ints", intsWrite);

        DataIn in = new DataIn(out.toPrimitiveData());
        Set<Integer> intsRead = in.read(Set.class, "ints");

        assertSetEquals(intsWrite, intsRead);
    }

}
