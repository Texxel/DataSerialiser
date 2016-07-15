package com.github.texxel.data.serializers;

import com.github.texxel.data.DataConverter;
import com.github.texxel.data.DataIn;
import com.github.texxel.data.DataOut;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectionConverter implements DataConverter<Collection> {

    @Override
    public void serialize (Collection set, DataOut bundle) {
        int i = 0;
        bundle.write("size", set.size());
        for (Object obj : set) {
            bundle.write(Integer.toString(i), obj);
            i++;
        }
    }

    @Override
    public Collection create (DataIn bundle, Class<? extends Collection> actual) {
        try {
            return actual.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            if (Set.class.isAssignableFrom(actual))
                return new HashSet();
            else
                return new ArrayList();
        }
    }

    @Override
    public void initialise (DataIn bundle, Collection collection) {
        int size = bundle.read("size", Integer.class);
        for (int i = 0; i < size; i++) {
            Object o = bundle.read(Integer.toString(i), Object.class);
            collection.add(o);
        }
    }

}
