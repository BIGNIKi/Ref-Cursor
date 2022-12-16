package com.refcursorconnector;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class PostgresService {
    private PostgresService() {
        throw new IllegalStateException("Static class");
    }

    /**
     * Возвращает ref-cursor к текущей БД
     */
    public static ResultSet getRefCursor(Connection jbdcConnection) {
        try(var stmt = jbdcConnection.createStatement()) {
            stmt.execute("CREATE OR REPLACE FUNCTION refcursorfunc() RETURNS refcursor AS '" +
                    " DECLARE " +
                    "    mycurs refcursor; " +
                    " BEGIN " +
                    "    OPEN mycurs FOR SELECT * FROM accounts; " +
                    "    RETURN mycurs; " +
                    " END;' language plpgsql");

            jbdcConnection.setAutoCommit(false);

            try (var func = jbdcConnection.prepareCall("{? = call refcursorfunc() }")) {
                func.registerOutParameter(1, Types.OTHER);
                func.execute();
                return (ResultSet) func.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Инициализирует таблицу с пользователями в БД postgresSQL
     */
    public static void initTable(Connection jbdcConnection) {
        try (var inputStream = PostgresService.class.getResourceAsStream("/create_table.sql")) {
            assert inputStream != null;

            var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            var runner = new ScriptRunner(jbdcConnection);
            runner.runScript(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает схему для активного соединения с БД
     */
    public static ResultSetMetaData getScheme(Connection jbdcConnection) {
        try(var stmt = jbdcConnection.createStatement()) {
            final String SCHEMA_QUERY = "SELECT * FROM accounts WHERE id IS NULL";
            return stmt.executeQuery(SCHEMA_QUERY).getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
