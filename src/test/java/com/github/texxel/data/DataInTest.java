package com.github.texxel.data;

import com.github.texxel.data.exceptions.MissingDataException;
import com.github.texxel.data.exceptions.WrongTypeException;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataInTest {

    public static class TestClass implements DataSerializable {

        Object value;

        @Constructor
        private TestClass(DataIn in) {
            value = in.read(Object.class, "value");
        }

        @Override
        public void bundleInto (DataOut data) {
            data.write("value", value);
        }
    }

    @Test
    public void testReadPrimitives() {
        PData pData = new PData()
                .set("string", "thing")
                .set("double", 1.4)
                .set("bool", true)
                .set("long", 123);
        DataIn in = new DataIn(pData);

        assertEquals(true, in.readBoolean("bool"));
        assertEquals(123, in.readLong("long"));
        assertEquals("thing", in.readString("string"));
        assertEquals(1.4, in.readDouble("double"), 0.1);
    }

    @Test
    public void testReadNull() {
        PData pData = new PData()
                .setNull("null");
        DataIn in = new DataIn(pData);

        // it shouldn't matter what type it's read in as
        assertNull(in.read(String.class, "null"));
        assertNull(in.read(Integer.class, "null"));
        assertNull(in.read(Object.class, "null"));
    }

    @Test
    public void testReadSection() {
        PData pData = new PData()
                .set("top", 10);
        pData.createSection("ladder")
                .set("bottom", -10);
        DataIn in = new DataIn(pData);

        assertEquals(10, in.readLong("top"));
        assertNotNull(in.readSection("ladder"));
        assertEquals(-10, in.readSection("ladder").readLong("bottom"));
    }

    @Test
    public void testSameSectionReadEachTime() {
        PData pData = new PData();
        pData.createSection("fish");

        DataIn in = new DataIn(pData);

        assertSame(in.readSection("fish"), in.readSection("fish"));
    }

    @Test
    public void testReadSectionAsObject() {
        PData pData = new PData();
        pData.createSection("salmon");
        DataIn in = new DataIn(pData);

        DataIn sub = (DataIn)in.read(Object.class, "salmon");
        assertNotNull(sub);
    }

    @Test(expected = MissingDataException.class)
    public void testExceptionOnMissingInfo() {
        PData data = new PData()
            .set("here", true);
        DataIn in = new DataIn(data);

        in.readBoolean("here");
        in.readLong("there"); // crash here
    }

    @Test(expected = WrongTypeException.class)
    public void testExceptionOnWrongType() {
        PData data = new PData()
                .set("int", 10);
        DataIn in = new DataIn(data);

        in.read(String.class, "int");
    }

    @Test
    public void testReadPrimitivesAsWrapper() {
        PData data = new PData()
                .set("one", 1)
                .set("half", 0.5)
                .set("string", "tuna")
                .set("bool", false);
        DataIn in = new DataIn(data);

        Object one    = in.read(Long.class, "one");
        Object half   = in.read(Double.class, "half");
        Object string = in.read(String.class, "string");
        Object bool   = in.read(Boolean.class, "bool");

        assertEquals(1l, one);
        assertEquals(0.5, half);
        assertEquals("tuna", string);
        assertEquals(false, bool);
    }

    @Test
    public void testReadPrimitivesAsPrimitiveClassType() {
        DataOutRoot out = new DataOutRoot();
        out.write("long", 1);
        out.write("double", 1.3);
        out.write("bool", true);

        DataIn in = new DataIn(out.toPrimitiveData());

        assertEquals(1, (long)in.read(long.class, "long"));
        assertEquals(1, (int)in.read(int.class, "long"));
        assertEquals(1.3, in.read(double.class, "double"), 0.001);
        assertEquals(1.3f, in.read(float.class, "double"), 0.001);
        assertEquals(true, in.read(boolean.class, "bool"));
    }

    @Test
    public void testReadPrimitivesAsObject() {
        PData data = new PData()
                .set("one", 1)
                .set("half", 0.5)
                .set("string", "tuna")
                .set("bool", false);
        DataIn in = new DataIn(data);

        Object one    = in.read(Object.class, "one");
        Object half   = in.read(Object.class, "half");
        Object string = in.read(Object.class, "string");
        Object bool   = in.read(Object.class, "bool");

        assertEquals(1l, one);
        assertEquals(0.5, half);
        assertEquals("tuna", string);
        assertEquals(false, bool);
    }

    @Test
    public void testReadCastPrimitivesWrappers() {
        PData pData = new PData()
                .set("one", 1)
                .set("half", 0.5);
        DataIn in = new DataIn(pData);

        assertEquals(1, (int)in.read(Integer.class, "one"));
        assertEquals(1, (long)in.read(Long.class, "one"));
        assertEquals(1, in.read(Float.class, "one"), 0.01);
        assertEquals(1, in.read(Double.class, "one"), 0.01);

        assertEquals(0.5f, in.read(Float.class, "half"), 0.01);
        assertEquals(0.5, in.read(Double.class, "half"), 0.01);
    }

    @Test
    public void testReadDataSerializable() {
        PData data = new PData();
        data.createSection("object")
                .set("__classname", TestClass.class.getName())
                .set("__id", "__#123")
                .set("value", "hi");
        DataIn in = new DataIn(data);

        TestClass obj = in.read(TestClass.class, "object");

        assertNotNull(obj);
        assertEquals("hi", obj.value);
    }

    @Test
    public void testReadDataSerializableAsObject() {
        PData data = new PData();
        data.createSection("object")
                .set("__classname", TestClass.class.getName())
                .set("__id", "__#123")
                .set("value", "hi");
        DataIn in = new DataIn(data);

        TestClass obj = (TestClass)in.read(Object.class, "object");

        assertNotNull(obj);
        assertEquals("hi", obj.value);
    }

    @Test
    public void testReadCircularReference() {
        // the data needed here is highly intertwined with the DataIn/Out implementations
        // don't rely on it being like this forever
        PData data = new PData();
        data.createSection("A")
                .set("__classname", TestClass.class.getName())
                .set("__id", "__#123")
                .set("value", "__#456");
        data.createSection("B")
                .set("__classname", TestClass.class.getName())
                .set("__id", "__#456")
                .set("value", "__#123");
        data.createSection("__#123").createSection("A");
        data.createSection("__#456").createSection("B");

        DataIn in = new DataIn(data);

        // these two lines are the real magic of this whole library
        TestClass a = in.read(TestClass.class, "A");
        TestClass b = in.read(TestClass.class, "B");

        assertSame(b, a.value);
        assertSame(a, b.value);
    }

    @Test
    public void testReadBupPrimitives() {
        PData data = new PData()
                .set("int", 2)
                .set("float", 1.3)
                .set("string", "hi")
                .set("bool", true);
        DataIn in = new DataIn(data);

        // longs
        assertEquals(2, in.readLong("int", -1));
        assertEquals(-1, in.readLong("float", -1));

        // double
        assertEquals(2, in.readDouble("int", -1), 0.001f);
        assertEquals(1.3, in.readDouble("float", -1), 0.001f);
        assertEquals(-1, in.readDouble("bool", -1), 0.001f);

        // strings
        assertEquals("hi", in.readString("string", "howdy"));
        assertEquals("howdy", in.readString("bool", "howdy"));
        assertEquals("howdy", in.readString("greet", "howdy"));

        // booleans
        assertEquals(true, in.readBoolean("bool", false));
        assertEquals(true, in.readBoolean("int", true));
    }

}