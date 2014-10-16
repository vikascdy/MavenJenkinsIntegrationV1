package com.edifecs.epp.flexfields.exception;

/**
 * Created by sandeep.kath on 5/7/2014.
 */
public class FlexFieldRegistryException extends Exception {
    public FlexFieldRegistryException() {
        super();
    }

    public FlexFieldRegistryException(String message) {
        super(message);
    }

    public FlexFieldRegistryException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlexFieldRegistryException(Throwable cause) {
        super(cause);
    }

    protected FlexFieldRegistryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
