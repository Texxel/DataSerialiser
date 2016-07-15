package com.github.texxel.data;

import com.github.texxel.data.exceptions.DataSerializationException;
import com.github.texxel.data.exceptions.InvalidDataException;
import com.github.texxel.data.exceptions.MissingDataException;
import com.github.texxel.data.exceptions.WrongTypeException;
import com.github.texxel.data.serializers.ArrayConverter;

import java.util.*;

/**
 * A helper class for conversion between Data and Primitive data
 */
class PrimitiveConverter {

    private interface Converter<T> {
        void write (T obj, PData data, String key);
    }

    private static Map<Class, Converter> primitives = new HashMap<>();
    private static Map<Class, Class> wrappedTypes = new HashMap<>();

    static {
        primitives.put(Integer.class, new Converter<Integer>() {
            @Override
            public void write (Integer obj, PData data, String key) {
                data.set(key, obj);
            }
        });
        primitives.put(Float.class, new Converter<Float>() {
            @Override
            public void write (Float obj, PData data, String key) {
                data.set(key, obj);
            }
        });
        primitives.put(Double.class, new Converter<Double>() {
            @Override
            public void write (Double obj, PData data, String key) {
                data.set(key, obj);
            }
        });
        primitives.put(Long.class, new Converter<Long>() {
            @Override
            public void write (Long obj, PData data, String key) {
                data.set(key, obj);
            }
        });
        primitives.put(Boolean.class, new Converter<Boolean>() {
            @Override
            public void write (Boolean obj, PData data, String key) {
                data.set(key, obj);
            }
        });
        primitives.put(String.class, new Converter<String>() {
            @Override
            public void write (String obj, PData data, String key) {
                data.set(key, obj);
            }
        });

        wrappedTypes.put(boolean.class, Boolean.class);
        wrappedTypes.put(byte.class, Byte.class);
        wrappedTypes.put(short.class, Short.class);
        wrappedTypes.put(char.class, Character.class);
        wrappedTypes.put(int.class, Integer.class);
        wrappedTypes.put(long.class, Long.class);
        wrappedTypes.put(float.class, Float.class);
        wrappedTypes.put(double.class, Double.class);
    }

    public static void write (String key, Object obj, DataOut data) {
        PData pData = data.pData;
        if (obj == null) {
            pData.setNull(key);
            return;
        }
        Class clazz = obj.getClass();
        if (primitives.containsKey(clazz)) {
            primitives.get(clazz).write(obj, pData, key);
        } else {
            long id = System.identityHashCode(obj);
            String idKey = "__#" + id;
            // check if the data has already been written
            DataOut previous = data.global.get(id);
            if (previous != null) {
                if ( previous.depth <= data.depth ) {
                    // write a reference to the other location
                    pData.set(key, idKey);
                    // add a reference to the top level
                    if (!data.root.contains(idKey)) {
                        PData section = data.root.createSection(idKey);
                        for (String path : previous.path) {
                            section = section.createSection(path);
                        }
                    }
                } else {
                    // move the contents to over here
                    PData oldPContainer = data.containers.get(id);
                    PData newPContainer = data.pData;
                    PData objPData = previous.pData;

                    //rewire the primitive containers
                    oldPContainer.set(previous.path[previous.path.length - 1], idKey);
                    newPContainer.data.put(key, objPData);

                    // correct the data container
                    DataOut moved = new DataOut(data.getPath(key), objPData, data.global, data.containers, data.root, data.depth+1);
                    data.global.put(id, moved);

                    // correct the top level reference
                    PData section = data.root.createSection(idKey);
                    for (String path : moved.path) {
                        section = section.createSection(path);
                    }
                }
                return;
            }

            // check the object type can be serialized
            DataConverter converter = ConverterRegistry.find(clazz);
            if (converter == null)
                throw new InvalidDataException("No converter registered for '" + clazz.getName() + "'");

            // write the data here
            DataOut objData = data.createSection(key);
            data.global.put(id, objData);
            data.containers.put(id, data.pData);
            objData.write("__classname", clazz.getName());
            objData.write("__id", idKey);
            try {
                converter.serialize(obj, objData);
            } catch (Throwable thr) {
                throw new DataSerializationException("Failed to serialize '" + clazz.getName() + "'", thr);
            }
        }
    }

