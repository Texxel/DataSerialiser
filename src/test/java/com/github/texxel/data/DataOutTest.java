package com.github.texxel.data;

import com.github.texxel.data.exceptions.DataSerializationException;
import com.github.texxel.data.exceptions.InvalidDataException;
import org.junit.Test;

import static org.junit.Assert.*;

// Note: these tests are highly intertwined with the internal details of the class and may change at any time.
// Generally, the way the data is written to is supported, however, how the actions are then verified is completely
// dependent on the class' implementation.
public class DataOutTest {

    static class TestSerializable implements DataSerializable {

        String value;

        public TestSerializable(String value) {
            this.value = value;
        }

        @Constructor
        private TestSerializable(DataIn data) {
            this.value = data.read(String.class, "value");
        }

        @Override
        public void bundleInto (DataOut data) {
            data.write("value", value);
            if (value.equals("boom"))
                throw new RuntimeException("KABOOM!");
        }
    }

    static class TestRef implements DataSerializable {

        String id;
        TestRef reference;

        public TestRef(String id, TestRef value) {
            this.id = id;
            this.reference = value;
        }

        @Constructor
        private TestRef(DataIn data) {
            id = data.read(String.class, "id");
            reference = data.read(TestRef.class, "value");
        }

        @Override
        public void bundleInto (DataOut data) {
            data.write("id", id);
            data.write("value", reference);
        }
    }

    @Test
    public void testSimpleDataToPData() {
        DataOutRoot data = new DataOutRoot();
        data.write("int", 1);
        data.write("float", 1.5f);
        data.write("fish", "blue");
        data.write("bool", false);

        PData pdata = data.toPrimitiveData();

        assertEquals(1, pdata.getLong("int"));
        assertEquals(1.5, pdata.getDouble("float"), 0.01);
        assertEquals("blue", pdata.getString("fish"));
        assertEquals(false, pdata.getBoolean("bool"));
    }

    @Test
    public void testWriteStringAsObject() {
        DataOutRoot data = new DataOutRoot();
        data.write("thing", (Object)"one");

        PData pData = data.toPrimitiveData();

        assertEquals("one", pData.getString("thing"));
    }

    @Test
    public void testSectionsInDataToPData() {
        DataOutRoot root = new DataOutRoot();
        root.write("hello", "bob");
        DataOut subData = root.createSection("hia");
        subData.write("one", 1);

        PData pData = root.toPrimitiveData();

        assertEquals("bob", pData.getString("hello"));
        PData subPData = pData.getSection("hia");
        assertEquals(1, subPData.getLong("one"));
    }

    @Test
    public void testSerializableInData() {
        DataOutRoot data = new DataOutRoot();
        TestSerializable test = new TestSerializable("Fish");
        data.write("thing", test);

        PData pData = data.toPrimitiveData();

        PData thingData = pData.getSection("thing");
        assertEquals(TestSerializable.class.getName(), thingData.getString("__classname"));
        assertEquals("__#"+System.identityHashCode(test), thingData.getString("__id"));
        assertEquals("Fish", thingData.getString("value"));
    }

    @Test( expected = InvalidDataException.class)
    public void testSensibleExceptionOnInvalidData() {
        DataOutRoot data = new DataOutRoot();
        data.write("Hello", new DataOutTest()); // cannot serialize DataOutTest

        data.toPrimitiveData();
    }

    @Test(expected = DataSerializationException.class)
    public void testSensibleExceptionOnBadLoad() {
        DataOutRoot data = new DataOutRoot();
        data.write("explode", new TestSerializable("boom"));

        data.toPrimitiveData();
    }

    @Test
    public void testNullInData() {
        DataOutRoot data = new DataOutRoot();
        data.write("hello", null);

        PData pData = data.toPrimitiveData();

        assertEquals(PData.Type.NULL, pData.getType("hello"));
    }

    @Test
    public void testMultipleReferences() {
        DataOutRoot data = new DataOutRoot();
        TestSerializable test = new TestSerializable("hi");
        data.write("hello", test );
        DataOut sub = data.createSection("enter");
        sub.write("boo", test );

        PData pData = data.toPrimitiveData();
        // System.out.println(pData);

        // test the object is written into the first reference
        PData contents = pData.getSection("hello");
        assertEquals(TestSerializable.class.getName(), contents.getString("__classname"));
        assertEquals("hi", contents.getString("value"));

        // the second should only reference the first
        String idCode = "__#" + System.identityHashCode(test);
        PData section = pData.getSection("enter");
        assertEquals(idCode, section.getString("boo"));

        // the top level should contain the reference location
        assertNotNull(pData.getSection(idCode));
        assertNotNull(pData.getSection(idCode).getSection("hello") );
        assertArrayEquals(new String[0], pData.getSection(idCode).getSection("hello").keys().toArray());
    }

    @Test
    public void testCircularConversion() {
        DataOutRoot out = new DataOutRoot();
        TestRef a = new TestRef("A", null);
        TestRef b = new TestRef("B", a);
        a.reference = b;
        out.write("A", a);
        out.write("B", b);

        PData pData = out.toPrimitiveData();
        // System.out.println(pData);

        // just don't crash!!
    }

    @Test
    public void testReferencesPlacedAtHighestLocation() {
        DataOutRoot out = new DataOutRoot();
        // make this structure
        // root
        //    + -> A
        //    |    + -> B
        //    |         + -> D  // reference D here
        //    + -> C
        //         + -> D       // write D data here
        TestRef d = new TestRef("D", null);
        TestRef c = new TestRef("C", d);
        TestRef b = new TestRef("B", d);
        TestRef a = new TestRef("A", b);
        out.write("A", a);
        out.write("C", c);

        PData data = out.toPrimitiveData();
        // System.out.println(data);

        // assert the reference is in the right place
        PData aData = data.getSection("A");
        PData bData = aData.getSection("value");
        String dDataRef = bData.getString("value");
        assertEquals("__#" + System.identityHashCode(d), dDataRef);

        // assert the data is in the right place
        PData cData = data.getSection("C");
        PData dData = cData.getSection("value");
        assertEquals("D", dData.getString("id"));

        // assert the reference is correct
        assertArrayEquals(new String[0], data.getSection(dDataRef).getSection("C").getSection("value").keys().toArray());
    }

    @Test
    public void testPDataDoesNotAlterData() {
        DataOutRoot out = new DataOutRoot();
        out.write("hello", "hi");

        PData pData = out.toPrimitiveData();
        pData.set("bye", "ole");

        PData pData2 = out.toPrimitiveData();
        assertFalse(pData2.contains("bye"));
    }
}