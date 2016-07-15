package com.github.texxel.data.serializers;

import com.github.texxel.data.DataConverter;
import com.github.texxel.data.DataIn;
import com.github.texxel.data.DataOut;
import com.github.texxel.data.DataSerializable;
import com.github.texxel.data.serializers.ObjectCreator;

/**
 * The class that deserialises DataSerializables
 */
public class DataSerializer implements DataConverter<DataSerializable> {

    @Override
    public void serialize (DataSerializable obj, DataOut bundle) {
        bundle.write("__classname", obj.getClass().getName());
        obj.bundleInto(bundle);
    }

    @Override
    public DataSerializable create(DataIn bundle, Class<? extends DataSerializable> actual) {
        return ObjectCreator.create(actual);
    }

    @Override
    public void initialise (DataIn bundle, DataSerializable obj) {
        ObjectCreator.initialise(obj, bundle);
    }
}
