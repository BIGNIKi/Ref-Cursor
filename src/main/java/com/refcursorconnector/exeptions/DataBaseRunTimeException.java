package com.refcursorconnector.exeptions;

public class DataBaseRunTimeException extends RuntimeException {
    public DataBaseRunTimeException(Exception e) {
        super(e);
    }
}