package com.edifecs.epp.isc.exception;

public class ClusterBuilderException extends Exception {
    private static final long serialVersionUID = 1L;

    public ClusterBuilderException(String string) {
        super(string);
    }

    public ClusterBuilderException(Exception e) {
        super(e);
    }

    public ClusterBuilderException(String string, Exception e) {
        super(string, e);
    }
}
