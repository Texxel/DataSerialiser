package com.github.texxel.data.exceptions;

/**
 * An exception to throw when there is an issue serializing data
 */
public class DataSerializationException extends DataException {

    public DataSerializationException () {
        super();
    }

    public DataSerializationException (String message) {
        super(message);
    }

    public DataSerializationException (String message, Throwable cause) {
        super(message, cause);
    }

    public DataSerializationException (Throwable cause) {
        super(cause);
    }

    protected DataSerializationException (String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
