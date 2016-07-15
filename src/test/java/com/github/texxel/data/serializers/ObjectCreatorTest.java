package com.github.texxel.data.serializers;

import com.github.texxel.data.Constructor;
import com.github.texxel.data.DataIn;
import com.github.texxel.data.DataOut;
import com.github.texxel.data.DataSerializable;
import com.github.texxel.data.exceptions.DataException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ObjectCreatorTest {

    static {
        try {
            // UNCOMMENT THIS METHOD WHEN PLAYING WITH NATIVE CODE
            //JNIGenerate.remakeSources();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class NoArgConstructor implements DataSerializable {
        private final int arg;

        private NoArgConstructor() {
            arg = 2;
        }

        @Override
        public void bundleInto( DataOut data ) {}
    }

    private static class DataConstructor implements DataSerializable {
        DataIn data;

        @Constructor
        private DataConstructor (DataIn data) {
            this.data = data;
        }

        @Constructor
        public DataConstructor() {
            throw new AssertionError( "The other constructor should get used" );
        }

        @Override
        public void bundleInto (DataOut data) {}
    }

    private static class ErrorInConstructor implements DataSerializable {

        private ErrorInConstructor () {
            throw new IllegalArgumentException("BOOM!");
        }

        @Override
        public void bundleInto (DataOut data) {}
    }

    @Test
    public void testCreateDoesNotCallConstructor() {
        NoArgConstructor obj = ObjectCreator.create(NoArgConstructor.class);

        assertEquals(0, obj.arg);
    }

    @Test
    public void testCallNoArgConstructor() {
        NoArgConstructor obj = ObjectCreator.create(NoArgConstructor.class);
        ObjectCreator.initialise(obj, null);

        assertEquals(2, obj.arg);
    }

    @Test
    public void testCallDataConstructor() {
        DataConstructor obj = ObjectCreator.create(DataConstructor.class);
        DataIn data = mock(DataIn.class);
        ObjectCreator.initialise(obj, data);

        assertEquals(data, obj.data);
    }

    @Test( expected = DataException.class)
    public void testHandlesErrors() {
        ErrorInConstructor obj = ObjectCreator.create(ErrorInConstructor.class);
        ObjectCreator.initialise(obj, null);
    }

}