package com.refcursorconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RefCursorConnectorConnection {
    private Connection jbdcConnection;

    private MidpointClient midpointClient;

    private ResultSet refcursor;

    private RefCursorConnectorConfiguration configuration;

    public RefCursorConnectorConnection(RefCursorConnectorConfiguration configuration) {
        this.configuration = configuration;
    }

    public void dispose()  {
        if (midpointClient != null) {
            midpointClient.dispose();
            midpointClient = null;
        }

        if (jbdcConnection != null) {
            try {
                jbdcConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            jbdcConnection = null;
        }
    }

    public Connection getJbdcConnection() throws SQLException {
        if (jbdcConnection != null) {
            return jbdcConnection;
        }

        var postgres = configuration.getPostgresConfiguration();
        jbdcConnection = DriverManager.getConnection(postgres.host, postgres.user, postgres.password);
        return jbdcConnection;
    }

    public ResultSet getRefCursor() {
        if (refcursor != null) {
            return refcursor;
        }

        refcursor = PostgresService.getRefCursor(jbdcConnection);
        return refcursor;
    }

    public MidpointClient getMidpointClient() throws Exception {
        if (midpointClient != null) {
            return midpointClient;
        }

        midpointClient = new MidpointClient(configuration.getMidpointConfiguration());
        return midpointClient;
    }
}
