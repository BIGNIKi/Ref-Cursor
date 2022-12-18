package com.refcursorconnector.exeptions;

public class DataBaseDeleteException  extends Exception {
    public DataBaseDeleteException(String errorMessage) {
        super(errorMessage);
    }
}