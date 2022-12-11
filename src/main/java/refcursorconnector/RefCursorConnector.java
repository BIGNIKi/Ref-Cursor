package refcursorconnector;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.SyncOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;

import java.sql.*;
import java.util.Set;

@ConnectorClass(
        displayNameKey = "REFCURSOR_CONNECTOR",
        configurationClass = RefCursorConfiguration.class)
public class RefCursorConnector implements Connector, CreateOp, UpdateOp, DeleteOp, SyncOp {
    public static final Log LOG = Log.getLog(RefCursorConnector.class);

    private RefCursorConfiguration configuration;

    private RefCursorConnection connection;

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(Configuration configuration) {
        this.configuration = (RefCursorConfiguration)configuration;
        try {
            this.connection = new RefCursorConnection(this.configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        //var cursor = getRefCursor();

        // TODO Get ref cursor data (hardcode)

        try {
            var cursor = PostgresService.getRefCursor(getJbdcConnection());
            while (cursor.next()) {
                var id = cursor.getString(1);
                var name = cursor.getString(2);

                System.out.println(id + ": " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Uid("12");
    }



    @Override
    public Uid update(ObjectClass objectClass, Uid uid, Set<Attribute> set, OperationOptions operationOptions) {
        // TODO Находим id шник из базы в мидпоинте и обновляем значения по этому id шнику

        try {
            var cursor = PostgresService.getRefCursor(getJbdcConnection());
            while(cursor.next()) {
                var id = cursor.getInt(1);
                var name = cursor.getString(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void delete(ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {
        // TODO В мидпоинте удаляем те строки, которые в нашей базе помечены как deleted
        try {
            var cursor = PostgresService.getRefCursor(getJbdcConnection());
            while(cursor.next()) {
                var isDeleted = cursor.getBoolean(3);
                if (isDeleted) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sync(ObjectClass objectClass, SyncToken syncToken, SyncResultsHandler syncResultsHandler, OperationOptions operationOptions) {

    }

    @Override
    public SyncToken getLatestSyncToken(ObjectClass objectClass) {
        return null;
    }

    private RefCursorConnection getConnection() throws Exception {
        if (connection != null) {
            return connection;
        }

        configuration.validate();
        connection = new RefCursorConnection(configuration);
        return connection;
    }

    private MidpointClient getMidpointClient() throws Exception {
        return getConnection().getMidpointClient();
    }

    private Connection getJbdcConnection() throws Exception {
        return getConnection().getJbdcConnection();
    }

    private void closeConnection() {
        try {
            getConnection().closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