    public static <T> T read(String key, Class<T> expected, DataIn data) {
        PData pData = data.pData;
        PData.Type type = pData.getType(key);

        if (type == null)
            throw new MissingDataException("No value mapped to '" + key + "'");

        if (expected.isPrimitive())
            expected = wrappedTypes.get(expected);

        switch (type) {
            case NULL:
                return null;
            case BOOLEAN:
                if (expected.isAssignableFrom(Boolean.class))
                    return (T) (Boolean) pData.getBoolean(key);
                return badType(expected, Boolean.class, key);
            case LONG:
                if (expected.isAssignableFrom(Long.class))
                    return (T)(Long)pData.getLong(key);
                if (expected.isAssignableFrom(Integer.class))
                    return (T)(Integer)(int)pData.getLong(key);
                if (expected.isAssignableFrom(Float.class))
                    return (T)(Float)(float)pData.getLong(key);
                if (expected.isAssignableFrom(Double.class))
                    return (T)(Double)(double)pData.getLong(key);
                return badType(expected, Long.class, key);
            case DOUBLE:
                if (expected.isAssignableFrom(Double.class))
                    return (T)(Double)pData.getDouble(key);
                if (expected.isAssignableFrom(Float.class))
                    return (T)(Float)(float)pData.getDouble(key);
                return badType(expected, Double.class, key);
            case STRING:
                String contents = pData.getString(key);
                if (contents.startsWith("__#")) {
                    // this is actually an object reference
                    return getObjectReference(contents, data, expected);
                }
                if (expected.isAssignableFrom(String.class))
                    return (T)contents;
                return badType(expected, String.class, key);
            case DATA:
                // continue;
                break;
            default:
                throw new RuntimeException("Unknown type " + type);
        }
        // case Data:
        // look up the cache for the object
        Object cached = data.cache.get(key);
        if (cached != null) {
            return (T) cached;
        }

        T value;
        PData pDataSec = pData.getSection(key);
        DataIn dataSec = new DataIn(pDataSec, data.references, data.root);
        if (!pDataSec.contains("__classname")) {
            // this is just a simple sub bundle
            if (expected.isAssignableFrom(DataIn.class))
                value = (T) dataSec;
            else
                value = badType(expected, DataIn.class, key);
        } else {
            String id = pDataSec.getString("__id");
            Object maybe = data.references.get(id);
            if (maybe != null) {
                Class actual = maybe.getClass();
                if (expected.isAssignableFrom(actual))
                    return (T) maybe;
                else
                    throw new WrongTypeException(expected, actual);
            }
            value = createObject(dataSec, expected);
        }

        // store in cache and continue
        data.cache.put(key, value);
        return (T)value;
    }

    private static <T> T badType( Class expected, Class actual, String key ) {
        throw new WrongTypeException("Expected " + expected.getName() + " but found " + actual + " in key " + key);
    }

    private static <T> T getObjectReference(String id, DataIn data, Class<T> expected) {
        if (data.references.containsKey(id)) {
            Object value = data.references.get(id);
            if (value == null)
                return null;
            Class actual = value.getClass();
            if (expected.isAssignableFrom(actual))
                return (T)value;
            else
                return badType(expected, actual, id);
        }

        PData path = data.root.getSection(id);
        Set<String> keys = path.keys();
        PData objPData = data.root;
        while (!keys.isEmpty()) {
            String key = keys.iterator().next();
            path = path.getSection(key);
            objPData = objPData.getSection(key);
            keys = path.keys();
        }
        DataIn objData = new DataIn(objPData, data.references, data.root);
        return createObject(objData, expected);
    }

    private static <T> T createObject(DataIn data, Class<T> expected) {
        PData pData = data.pData;
        String actualName = pData.getString("__classname");
        Class actual;
        try {
            actual = Class.forName(actualName);
        } catch (ClassNotFoundException e) {
            throw new InvalidDataException("No class named '" + actualName + "'", e);
        }
        if (!expected.isAssignableFrom(actual))
            throw new WrongTypeException(expected, actual);
        DataConverter<T> converter = ConverterRegistry.find(actual);
        if (converter == null)
            throw new InvalidDataException("No converter registered for " + actual);
        T value = converter.create(data, (Class<T>)actual);
        data.references.put(pData.getString("__id"), value);
        converter.initialise(data, value);
        return value;
    }
}
