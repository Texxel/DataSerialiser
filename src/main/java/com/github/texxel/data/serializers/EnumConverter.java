package com.github.texxel.data.serializers;

import com.github.texxel.data.DataConverter;
import com.github.texxel.data.DataIn;
import com.github.texxel.data.DataOut;

public class EnumConverter implements DataConverter<Enum> {

    @Override
    public void serialize (Enum obj, DataOut bundle) {
        bundle.write("name", obj.toString());
    }

    @Override
    public Enum create (DataIn bundle, Class<? extends Enum> stored) {
        String name = bundle.read("name", String.class);
        return Enum.valueOf(stored, name);
    }

    @Override
    public void initialise (DataIn bundle, Enum obj) {

    }

}
