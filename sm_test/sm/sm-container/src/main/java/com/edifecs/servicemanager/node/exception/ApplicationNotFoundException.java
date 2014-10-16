package com.edifecs.servicemanager.node.exception;

import com.edifecs.epp.isc.exception.ServiceException;

import java.io.File;

/**
 * Created by willclem on 2/25/14.
 */
public class ApplicationNotFoundException extends ServiceException {

    public ApplicationNotFoundException(String name, String version, File path) {
        this(name, version, path.getAbsolutePath());
    }

    public ApplicationNotFoundException(String name, String version, String path) {
        super(String.format("Unable to file application '%1$s' with version '%1$s' at '%1$s'", name, version, path));
    }
}
