package com.edifecs.epp.security.data;

/**
 * Created by abhising on 15-07-2014.
 */
public enum RolePermissionType {
    DIRECT("Direct"), TRANSITIVE("Transitive");

    private String type;

    RolePermissionType(String type) {
        this.type = type;
    }
}
