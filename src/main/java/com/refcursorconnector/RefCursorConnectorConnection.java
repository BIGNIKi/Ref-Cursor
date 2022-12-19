package com.refcursorconnector;

import com.refcursorconnector.config.PostgresConfiguration;
import org.identityconnectors.common.logging.Log;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RefCursorConnectorConnection {
    public static final Log LOG = Log.getLog(RefCursorConnectorConnection.class);

    private Connection jdbcConnection;

    private MidpointClient midpointClient;

    private ResultSet refcursor;


    public RefCursorConnectorConnection(RefCursorConnectorConfiguration configuration) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        LOG.info("[Connector] init connection " + PostgresConfiguration.host + " : " + PostgresConfiguration.user + " : " + PostgresConfiguration.password);
        try {
            this.jdbcConnection = DriverManager.getConnection(PostgresConfiguration.host, PostgresConfiguration.user, PostgresConfiguration.password);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try {
            this.midpointClient = new MidpointClient();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        LOG.info("[Connector] midpoint host is {0}", configuration.getMidpointHostname());
    }

    public void dispose() {
        if (midpointClient != null) {
            midpointClient.dispose();
            midpointClient = null;
        }

        if (jdbcConnection != null) {
            try {
                jdbcConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
            jdbcConnection = null;
        }
    }

    public Connection getJdbcConnection() {
        return jdbcConnection;
    }

    /**
     * Получить ref-cursor на текущую бд и таблицу.
     * Если ref-cursor уже создавался, вернет его, если нет - создаст новый
     */
    public ResultSet getRefCursor() {
        if (refcursor != null) {
            return refcursor;
        }

        refcursor = PostgresService.getRefCursor(jdbcConnection);
        return refcursor;
    }

    public MidpointClient getMidpointClient() {
        return midpointClient;
    }
}
