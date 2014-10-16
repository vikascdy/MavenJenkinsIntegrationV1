package com.edifecs.epp.security.exception;

public class InvalidTokenException extends SecurityDataException {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Invalid Authentication Token";
    }

    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(Throwable t) {
        super(t);
    }

}