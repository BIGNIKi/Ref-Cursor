package refcursorconnector;

import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.operations.CreateOp;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

public class RefCursorConnector implements Connector, CreateOp {

    private RefCursorConfiguration configuration;
    private RefCursorConnection connection;

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(Configuration configuration) {
        this.configuration = (RefCursorConfiguration)configuration;
        this.connection = new RefCursorConnection(this.configuration);
    }

    @Override
    public void dispose() {
        if (connection == null) {
            return;
        }

        connection.dispose();
        connection = null;
    }

    @Override
    public Uid create(ObjectClass objectClass, Set<Attribute> set, OperationOptions operationOptions) {
        // TODO implement
        throw new NotImplementedException();
    }
}
