package com.refcursorconnector.config;

public class PostgresConfiguration {
    public final String host = "jdbc:postgresql://postgres-crm:5432/postgres";
    // jdbc:postgresql://postgres-crm:5432/postgres // for prod
    // jdbc:postgresql://localhost:5433/postgres // for unitTests
    public final String user = "postgres";
    public final String password = "postgres";
}
