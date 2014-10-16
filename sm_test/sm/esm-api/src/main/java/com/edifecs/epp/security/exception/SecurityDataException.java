package com.edifecs.epp.security.exception;

/**
 * Thrown when an error prevents access to a {@link IBaseDataStore}, or when
 * an internal error occurs in the com.edifecs.epp.security.data store.
 * 
 * @author i-adamnels
 */
public class SecurityDataException extends SecurityManagerException {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Unknown error has occurred.";
    }

    public SecurityDataException() {
        super();
    }

    public SecurityDataException(String[] arguments) {
        super(arguments);
    }

    public SecurityDataException(String[] arguments, Throwable cause) {
        super(arguments, cause);
    }

    public SecurityDataException(Throwable cause) {
        super(cause);
    }
}
