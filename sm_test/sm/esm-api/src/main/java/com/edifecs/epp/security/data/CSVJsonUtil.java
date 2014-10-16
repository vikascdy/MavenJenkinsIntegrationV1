package com.edifecs.epp.security.data;

import java.io.Serializable;

public class CSVJsonUtil implements Serializable {
    private String line;
    private boolean valid;
    private String error;
    private int lineNumber;

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public CSVJsonUtil(String line, boolean valid, String error,
                       int lineNumber) {
        super();
        this.line = line;
        this.valid = valid;
        this.error = error;
        this.lineNumber = lineNumber;
    }
}