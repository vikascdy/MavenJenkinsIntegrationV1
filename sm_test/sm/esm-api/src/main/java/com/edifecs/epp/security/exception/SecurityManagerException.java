package com.edifecs.epp.security.exception;

import com.edifecs.epp.exception.EppException;

/**
 * Thrown when an error prevents access to a {@link IBaseDataStore}, or
 * when an internal error occurs in the com.edifecs.epp.security.data store.
 * 
 * @author i-adamnels
 */
public class SecurityManagerException extends EppException {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Unknown Security Exception";
    }

    public SecurityManagerException(String[] arguments, Throwable cause) {
        super(arguments, cause);
    }

    public SecurityManagerException(String[] arguments) {
        super(arguments);
    }

    public SecurityManagerException(Throwable cause) {
        super(cause);
    }

    public SecurityManagerException() {super();}

}
