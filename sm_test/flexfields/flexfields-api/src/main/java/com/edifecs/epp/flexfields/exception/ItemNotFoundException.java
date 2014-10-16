package com.edifecs.epp.flexfields.exception;

import java.io.Serializable;

/**
 * Created by sandeep.kath on 5/7/2014.
 */
public class ItemNotFoundException extends FlexFieldRegistryException {
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "%s with the id '%s' does not exist.";

    private final Class<? extends Serializable> type;
    private Object id;

    public ItemNotFoundException(Class<? extends Serializable> type, Object id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public String getMessage() {
        return String.format(MESSAGE, type.getSimpleName(), id);
    }
}
