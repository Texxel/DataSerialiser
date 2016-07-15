package com.github.texxel.data;

/**
 * A DataConverter can convert between some type of Object and Data.
 * DataConverters do not need to handle reading/writing null types.
 * @param <T> the type that this DataConverter can convert.
 */
public interface DataConverter<T> {

    /**
     * Writes the object into a bundle.
     * @param obj the object to write
     * @param bundle the bundle to write the object into
     */
    void serialize(T obj, DataOut bundle);

    /**
     * Reads out an object from a bundle. The object read out here only need to
     * be a correct reference; the object can be fully constructed latter in
     * {@link #initialise(DataIn, Object)}. The type created must be a subclass
     * of {@code expected}.
     * @param bundle the bundle to read out of
     * @param stored the class type that is written into the data.
     * @return the created object
     */
    T create(DataIn bundle, Class<? extends T> stored);

    /**
     * Finalises the creation of an object. The Object passed into this method
     * is the exact same as the Object passed out of {@link #create(DataIn, Class)}.
     * @param bundle the same bundle passed into {@link #create(DataIn, Class)}
     * @param obj the same object created in {@link #create(DataIn, Class)}
     */
    void initialise(DataIn bundle, T obj);

}
