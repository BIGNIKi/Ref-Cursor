package com.refcursorconnector.models;

public class SQLColumn {
    private final String typeName;
    private final Integer typeCode;

    public SQLColumn(String typeName, Integer typeCode) {
        this.typeName = typeName;
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        if (getTypeName() != null) {
            builder.append(String.format("typeName: %s; ", getTypeName()));
        }

        if (getTypeCode() != null) {
            builder.append(String.format("typeCode: %s", getTypeCode()));
        }
        return builder.toString();
    }
}
