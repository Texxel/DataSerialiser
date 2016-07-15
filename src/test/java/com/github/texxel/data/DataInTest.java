package com.github.texxel.data;

import com.github.texxel.data.*;
import com.github.texxel.data.exceptions.MissingDataException;
import com.github.texxel.data.exceptions.WrongTypeException;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataInTest {

    public static class TestClass implements DataSerializable {

        Object value;

        public TestClass(Object value) {
            this.value = value;
        }

        @Constructor
        private TestClass(DataIn in) {
            value = in.read("value", Object.class);
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

        assertEquals(true, in.read("bool", Boolean.class));
        assertEquals(123, (long)in.read("long", Long.class));
        assertEquals("thing", in.read("string", String.class));
        assertEquals(1.4, in.read("double", Double.class), 0.1);
    }

    @Test
    public void testReadCastPrimitives() {
        PData pData = new PData()
                .set("one", 1)
                .set("half", 0.5);
        DataIn in = new DataIn(pData);

        assertEquals(1, (int)in.read("one", Integer.class));
        assertEquals(1, (long)in.read("one", Long.class));
        assertEquals(1, in.read("one", Float.class), 0.01);
        assertEquals(1, in.read("one", Double.class), 0.01);

        assertEquals(0.5f, in.read("half", Float.class), 0.01);
        assertEquals(0.5, in.read("half", Double.class), 0.01);
    }

    @Test
    public void testReadNull() {
        PData pData = new PData()
                .setNull("null");
        DataIn in = new DataIn(pData);

        // it shouldn't matter what type it's read in as
        assertNull(in.read("null", String.class));
        assertNull(in.read("null", Integer.class));
        assertNull(in.read("null", Object.class));
    }

    @Test
    public void testReadSection() {
        PData pData = new PData()
                .set("top", 10);
        pData.createSection("ladder")
                .set("bottom", -10);
        DataIn in = new DataIn(pData);

        assertEquals(10, (long)in.read("top", Long.class));
        assertNotNull(in.readSection("ladder"));
        assertEquals(-10, (long)in.readSection("ladder").read("bottom", Long.class));
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

        DataIn sub = (DataIn)in.read("salmon", Object.class);
        assertNotNull(sub);
    }

    @Test(expected = MissingDataException.class)
    public void testExceptionOnMissingInfo() {
        PData data = new PData()
            .set("here", true);
        DataIn in = new DataIn(data);

        in.read("here", Boolean.class);
        in.read("there", Long.class);
    }

    @Test(expected = WrongTypeException.class)
    public void testExceptionOnWrongType() {
        PData data = new PData()
                .set("int", 10);
        DataIn in = new DataIn(data);

        in.read("int", String.class);
    }

    @Test
    public void testReadPrimitivesAsObject() {
        PData data = new PData()
                .set("one", 1)
                .set("half", 0.5)
                .set("string", "tuna")
                .set("bool", false);
        DataIn in = new DataIn(data);

        Object one    = in.read("one", Object.class);
        Object half   = in.read("half", Object.class);
        Object string = in.read("string", Object.class);
        Object bool   = in.read("bool", Object.class);

        assertEquals(1l, one);
        assertEquals(0.5, half);
        assertEquals("tuna", string);
        assertEquals(false, bool);
    }

    @Test
    public void testReadDataSerializable() {
        PData data = new PData();
        data.createSection("object")
                .set("__classname", TestClass.class.getName())
                .set("__id", "__#123")
                .set("value", "hi");
        DataIn in = new DataIn(data);

        TestClass obj = in.read("object", TestClass.class);

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

        TestClass obj = (TestClass)in.read("object", Object.class);

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
        TestClass a = in.read("A", TestClass.class);
        TestClass b = in.read("B", TestClass.class);

        assertSame(b, a.value);
        assertSame(a, b.value);
    }

}