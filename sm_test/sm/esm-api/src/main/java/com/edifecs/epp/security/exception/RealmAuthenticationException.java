package com.edifecs.epp.security.exception;


/**
 * Thrown when an error prevents access to a {@link IBaseDataStore}, or
 * when an internal error occurs in the com.edifecs.epp.security.data store.
 * 
 * @author i-adamnels
 */
public class RealmAuthenticationException extends RealmException {
    private static final long serialVersionUID = 1L;
    
    private static final String error = "Invalid Authentication Credentials for Realm";
    
    public RealmAuthenticationException() {super(error);}
    public RealmAuthenticationException(Throwable cause) {super(error, cause);}
}
