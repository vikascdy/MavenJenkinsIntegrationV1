package com.edifecs.epp.security.exception;

import com.edifecs.epp.exception.EppException;

/**
 * Thrown when an error prevents access to a {@link IBaseDataStore}, or
 * when an internal error occurs in the com.edifecs.epp.security.data store.
 * 
 * @author i-adamnels
 */
public class EmailSendException extends EppException {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Unable to send out email";
    }

    public EmailSendException(Throwable cause) {
        super(cause);
    }
}
