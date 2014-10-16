package com.edifecs.core.configuration.exception;

import com.edifecs.core.configuration.helper.PropertiesException;

/**
 * Created by willclem on 6/5/2014.
 */
public class InvalidManifestException extends PropertiesException {

    private static final String msg = "Invalid manifest file found at directory '%s'";

    public InvalidManifestException(String path, Throwable throwable) {
        super(String.format(msg, path), throwable);
    }
}
