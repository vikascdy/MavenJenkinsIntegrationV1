package com.edifecs.epp.security.exception;


/**
 * Thrown when an error prevents access to a {@link IBaseDataStore}, or
 * when an internal error occurs in the com.edifecs.epp.security.data store.
 * 
 * @author i-adamnels
 */
public class RealmException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private static final String error = "Error with realm configuration.";
    
    public RealmException() {super(error);}
    public RealmException(Throwable cause) {super(error, cause);}
    
    protected RealmException(String message) {super(message);}
    protected RealmException(String message, Throwable cause) {super(message, cause);}
}
