package com.github.texxel.data;

/**
 * <p>Classes implementing this are able to be saved into a data stream. In order for classes to be
 * restored, the class must implement at least one of the following (first implementation listed will be used):</p>
 * <ul>
 *     <li>A constructor that accepts a single DataIn object</li>
 *     <li>A no arg constructor</li>
 * </ul>
 * <p>DataSerializable's cannot be inner classes (unless static) or anonymous classes. The constructors can have any
 * access level To stop IDEs complaining that the constructor is unused, the {@link Constructor} annotation is provided</p>
 */
public interface DataSerializable {

    /**
     * Stores the data for this object into a bundle so it can be reloaded latter. The passed bundle
     * is an empty bundle that this object should write all its data into
     * @param data the Data bundle to save data to
     */
    void bundleInto(DataOut data);

}
