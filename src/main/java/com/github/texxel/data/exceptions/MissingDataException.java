package com.github.texxel.data.exceptions;

/**
 * An exception to throw when the data does not contain the required data
 */
public class MissingDataException extends DataException {

    public MissingDataException() {
        super( );
    }

    public MissingDataException( String message ) {
        super( message );
    }

    public MissingDataException( String message, Throwable cause ) {
        super( message, cause );
    }

    public MissingDataException( Throwable cause ) {
        super( cause );
    }

}
