package refcursorconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RefCursorConnection{
    private Connection jbdcConnection;

    private RefCursorConfiguration configuration;

    private static final String url = "jdbc:postgresql://localhost:5433/postgres";
    private static final String user = "postgres";
    private static final String password = "postgres";

    public RefCursorConnection(RefCursorConfiguration configuration) throws SQLException {
        configuration = configuration;
        jbdcConnection = DriverManager.getConnection(url, user, password);
    }

    public void dispose() {
        // TODO implement
    }

    public Connection getJbdcConnection() {
        return jbdcConnection;
    }


    public void openConnection()  {
        // TODO implement
    }

    public void closeConnection() throws SQLException {
        // TODO implement
    }
}
