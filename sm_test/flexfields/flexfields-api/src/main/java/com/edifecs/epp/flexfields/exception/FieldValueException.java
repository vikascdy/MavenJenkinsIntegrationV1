package com.edifecs.epp.flexfields.exception;

/**
 * Created by sandeep.kath on 5/10/2014.
 */
public class FieldValueException extends Exception {
    String message;
    public FieldValueException() {
        super();
    }

    public FieldValueException(String... parameters) {
        String message = "Invalid value for ";
        for(String parameter: parameters) {
            message += parameter +" ";
        }
       this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
