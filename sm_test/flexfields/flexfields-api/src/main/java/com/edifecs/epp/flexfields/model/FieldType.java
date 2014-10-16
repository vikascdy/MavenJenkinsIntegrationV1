package com.edifecs.epp.flexfields.model;

/**
 * Created by sandeep.kath on 5/11/2014.
 */
public enum FieldType {

    TEXT("TEXT"), STRING("STRING"), DOUBLE("DOUBLE"), LONG("LONG"), DATE("DATE"), BOOLEAN("BOOLEAN"), SELECTONE("SELECTONE");

    private String text;

    private FieldType(final String newText) {
        text = newText;
    }

    public final String getText() {
        return text;
    }

    public static final FieldType fromString(final String text) {
        if (text != null) {
            for (FieldType b : FieldType.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}

