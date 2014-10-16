package com.edifecs.epp.security.exception;


import com.edifecs.epp.exception.EppException;

/**
 * Thrown when an error prevents access to a {@link IBaseDataStore}, or
 * when an internal error occurs in the com.edifecs.epp.security.data store.
 * 
 * @author willclem
 */
public class SecurityDatabaseNotConfiguredException extends EppException {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "ESM Database not configured.";
    }

    public SecurityDatabaseNotConfiguredException() {
    }
    
    /**
     * 
     * @param cause
     */
    public SecurityDatabaseNotConfiguredException(Throwable cause) {
        super(cause);
    }
}
