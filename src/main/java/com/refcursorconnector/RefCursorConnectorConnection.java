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

    public RefCursorConnectorConnection(RefCursorConnectorConfiguration configuration) throws Exception {
        this.configuration = configuration;

        var postgres = configuration.getPostgresConfiguration();
        this.jbdcConnection = DriverManager.getConnection(postgres.host, postgres.user, postgres.password);

        this.midpointClient = new MidpointClient(configuration.getMidpointConfiguration());
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

    public Connection getJbdcConnection() {
        return jbdcConnection;
    }

    public ResultSet getRefCursor() {
        if (refcursor != null) {
            return refcursor;
        }

        refcursor = PostgresService.getRefCursor(jbdcConnection);
        return refcursor;
    }

    public MidpointClient getMidpointClient() {
        return midpointClient;
    }
}
