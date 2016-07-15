package com.github.texxel.data.serializers;

import com.github.texxel.data.DataConverter;
import com.github.texxel.data.DataIn;
import com.github.texxel.data.DataOut;

import java.lang.reflect.Array;

/**
 * Converts all types of arrays
 */
public class ArrayConverter implements DataConverter<Object> {

    @Override
    public void serialize (Object array, DataOut out) {
        int size = Array.getLength(array);
        out.write("size", size);
        for (int i = 0; i < size; i++) {
            out.write(Integer.toString(i), Array.get(array, i));
        }
    }

    @Override
    public Object create (DataIn in, Class type) {
        int length = in.read("size", Integer.class);
        Class componentType = type.getComponentType();
        return Array.newInstance(componentType, length);
    }

    @Override
    public void initialise (DataIn in, Object array) {
        int length = Array.getLength(array);
        Class componentType = array.getClass().getComponentType();
        for (int i = 0; i < length; i++) {
            Array.set(array, i, in.read(Integer.toString(i), componentType));
        }
    }
}
