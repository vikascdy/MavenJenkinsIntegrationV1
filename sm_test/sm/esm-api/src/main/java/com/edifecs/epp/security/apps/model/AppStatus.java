package com.edifecs.epp.security.apps.model;

/**
 * Created by sandeep.kath on 9/9/2014.
 */
public enum AppStatus {
    AVAILABLE("A"), PENDING("P"), INSTALLED("I"), UPGRADE("U"), DELETED("D");

    private String statusCode;

    private AppStatus(String s) {
        statusCode = s;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
