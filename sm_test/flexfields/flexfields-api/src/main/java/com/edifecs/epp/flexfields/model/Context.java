package com.edifecs.epp.flexfields.model;

/**
 * Created by sandeep.kath on 5/11/2014.
 */
public enum Context {

    TENANT("tenantName"),
    APPLICATION("appName"),
    COMPONENT("componentName"),
    ENTITY("entityName"),
    PERMISSION("permissionRequired"),
    ENTITY_ID("entityId");

    private String text;

    private Context(final String newText) {
        text = newText;
    }

    public final String getText() {
        return text;
    }

    public static final Context fromString(final String text) {
        if (text != null) {
            for (Context b : Context.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}
