package com.github.texxel.data;

import java.util.HashMap;
import java.util.Set;

import com.github.texxel.data.exceptions.*;

/**
 * A High level interface that lets data be read in from a data file. The class
 * is readonly.
 */
public class DataIn {

    final PData root;
    final PData pData;
    final HashMap<String, Object> references;
    final HashMap<String, Object> cache;

    public DataIn(PData data) {
        this(data, new HashMap<String, Object>(), data);
    }

    DataIn(PData section, HashMap<String, Object> references, PData root ) {
        this.references = references;
        this.pData = section;
        this.root = root;
        cache = new HashMap<>();
    }

    /**
     * Gets an unmodifiable set of all the keys that are in this section of the data
     * @return the data keys
     */
    public Set<String> keys() {
        return pData.keys();
    }

    /**
     * Tests if this section has a specific key
     * @param key the key to test for
     * @return true if the data has the key
     */
    public boolean has( String key ) {
        return pData.contains(key);
    }

    /**
     * Reads out an Object from the Data. WARNING: the returned object may not
     * be initialised yet. Therefore, NEVER call any methods on the object or
     * let anyone else call methods (including methods like toString() or
     * hashCode()). It is only safe to call methods on the object once
     * deserialization has finished.
     * @param type the class that should be read
     * @param key where to read the object from
     * @return the created object
     * @throws MissingDataException if there is no value at the specified location
     * @throws WrongTypeException if the object at the specified location is of the incorrect type
     * @throws InvalidDataException if the data type is not supported
     * @throws DataSerializationException if there was any issue in constructing the object
     */
    public <T> T read (Class<T> type, String key) {
        return PrimitiveConverter.read(key, type, this);
    }

    /**
     * Reads out an object from the Data. If any error occurs during the read or
     * the data is missing, then the bup is returned. This is the exact same
     * result as calling {@link #read(Class, String)} and catching any exceptions.
     * @param type the class that is wanted
     * @param bup the object to return if the class is not okay
     * @return the object read or bup
     * @see {@link #read(Class, String)}
     */
    public <T> T read (Class<T> type, String key, T bup) {
        try {
            return read(type, key);
        } catch (DataException e) {
            return bup;
        }
    }

    public long readLong(String key) {
        return pData.getLong(key);
    }

    public long readLong(String key, long bup) {
        if (pData.getType(key) == PData.Type.LONG)
            return pData.getLong(key);
        return bup;
    }
    public int readInt(String key) {
        return (int)readLong(key);
    }

    public int readInt(String key, int bup) {
        return (int)readLong(key, bup);
    }

    public double readDouble(String key) {
        return pData.getDouble(key);
    }

    public double readDouble(String key, double bup) {
        if (pData.getType(key) == PData.Type.DOUBLE)
            return pData.getDouble(key);
        return bup;
    }

    public float readFloat(String key) {
        return (float)readDouble(key);
    }
    public float readFloat(String key, float bup) {
        return (float)readDouble(key, bup);
    }

    public boolean readBoolean(String key) {
        return pData.getBoolean(key);
    }

    public boolean readBoolean(String key, boolean bup) {
        if (pData.getType(key) == PData.Type.BOOLEAN)
            return pData.getBoolean(key);
        return bup;
    }

    public String readString(String key) {
        return pData.getString(key);
    }

    public String readString(String key, String bup) {
        if (pData.getType(key) == PData.Type.STRING)
            return pData.getString(key);
        return bup;
    }

    /**
     * Reads a section of data out.
     * @param key the place to read the section from
     * @return the read section
     * @throws MissingDataException if the section is not present
     * @throws WrongTypeException if the key does not contain a data section
     */
    public DataIn readSection(String key) {
        return read(DataIn.class, key);
    }
}
