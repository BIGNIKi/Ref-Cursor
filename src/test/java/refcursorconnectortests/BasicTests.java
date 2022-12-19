package refcursorconnectortests;

import com.refcursorconnector.PostgresService;
import com.refcursorconnector.RefCursorConnectorConfiguration;
import com.refcursorconnector.RefCursorConnectorConnection;
import com.refcursorconnector.RefCursorConnector;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashSet;


public class BasicTests {
    private RefCursorConnector connector;

    private Uid testUserUid;

    @BeforeClass
    public void setUpBeforeClass() throws Exception {
        connector = new RefCursorConnector();
        var configuration = new RefCursorConnectorConfiguration();

        connector.init(configuration);
        Assert.assertEquals(configuration, connector.getConfiguration());

        var connection = new RefCursorConnectorConnection(configuration);
        PostgresService.initTable(connection.getJdbcConnection());

        Assert.assertNotNull(connector.getConfiguration());

        testUserUid = null;
    }

    @AfterClass
    public void shutdown() {
        connector.dispose();
    }

    @Test
    public void testScheme() {
        var schema = connector.schema();
        Assert.assertNotNull(schema);
    }

    @Test
    public void testCreate() {
        var attribute = AttributeBuilder.build("testUser", "id", false);
        var set = new HashSet<Attribute>();
        set.add(attribute);

        this.testUserUid = connector.create(ObjectClass.ACCOUNT, set, null);
        Assert.assertNotNull(this.testUserUid);
    }

    @Test
    public void testUpdate() {

    }

    @Test
    public void testDelete() {
        Assert.assertNotNull(testUserUid);
        Assert.assertNotNull(connector);
        connector.delete(ObjectClass.ACCOUNT, testUserUid, null);

        var existed = connector.find(testUserUid);
        Assert.assertNull(existed);
    }
}
