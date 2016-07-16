package com.github.texxel.data.serializers;

import com.github.texxel.data.DataConverter;
import com.github.texxel.data.DataIn;
import com.github.texxel.data.DataOut;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapConverter implements DataConverter<Map> {

    @Override
    public void serialize (Map obj, DataOut bundle) {
        int index = 0;
        for (Map.Entry entry : (Set<Map.Entry>)obj.entrySet()) {
            bundle.write(Integer.toString(index) + "-k", entry.getKey());
            bundle.write(Integer.toString(index) + "-v", entry.getValue());
            index++;
        }
        bundle.write("size", index);
    }

    @Override
    public Map create (DataIn bundle, Class<? extends Map> stored) {
        try {
            return stored.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return new HashMap<>();
        }
    }

    @Override
    public void initialise (DataIn bundle, Map obj) {
        int size = bundle.readInt("size");
        for (int i = 0; i < size; i++) {
            Object key = bundle.read(Object.class, Integer.toString(i) + "-k");
            Object val = bundle.read(Object.class, Integer.toString(i) + "-v");
            obj.put(key, val);
        }
    }
}
