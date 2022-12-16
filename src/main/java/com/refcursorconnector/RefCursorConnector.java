package com.refcursorconnector;

import com.evolveum.midpoint.xml.ns._public.common.common_3.UserType;
import org.apache.commons.lang3.NotImplementedException;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.*;
import java.sql.Types;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@ConnectorClass(
        displayNameKey = "refcursor.connector.display",
        configurationClass = RefCursorConnectorConfiguration.class)
public class RefCursorConnector implements Connector, CreateOp, UpdateOp,  DeleteOp, SchemaOp, TestOp, SyncOp {
    // com.refcursorconnector.RefCursorConnector
    public static final Log LOG = Log.getLog(RefCursorConnector.class);

    private RefCursorConnectorConfiguration configuration;

    private RefCursorConnectorConnection connection;

    private Schema schema;

    private final String KEY_COLUMN = "name";
    private final String PASSWORD_COLUMN = "password";

    @Override
    public Configuration getConfiguration() {
        LOG.info("[Connector] Get configuration");
        return configuration;
    }

    @Override
    public void init(Configuration configuration) {
        LOG.info("[Connector] Starting initialization");
        this.configuration = (RefCursorConnectorConfiguration)configuration;
        this.configuration.init();
        try {
            this.connection = new RefCursorConnectorConnection(this.configuration);
            PostgresService.initTable(getJbdcConnection());
            this.schema = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info("[Connector] Initialization finished");
    }

    @Override
    public void dispose() {
        LOG.info("[Connector] Dispose");
        if (connection != null) {
            connection.dispose();
        }

        connection = null;
        schema = null;
    }

    public UserType find(Uid userUid) {
        try {
            var client = getMidpointClient();
            return client.getUserByOid(userUid.getUidValue());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Uid create(ObjectClass objectClass, Set<Attribute> set, OperationOptions operationOptions) {
        LOG.info("[Connector] Start creating");
        LOG.info("[Connector] attributes are {0}", set);
        try {
            var cursor = getConnection().getRefCursor();
            if (!cursor.next()) {
                throw new Exception("Source database is empty");
            }

            var client = getMidpointClient();
            LOG.info("[Connector] midpoint client {0}", client);

            var name = cursor.getString(2);
            LOG.info("[Connector] user name {0}", name);
            var user = new UserType();
            user.setName(client.createPoly(name));
            var oid = client.addUser(user);
            if (oid == null) {
                throw new Exception("Couldn't add user");
            }

            return new Uid(oid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Uid update(ObjectClass objectClass, Uid uid, Set<Attribute> set, OperationOptions operationOptions) {
        LOG.info("[Connector] Start updating");
        throw new NotImplementedException("Not Implemented");
//        try {
//            var cursor = getConnection().getRefCursor();
//            if (!cursor.next()) {
//                throw new Exception("Source database is empty");
//            }
//
//            var client = getMidpointClient();
//
//            var name = cursor.getString(2);
//            var user = new UserType().name(name);
//            var oid = client.addUser(user);
//            if (oid == null) {
//                throw new Exception("Couldn't update user");
//            }
//
//            return new Uid(oid);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public void delete(ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {
        LOG.info("[Connector] Starting deleting");
        if (uid == null || (uid.getUidValue() == null)) {
            throw new IllegalArgumentException("Uid is empty");
        }

        try {
            var cursor = getConnection().getRefCursor();
            if (!cursor.next()) {
                throw new Exception("Source database is empty");
            }

            var client = getMidpointClient();

            var name = cursor.getString(2);
            //var user = new UserType().name(name);
            var isDeleted = client.deleteUserByOid(uid.getUidValue());
            if (!isDeleted) {
                throw new Exception("User wasn't deleted");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sync(ObjectClass objectClass, SyncToken syncToken, SyncResultsHandler syncResultsHandler, OperationOptions operationOptions) {
        /* Polls the target resource for synchronization events, that is, native changes
            to objects on the target resource. */
    }

    @Override
    public SyncToken getLatestSyncToken(ObjectClass objectClass) {
        return null;
    }

    private RefCursorConnectorConnection getConnection() throws Exception {
        if (connection != null) {
            return connection;
        }

        configuration.validate();
        connection = new RefCursorConnectorConnection(configuration);
        return connection;
    }

    private MidpointClient getMidpointClient() throws Exception {
        return getConnection().getMidpointClient();
    }

    private Connection getJbdcConnection() throws Exception {
        return getConnection().getJbdcConnection();
    }

    @Override
    public Schema schema() {
        try {
            var set = buildSelectBasedAttributeInfos();
            this.schema = buildSchema(set);
            return this.schema;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Schema buildSchema(Set<AttributeInfo> attributes) {
        final var builder = new SchemaBuilder(getClass());
        final var classInfoBuilder = new ObjectClassInfoBuilder();

        classInfoBuilder.setType(ObjectClass.ACCOUNT_NAME);
        classInfoBuilder.addAllAttributeInfo(attributes);

        final var classInfo = classInfoBuilder.build();
        builder.defineObjectClass(classInfo);

        return builder.build();
    }

    private Set<AttributeInfo> buildSelectBasedAttributeInfos() throws Exception {
        var metaData = PostgresService.getScheme(getJbdcConnection());
        if (metaData == null) {
            throw new RuntimeException("SQL query returned null");
        }

        var attributes = new HashSet<AttributeInfo>();
        var columnCount = metaData.getColumnCount();

        for (var i = 1; i <= columnCount; i++) {
            final var columnName = metaData.getColumnName(i);
            final var columnType = metaData.getColumnType(i);

            final var builder = new AttributeInfoBuilder();
            builder.setType(getType(columnType));

            if (columnName.equalsIgnoreCase(KEY_COLUMN)) {
                builder.setName(Name.NAME);
                builder.setRequired(true);
            } else {
                builder.setName(columnName);
                builder.setType(getType(columnType));
            }

            attributes.add(builder.build());
        }

        return attributes;
    }

    @Override
    public void test() {
        LOG.info("[Connector] Start testing");
        try {
            //PostgresService.initTable(getJbdcConnection());
            LOG.info("[Connector] Try create");
            //this.create(null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<?> getType(int typeCode) {
        switch (typeCode) {
            case Types.INTEGER:
                return Integer.class;
            case Types.VARCHAR:
            case Types.BIT:
                return Boolean.class;
            case Types.BOOLEAN:
            default:
                return String.class;
        }
    }
}
