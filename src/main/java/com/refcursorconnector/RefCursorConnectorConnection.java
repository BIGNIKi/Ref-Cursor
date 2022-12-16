package com.refcursorconnector;

import org.identityconnectors.common.logging.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RefCursorConnectorConnection {
    public static final Log LOG = Log.getLog(RefCursorConnectorConnection.class);

    private Connection jbdcConnection;

    private MidpointClient midpointClient;

    private ResultSet refcursor;

    private RefCursorConnectorConfiguration configuration;

    public RefCursorConnectorConnection(RefCursorConnectorConfiguration configuration) throws Exception {
        this.configuration = configuration;

        Class.forName("org.postgresql.Driver");

        var postgres = configuration.getPostgresConfiguration();
        LOG.info("[Connector] init connection " + postgres.host + " : " + postgres.user + " : " + postgres.password);
        this.jbdcConnection = DriverManager.getConnection(postgres.host, postgres.user, postgres.password);

        this.midpointClient = new MidpointClient(configuration.getMidpointConfiguration(), configuration.getMidpointHostname());
        LOG.info("[Connector] midpoint host is {0}", configuration.getMidpointHostname());
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

    /**
     * Получить ref-cursor на текущую бд и таблицу.
     * Если ref-cursor уже создавался, вернет его, если нет - создаст новый
     */
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
