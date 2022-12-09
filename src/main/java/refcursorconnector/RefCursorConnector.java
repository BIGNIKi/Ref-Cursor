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
        } catch (SQLException e) {
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
        LOG.info("attributes {0}", set);
        String name = (String) set.toArray(new Attribute[0])[0].getValue().get(0);
        LOG.info("name {0}", name);

        var cursor = getRefCursor();

        // TODO Get ref cursor data (hardcode)
//        while (cursor.next()) {
//            cursor.getString(1)
//        }

        try {
            openConnection();
            // TODO place data to midpoint
            var pstmt = getConnection().getJbdcConnection().prepareStatement(sql, bld.getParams());
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return new Uid(name);
    }

    private RefCursorConnection getConnection() throws SQLException {
        if (connection != null) {
            return connection;
        }

        configuration.validate();
        connection = new RefCursorConnection(configuration);
        return connection;
    }

    private void openConnection() throws SQLException {
        getConnection().openConnection();
    }

    private void closeConnection() {
        try {
            getConnection().closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ResultSet getRefCursor() {
        try {
            var stmt = connection.getJbdcConnection().createStatement();
            stmt.execute("CREATE OR REPLACE FUNCTION refcursorfunc() RETURNS refcursor AS '" +
                    " DECLARE " +
                    "    mycurs refcursor; " +
                    " BEGIN " +
                    "    OPEN mycurs FOR SELECT * FROM accounts; " +
                    "    RETURN mycurs; " +
                    " END;' language plpgsql");
            stmt.close();

            connection.getJbdcConnection().setAutoCommit(false);

            CallableStatement func = connection.getJbdcConnection().prepareCall("{? = call refcursorfunc() }");
            func.registerOutParameter(1, Types.OTHER);
            func.execute();
            return (ResultSet) func.getObject(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Uid update(ObjectClass objectClass, Uid uid, Set<Attribute> set, OperationOptions operationOptions) {
        // TODO Находим id шник из базы в мидпоинте и обновляем значения по этому id шнику
        return null;
    }

    @Override
    public void delete(ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {
        // TODO В мидпоинте удаляем те строки, которые в нашей базе помечены как deleted
    }

    @Override
    public void sync(ObjectClass objectClass, SyncToken syncToken, SyncResultsHandler syncResultsHandler, OperationOptions operationOptions) {

    }

    @Override
    public SyncToken getLatestSyncToken(ObjectClass objectClass) {
        return null;
    }
}
