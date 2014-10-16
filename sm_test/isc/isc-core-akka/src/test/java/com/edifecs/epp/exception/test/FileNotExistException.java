package com.edifecs.epp.exception.test;

import com.edifecs.epp.exception.EppException;

public class FileNotExistException extends EppException {

    @Override
    public String getMessage() {
        return "File (configuration.xml) does not exist.";
    }

    public FileNotExistException (String[] properties) {
	   super(properties);
   }
}
