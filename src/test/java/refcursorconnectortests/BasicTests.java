package refcursorconnectortests;


import org.junit.Test;
import refcursorconnector.RefCursorConfiguration;
import refcursorconnector.RefCursorConnector;

import java.sql.SQLException;

public class BasicTests {
    @Test
    public void testCreate() throws SQLException {
        var config = new RefCursorConfiguration();
        var connector = new RefCursorConnector();
        connector.init(config);
        connector.create(null, null, null);
    }
}
