package com.github.texxel.data;

import com.github.texxel.data.serializers.ArrayConverter;
import com.github.texxel.data.serializers.CollectionConverter;
import com.github.texxel.data.serializers.DataSerializer;
import com.github.texxel.data.serializers.EnumConverter;

import java.util.*;

/**
 * A singleton class where classes can be registered to have custom serialization.
 * If a class is registered with custom serialization, then all the subclasses
 * of the registered class will also use that same serialization method.
 */
public final class ConverterRegistry {

    private static ClassMap<Object, DataConverter> map = new ClassMap<>();
    private static DataConverter arrayConverter = new ArrayConverter();

    public static <T> void register( Class<T> clazz, DataConverter<? super T> converter ) {
        map.put(clazz, converter);
    }

    public static <T> DataConverter<T> find(Class<? extends T> clazz) {
        // arrays need special consideration because there is no common array ancestor
        if (clazz.isArray())
            return arrayConverter;

        return map.get( clazz );
    }

    static {
        register(DataSerializable.class, new DataSerializer());
        register(Collection.class, new CollectionConverter());
        register(Class.class, new ClassConverter());
        register(Enum.class, new EnumConverter());
    }

}
