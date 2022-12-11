package refcursorconnectortests;

import org.junit.Test;
import refcursorconnector.*;

public class BasicTests {

    @Test
    public void testCreate() throws Exception {
        var configuration = new RefCursorConfiguration();

        var connection = new RefCursorConnection(configuration);
        PostgresService.initTable(connection.getJbdcConnection());

        var connector = new RefCursorConnector();
        connector.init(configuration);

        connector.create(null, null, null);

        connector.dispose();
    }
}
