package com.refcursorconnector.config;

/**
 * public final String host = "jdbc:postgresql://localhost:5433/postgres";
 */
public class PostgresConfiguration {
    public static final String HOST = "jdbc:postgresql://postgres-crm:5432/postgres";
    public static final String USER = "postgres";
    public static final String PASSWORD = "postgres";

    private PostgresConfiguration()
    {

    }
}
