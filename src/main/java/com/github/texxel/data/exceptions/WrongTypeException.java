package com.github.texxel.data.exceptions;

/**
 * An exception to throw when the wrong type of data has been requested
 */
public class WrongTypeException extends DataException {

    public WrongTypeException() {
        super( );
    }

    public WrongTypeException( String message ) {
        super( message );
    }

    public WrongTypeException(Class expected, Class actual) {
        this("Expected " + expected.getName() + " but found " + actual.getName());
    }

    public WrongTypeException( String message, Throwable cause ) {
        super( message, cause );
    }

    public WrongTypeException( Throwable cause ) {
        super( cause );
    }
}
