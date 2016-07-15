package com.github.texxel.data.exceptions;

/**
 * An exception to throw when data contains some information that makes no sense
 */
public class InvalidDataException extends DataException {

    public InvalidDataException() {
        super( );
    }

    public InvalidDataException( String message ) {
        super( message );
    }

    public InvalidDataException( String message, Throwable cause ) {
        super( message, cause );
    }

    public InvalidDataException( Throwable cause ) {
        super( cause );
    }

}
