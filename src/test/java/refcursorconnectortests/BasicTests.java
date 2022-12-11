package refcursorconnectortests;

import org.springframework.core.annotation.Order;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import refcursorconnector.*;

public class BasicTests {
    private RefCursorConnector connector;

    @BeforeClass
    public void setUpBeforeClass() throws Exception {
        var configuration = new RefCursorConfiguration();

        var connection = new RefCursorConnection(configuration);
        PostgresService.initTable(connection.getJbdcConnection());

        connector = new RefCursorConnector();
        connector.init(configuration);
    }

    @AfterClass
    public void shutdown() {
        connector.dispose();
    }

    @Test
    @Order(1)
    public void testScheme() {
        var schema = connector.schema();
        Assert.assertNotNull(schema);
    }

    @Test
    @Order(2)
    public void testCreate() throws Exception {
        //connector.create(null, null, null);
    }
}
