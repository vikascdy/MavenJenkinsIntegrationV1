package com.edifecs.epp.exception.test;

import com.edifecs.epp.exception.EppException;

public class FileNotExistException extends EppException {

    @Override
    public String getMessage() {
        return "File {0} does not exist.";
    }

    public FileNotExistException (String[] properties, Throwable throwable) {
        super(properties, throwable);
    }
}
