package com.refcursorconnector.exeptions;

public class DatabaseAddException extends Exception {
    public DatabaseAddException(String errorMessage) {
        super(errorMessage);
    }
}