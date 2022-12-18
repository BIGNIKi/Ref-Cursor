package com.refcursorconnector.exeptions;

public class DataBaseEmptyException extends Exception {
    public DataBaseEmptyException(String errorMessage) {
        super(errorMessage);
    }
}
