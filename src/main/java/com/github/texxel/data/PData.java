package com.github.texxel.data;

import com.github.texxel.data.exceptions.MissingDataException;
import com.github.texxel.data.exceptions.WrongTypeException;

import java.util.*;

/**
 * <p>PData (primitive data) is the link between the complexity of {@link DataIn}/{@link DataOut} and conversion to a
 * text file. The Data class knows how to convert itself so it only contains primitive data. A {@link DataLoader} then
 * provides the link between strings and PData.</p>
 *
 * <p>If a getXXX() method is asked for something that does not exist, then it will throw a MissingDataException. If the
 * data does exist but is of the incorrect type, then a WrongTypeException will be thrown</p>
 *
 * <p>Null values in PData is supported. Null values will be taken as Type.NULL</p>
 */
public final class PData {


    /**
     * All the primitive types that might be in a PData
     */
    public enum Type {
        DOUBLE, LONG, STRING, BOOLEAN, DATA, NULL;

    }

    final Map<String, Object> data = new HashMap<>();
    final Set<String> publicKeys = Collections.unmodifiableSet(data.keySet());

    /** always throws an exception. The method determine the cause of the error and throw a tasteful error message */
    private <T> T crash(String key, Type expected) {
        if (data.containsKey(key)) {
            throw new WrongTypeException("Expected '" + expected + "' from key '" + key + "' but had '" + getType(key) + "'");
        } else {
            throw new MissingDataException("No data mapped to '" + key + "'");
        }
    }

    /**
     * Gets a long value from the data. Double values will NOT be converted to integer values (will return 0 as if the
     * data is missing)
     */
    public long getLong( String key ) {
        Object obj = data.get( key );
        if (obj instanceof Long)
            return (Long)obj;

        return crash(key, Type.LONG);
    }

    /**
     * Gets a double value from the data. Integer values will be converted to double values
     */
    public double getDouble( String key ) {
        Object obj = data.get( key );
        if (obj instanceof Double)
            return (Double)obj;
        else if (obj instanceof Long)
            return (long)obj;

        return crash(key, Type.DOUBLE);
    }

    public String getString( String key ) {
        Object obj = data.get( key );
        if (obj instanceof String)
            return (String)obj;

        return crash(key, Type.STRING);
    }

    public boolean getBoolean( String key ) {
        Object obj = data.get( key );
        if (obj instanceof Boolean)
            return (Boolean)obj;

        return crash(key, Type.BOOLEAN);
    }

    public PData getSection( String key ) {
        Object obj = data.get( key );
        if (obj instanceof PData)
            return (PData)obj;

        return crash(key, Type.DATA);
    }

    public PData set( String key, long value ) {
        data.put( key, value );
        return this;
    }

    public PData set( String key, double value ) {
        data.put( key, value );
        return this;
    }

    public PData set( String key, String value ) {
        data.put( key, value );
        return this;
    }

    public PData set( String key, boolean value ) {
        data.put( key, value );
        return this;
    }

    public PData setNull( String key ) {
        data.put(key, null);
        return this;
    }

    public PData createSection( String key ) {
        PData child = new PData();
        data.put( key, child );
        return child;
    }

    public PData delete( String key ) {
        data.remove( key );
        return this;
    }

    /**
     * Gets an unmodifiable list of all the keys in this Data
     * @return the keys of this data
     */
    public Set<String> keys() {
        return publicKeys;
    }

    public boolean contains (String key) {
        return data.containsKey(key);
    }

    /**
     * Gets the type of data at the given key. If the data has not been entered, then null is returned. Note: if the
     * data has Null entered into it, then Type.NuLL will be returned for that type
     * @param key the key to look into
     * @return the type at the key
     */
    public Type getType (String key) {
        Object obj = data.get( key );
        if ( obj == null )
            return data.containsKey(key) ? Type.NULL : null;
        if ( obj instanceof String )
            return Type.STRING;
        if ( obj instanceof Long )
            return Type.LONG;
        if ( obj instanceof Double )
            return Type.DOUBLE;
        if ( obj instanceof Boolean )
            return Type.BOOLEAN;
        if ( obj instanceof PData )
            return Type.DATA;
        throw new RuntimeException( "This should ever happen" );
    }

    /**
     * Creates a full copy of this data object.
     * @return a clone
     */
    public PData copy () {
        PData clone = new PData();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof PData) {
                clone.data.put(key, ((PData) value).copy());
            } else {
                clone.data.put(key, value);
            }
        }
        return clone;
    }

    /**
     * Creates a human readable dump of the data in this structure. Should only be used for debugging
     */
    @Override
    public String toString () {
        StringBuilder builder = new StringBuilder();
        toString(0, builder);
        return builder.toString();
    }
    private void toString( int depth, StringBuilder builder ) {
        builder.append("{\n");
        for (String key : keys()) {
            addSpace(depth+1, builder);
            builder.append(key + ": ");
            if (getType(key) == Type.DATA) {
                getSection(key).toString(depth+1, builder);
            } else {
                builder.append(data.get(key));
            }
            builder.append(",\n");
        }
        addSpace(depth, builder);
        builder.append("}");
    }
    private void addSpace(int depth, StringBuilder builder) {
        for ( int i = 0; i < depth; i++) {
            builder.append("  ");
        }
    }
}
