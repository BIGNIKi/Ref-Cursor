package com.refcursorconnector;

import com.evolveum.midpoint.xml.ns._public.common.common_3.UserType;
import com.refcursorconnector.models.SQLColumn;
import org.apache.commons.lang3.NotImplementedException;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.*;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@ConnectorClass(
        displayNameKey = "REFCURSOR_CONNECTOR",
        configurationClass = RefCursorConfiguration.class)
public class RefCursorConnector implements Connector, CreateOp, UpdateOp, DeleteOp, SyncOp, SchemaOp {
    public static final Log LOG = Log.getLog(RefCursorConnector.class);

    private RefCursorConfiguration configuration;

    private RefCursorConnection connection;

    private final String KEY_COLUMN = "name";
    private final String PASSWORD_COLUMN = "password";

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
        LOG.info("attributes are" + set);
        try {
            var cursor = getConnection().getRefCursor();
            if (!cursor.next()) {
                throw new Exception("Source database is empty");
            }

            var client = getMidpointClient();

            var name = cursor.getString(2);
            var user = new UserType().name(name);
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
            var user = new UserType().name(name);
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

    private Schema schema;

    @Override
    public Schema schema() {
        try {
            var attributes = buildSelectBasedAttributeInfos();
            var schema = buildSchema(attributes);
            this.schema = schema;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schema;
    }

    private Schema buildSchema(Set<AttributeInfo> attrInfoSet) {
        // Use SchemaBuilder to build the schema. Currently, only ACCOUNT type is supported.
        final var builder = new SchemaBuilder(getClass());

        final var classInfoBuilder = new ObjectClassInfoBuilder();
        classInfoBuilder.setType(ObjectClass.ACCOUNT_NAME);
        classInfoBuilder.addAllAttributeInfo(attrInfoSet);

        final var classInfo = classInfoBuilder.build();
        builder.defineObjectClass(classInfo);

        return builder.build();
    }

    private Set<AttributeInfo> buildSelectBasedAttributeInfos() throws Exception {
        var sqlScheme = PostgresService.getScheme(getJbdcConnection());
        if (sqlScheme == null) {
            throw new RuntimeException("SQL query returned null");
        }

        var attrInfo = new HashSet<AttributeInfo>();

        var metaData = sqlScheme.getMetaData();
        var columnCount = metaData.getColumnCount();

        var columnSQLTypes = CollectionUtil.<SQLColumn>newCaseInsensitiveMap();

        for (var i = 1; i <= columnCount; i++) {
            final var columnName = metaData.getColumnName(i);
            final var columnType = metaData.getColumnType(i);
            final var columnTypeName = metaData.getColumnTypeName(i);

            columnSQLTypes.put(columnName, new SQLColumn(columnTypeName, columnType));

            if (columnName.equalsIgnoreCase(KEY_COLUMN)) {
                final var builder = new AttributeInfoBuilder();
                builder.setName(Name.NAME);
                builder.setRequired(true);
                attrInfo.add(builder.build());
                continue;
            }

            if (columnName.equalsIgnoreCase(PASSWORD_COLUMN)) {
                attrInfo.add(OperationalAttributeInfos.PASSWORD);
            }
        }

        return attrInfo;
    }
}
