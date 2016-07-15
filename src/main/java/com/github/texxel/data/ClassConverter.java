package com.github.texxel.data;

import com.github.texxel.data.exceptions.InvalidDataException;

public class ClassConverter implements DataConverter<Class> {

    @Override
    public void serialize (Class obj, DataOut bundle) {
        bundle.write("name", obj.getName());
    }

    @Override
    public Class create (DataIn bundle, Class<? extends Class> actual) {
        String classname = bundle.read("name", String.class);
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new InvalidDataException("Unknown class " + classname, e);
        }
    }

    @Override
    public void initialise (DataIn bundle, Class obj) {

    }

}
