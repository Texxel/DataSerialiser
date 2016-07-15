package com.github.texxel.data.serializers;

import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.github.texxel.data.DataIn;
import com.github.texxel.data.exceptions.DataException;
import com.github.texxel.data.exceptions.DataSerializationException;

import java.lang.reflect.Constructor;

/**
 * A helper class for separating the task of allocating an object before
 * constructing the object.
 */
class ObjectCreator {

    static {
        new SharedLibraryLoader("libs/data-natives.jar").load("data");
    }

    /**
     * Generates a reference to an object. The object will not have been
     * initialised yet (i.e. it will not have had its constructor called so even
     * final fields may not be set correctly). Make sure to always pass the
     * created object into {@link #initialise(Object, DataIn)} after calling
     * this method.
     * @param clazz the class to create a reference to
     * @return the new class
     */
    static <T> T create (Class<T> clazz) {
        return (T)allocate(clazz);
    }

    /**
     * Calls the relevant constructors on the object. This method should only be
     * used on objects returned from {@link #create(Class)}
     * @param obj the object to initialise
     */
    static void initialise(Object obj, DataIn data) {
        if ( obj == null )
            return;
        Class clazz = obj.getClass();
        Constructor[] constructors = clazz.getDeclaredConstructors();

        // look for constructor that accepts Data
        for (Constructor c : constructors) {
            if (c.getParameterCount() != 1)
                continue;
            if (c.getParameterTypes()[0].equals(DataIn.class)) {
                try {
                    callDataConstructor(obj, data);
                } catch (Throwable thr) {
                    throw new DataSerializationException("Failed to construct " + clazz, thr);
                }
                return;
            }
        }

        // look for empty constructor
        for (Constructor c : constructors) {
            if (c.getParameterCount() != 0)
                continue;
            try {
                callEmptyConstructor(obj);
            } catch (Throwable thr) {
                throw new DataSerializationException("Failed to construct " + clazz, thr);
            }
            return;
        }

        throw new DataException(clazz + " does not have a supported constructor");
    }

    static native Object allocate (Class objClazz); /*
        jobject obj = env->AllocObject(objClazz);
        return obj;
    */

    static native void callDataConstructor (Object o, DataIn data); /*
        jclass objClazz = env->GetObjectClass(o);
        jmethodID constructor = env->GetMethodID(objClazz, "<init>", "(Lcom/github/texxel/data/DataIn;)V");
        env->CallVoidMethod(o, constructor, data);
    */

    static native void callEmptyConstructor (Object o); /*
        jclass objClazz = env->GetObjectClass(o);
        jmethodID constructor = env->GetMethodID(objClazz, "<init>", "()V");
        env->CallVoidMethod(o, constructor);
    */
}
