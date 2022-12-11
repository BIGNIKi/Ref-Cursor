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
            builder.append(getTypeName());
            builder.append("=");
        }

        if (getTypeCode() != null) {
            builder.append(getTypeCode());
            builder.append("=");
        }
        return builder.toString();
    }
}
