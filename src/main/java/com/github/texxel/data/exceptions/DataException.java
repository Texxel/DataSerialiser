package com.github.texxel.data.exceptions;

/**
 * A base class for any type of Data Exception
 */
public class DataException extends RuntimeException {

    public DataException() {
        super( );
    }

    public DataException( String message ) {
        super( message );
    }

    public DataException( String message, Throwable cause ) {
        super( message, cause );
    }

    public DataException( Throwable cause ) {
        super( cause );
    }

    protected DataException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
