package refcursorconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RefCursorConnection  {
    private Connection jbdcConnection;

    private MidpointClient midpointClient;

    private RefCursorConfiguration configuration;

    public RefCursorConnection(RefCursorConfiguration configuration) throws Exception {
        configuration = configuration;

        var postgres = configuration.getPostgresConfiguration();
        jbdcConnection = DriverManager.getConnection(postgres.host, postgres.user, postgres.password);

        midpointClient = new MidpointClient(configuration.getMidpointConfiguration());
    }

    public void dispose() {
        // TODO implement
        midpointClient.dispose();
    }

    public Connection getJbdcConnection() {
        return jbdcConnection;
    }

    public MidpointClient getMidpointClient() {
        return midpointClient;
    }

    public void openConnection()  {
    }

    public void closeConnection() throws SQLException {
        // TODO implement
    }
}
