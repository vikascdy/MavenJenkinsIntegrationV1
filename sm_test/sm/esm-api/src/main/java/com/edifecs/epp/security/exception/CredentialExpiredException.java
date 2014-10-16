package com.edifecs.epp.security.exception;


public class CredentialExpiredException extends SecurityDataException {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Credential cannot be null.";
    }

    public CredentialExpiredException() {
        super();
    }

    public CredentialExpiredException(Throwable cause) {
        super(cause);
    }
}