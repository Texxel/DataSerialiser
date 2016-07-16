package com.github.texxel.data;

import java.util.HashMap;

/**
 * DataOut is the high level interface to how data can be written to a Data file. The DataOut class is readonly: once an
 * object has been written to it, there is no way of undoing it.
 */
public class DataOut {

    final PData pData;
    final String[] path;
    final HashMap<Long, DataOut> global;
    final HashMap<Long, PData> containers;
    final PData root;
    final int depth;

    DataOut (String[] path, PData pData, HashMap<Long, DataOut> global, HashMap<Long, PData> containers, PData root, int depth) {
        this.path = path;
        this.pData = pData;
        this.global = global;
        this.containers = containers;
        this.root = root == null ? pData : root;
        this.depth = depth;
    }

    /**
     * Writes an object to the data
     * @param key where to write the object
     * @param o the object to write
     * @return this
     */
    public DataOut write (String key, Object o) {
        PrimitiveConverter.write(key, o, this);
        return this;
    }

    public DataOut write(String key, long value) {
        pData.set(key, value);
        return this;
    }

    public DataOut write(String key, double value) {
        pData.set(key, value);
        return this;
    }

    public DataOut write(String key, boolean value) {
        pData.set(key, value);
        return this;
    }

    public DataOut write(String key, String value) {
        pData.set(key, value);
        return this;
    }

    /**
     * Creates a new section in the Data.
     * @param key the place to create the new section at
     * @return the new section
     */
    public DataOut createSection (String key) {
        PData pSection = pData.createSection(key);
        return new DataOut(getPath(key), pSection, global, containers, root, depth + 1);
    }

    String[] getPath(String key) {
        String[] location = new String[path.length+1];
        System.arraycopy(path, 0, location, 0, path.length);
        location[path.length] = key;
        return location;
    }
}
