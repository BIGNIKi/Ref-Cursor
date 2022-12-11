package refcursorconnectortests;

import com.refcursorconnector.PostgresService;
import com.refcursorconnector.RefCursorConnectorConfiguration;
import com.refcursorconnector.RefCursorConnectorConnection;
import com.refcursorconnector.RefCursorConnector;
import org.springframework.core.annotation.Order;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class BasicTests {
    private RefCursorConnector connector;

    @BeforeClass
    public void setUpBeforeClass() throws Exception {
        connector = new RefCursorConnector();
        var configuration = new RefCursorConnectorConfiguration();
        configuration.init();

        connector.init(configuration);
        Assert.assertEquals(configuration, connector.getConfiguration());

        var connection = new RefCursorConnectorConnection(configuration);
        PostgresService.initTable(connection.getJbdcConnection());

        Assert.assertNotNull(connector.getConfiguration());
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
