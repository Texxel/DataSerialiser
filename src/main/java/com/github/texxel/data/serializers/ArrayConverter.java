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
            Class type = array.getClass().getComponentType();
            String key = Integer.toString(i);
            if (type.isPrimitive()) {
                // the class would work without this monstrosity, but it is
                // needed so that the output gets primitives values written
                // instead of objects (which looks ugly in the final form)
                if (array instanceof int[])
                    out.write(key, ((int[])array)[i]);
                else if (array instanceof boolean[])
                    out.write(key, ((boolean[])array)[i]);
                else if (array instanceof float[])
                    out.write(key, ((float[])array)[i]);
                else if (array instanceof double[])
                    out.write(key, ((double[])array)[i]);
                else if (array instanceof long[])
                    out.write(key, ((long[])array)[i]);
                else if (array instanceof char[])
                    out.write(key, ((char[])array)[i]);
                else if (array instanceof short[])
                    out.write(key, ((short[])array)[i]);
                else if (array instanceof byte[])
                    out.write(key, ((byte[])array)[i]);
                else
                    throw new RuntimeException("This should never happen");
            } else {
                out.write(key, Array.get(array, i));
            }
        }
    }

    @Override
    public Object create (DataIn in, Class type) {
        int length = in.readInt("size");
        Class componentType = type.getComponentType();
        return Array.newInstance(componentType, length);
    }

    @Override
    public void initialise (DataIn in, Object array) {
        int length = Array.getLength(array);
        Class componentType = array.getClass().getComponentType();
        for (int i = 0; i < length; i++) {
            Array.set(array, i, in.read(componentType, Integer.toString(i)));
        }
    }
}
