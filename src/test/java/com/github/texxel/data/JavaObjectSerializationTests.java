package com.github.texxel.data;

import com.github.texxel.data.*;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.github.texxel.AssertHelpers.*;

public class JavaObjectSerializationTests {

    private static class TestClass implements DataSerializable {
        String value;

        @Constructor
        TestClass(DataIn in) {
            value = in.read("value", String.class);
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
        PData pData = out.toPrimitiveData();

        DataIn in = new DataIn(pData);

        LinkedList read = in.read("list", LinkedList.class);
        assertListEquals(original, read);
    }

    @Test
    public void testClassSerialize() {
        Class clazz = TestClass.class;

        DataOutRoot out = new DataOutRoot();
        out.write("class", clazz);
        DataIn in = new DataIn(out.toPrimitiveData());

        Class classRead = in.read("class", Class.class);
        assertEquals(clazz, classRead);
    }

    @Test
    public void testObjectArraySerialize() {
        Object[] objects = { "Hello", new TestClass("one") };

        DataOutRoot out = new DataOutRoot();
        out.write("objs", objects);
        DataIn in = new DataIn(out.toPrimitiveData());

        Object[] objsRead = in.read("objs", Object[].class);
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

        Object[] objsRead = in.read("objs", Object[].class);

        assertEquals("first", objsRead[0]);
        assertSame(objsRead, objsRead[1]);
    }

    @Test
    public void testPrimitiveArraySerialization() {
        int[] ints = { 1, 2, 3, 4 };

        DataOutRoot out = new DataOutRoot();
        out.write("ints", ints);
        DataIn in = new DataIn(out.toPrimitiveData());

        int[] read = in.read("ints", int[].class);

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

        int[][] read = in.read("ints", int[][].class);

        assertArrayEquals(write, read);
    }

    @Test
    public void testPrimitivesReadAsPrimitiveClassType() {
        DataOutRoot out = new DataOutRoot();
        out.write("long", 1);
        out.write("double", 1.3);
        out.write("bool", true);

        DataIn in = new DataIn(out.toPrimitiveData());

        assertEquals(1, (long)in.read("long", long.class));
        assertEquals(1, (int)in.read("long", int.class));
        assertEquals(1.3, in.read("double", double.class), 0.001);
        assertEquals(1.3f, in.read("double", float.class), 0.001);
        assertEquals(true, in.read("bool", boolean.class));
    }

    @Test
    public void testEnumsReadAndWrite() {
        DataOutRoot out = new DataOutRoot();
        out.write("a", TestEnum.A);
        out.write("b", TestEnum.B);

        DataIn in = new DataIn(out.toPrimitiveData());

        assertEquals(TestEnum.A, in.read("a", TestEnum.class));
        assertEquals(TestEnum.B, in.read("b", Object.class));
    }

}
