package com.github.texxel.data.serializers;

import com.github.texxel.data.*;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DataSerializerTest {

    private static class TestObject implements DataSerializable {

        String value;

        public TestObject(String value) {
            this.value = value;
        }

        @Constructor
        private TestObject(DataIn data) {
            this.value = data.read(String.class, "Pizza");
        }

        @Override
        public void bundleInto (DataOut data) {
            data.write("Pizza", value);
        }
    }

    @Test
    public void testSerializesObject() {
        TestObject test = new TestObject("Meat lover");
        DataOut bundle = mock(DataOut.class);
        DataSerializer serializer = new DataSerializer();
        serializer.serialize(test, bundle);

        verify(bundle).write("Pizza", "Meat lover");
    }

    @Test
    public void testCreatesObject() {
        DataIn bundle = mock(DataIn.class);
        when(bundle.read(String.class, "Pizza")).thenReturn("Hawian");

        TestObject result = (TestObject) new DataSerializer().create(bundle, TestObject.class);

        // note: the object may not be initialised yet, so we can't test if it's a hawian pizza
        assertNotNull(result);
    }

    @Test
    public void testInitialisesObject() {
        DataIn bundle = mock(DataIn.class);
        when(bundle.read(String.class, "Pizza")).thenReturn("Hawian");

        DataSerializer serializer = new DataSerializer();
        TestObject result = (TestObject) serializer.create(bundle, TestObject.class);
        serializer.initialise(bundle, result);

        assertEquals("Hawian", result.value);
    }
}