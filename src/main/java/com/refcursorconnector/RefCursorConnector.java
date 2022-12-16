package com.refcursorconnector;

import com.evolveum.midpoint.xml.ns._public.common.common_3.UserType;
import org.apache.commons.lang3.NotImplementedException;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.*;

import java.sql.Types;
import java.sql.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/**
 * ConnectorClass annotation that specifies the connector display name key and configuration class.
 */
@ConnectorClass(displayNameKey = "refcursor.connector.display", configurationClass = RefCursorConnectorConfiguration.class)
public class RefCursorConnector implements Connector, SearchOp<String>, CreateOp, UpdateOp,  DeleteOp, SchemaOp, TestOp, SyncOp {
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
        this.configuration = (RefCursorConnectorConfiguration) configuration;
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
        LOG.info("[Connector sync]");
        /* Polls the target resource for synchronization events, that is, native changes
            to objects on the target resource. */
        /*
        log.info("check the ObjectClass and result handler");
        // Contract tests
        if (oclass == null || (!oclass.equals(ObjectClass.ACCOUNT))) {
            throw new IllegalArgumentException(config.getMessage(MSG_ACCOUNT_OBJECT_CLASS_REQUIRED));
        }
        log.ok("The object class is ok");
        if (handler == null) {
            throw new IllegalArgumentException(config.getMessage(MSG_RESULT_HANDLER_NULL));
        }
        log.ok("The result handles is not null");
        //check if password column is defined in the config
        if (StringUtil.isBlank(config.getChangeLogColumn())) {
            throw new IllegalArgumentException(config.getMessage(MSG_CHANGELOG_COLUMN_BLANK));
        }
        log.ok("The change log column is ok");

        // Names
        final String tblname = config.getTable();
        final String changeLogColumnName = quoteName(config.getChangeLogColumn());
        log.ok("Change log attribute {0} map to column name {1}", config.getChangeLogColumn(), changeLogColumnName);
        final Set<String> columnNames = resolveColumnNamesToGet(options);
        log.ok("Column Names {0} To Get", columnNames);

        final List<OrderBy> orderBy = new ArrayList<>();
        //Add also the token column
        columnNames.add(changeLogColumnName);

        //Set ORDER BY on Sync Data
        String syncOrderByColumnName = changeLogColumnName;
        if (StringUtil.isNotBlank(config.getSyncOrderColumn())) {
            syncOrderByColumnName = config.getSyncOrderColumn();
        }
        Boolean syncOrderByAsc = true;
        if (config.getSyncOrderAsc() != null) {
            syncOrderByAsc = config.getSyncOrderAsc();
        }

        orderBy.add(new OrderBy(syncOrderByColumnName, syncOrderByAsc));
        log.ok("OrderBy {0}", orderBy);

        // The first token is not null set the FilterWhereBuilder
        final FilterWhereBuilder where = new FilterWhereBuilder();
        if (token != null && token.getValue() != null) {
            final Object tokenVal = token.getValue();
            log.info("Sync token is {0}", tokenVal);
            final SQLColumnTypeInfo sqlColumnTypeinfo = getColumnTypeInfo(config.getChangeLogColumn());
            where.addBind(new SQLParam(changeLogColumnName, tokenVal, sqlColumnTypeinfo.getTypeCode(), sqlColumnTypeinfo.getTypeName()), ">");
        }
        final DatabaseQueryBuilder query = new DatabaseQueryBuilder(tblname, columnNames);
        query.setWhere(where);
        query.setOrderBy(orderBy);

        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            openConnection();

            statement = getConn().prepareStatement(query);
            result = statement.executeQuery();
            log.info("execute sync query {0} on {1}", query.getSQL(), oclass);
            while (result.next()) {
                final Map<String, SQLParam> columnValues = getConn().getColumnValues(result);
                log.ok("Column values {0} from sync result set ", columnValues);

                // create the connector object..
                final SyncDeltaBuilder sdb = buildSyncDelta(columnValues);
                if (!handler.handle(sdb.build())) {
                    log.ok("Stop processing of the sync result set");
                    break;
                }
            }
            // commit changes
            log.info("commit sync account");
            commit();
        } catch (SQLException e) {

            SQLUtil.rollbackQuietly(getConn());
            evaluateAndHandleException(e, false, true, false, MSG_CAN_NOT_READ, tblname);
        } finally {
            IOUtil.quietClose(result);
            IOUtil.quietClose(statement);

            closeConnection();
        }
        log.ok("Sync Account committed");
         */
    }

    @Override
    public SyncToken getLatestSyncToken(ObjectClass objectClass) {
        LOG.info("[Connector sync token]");
        return new SyncToken(new Random());
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

    @Override
    public FilterTranslator createFilterTranslator(ObjectClass objectClass, OperationOptions operationOptions) {
        LOG.ok("createFilterTranslator::begin");
        return new AbstractFilterTranslator<String>() {};
    }

    @Override
    public void executeQuery(ObjectClass objectClass, String s, ResultsHandler handler, OperationOptions operationOptions) {
        try (var cursor = getConnection().getRefCursor()) {
            while (cursor.next()) {
                var id = cursor.getString(1);
                var name = cursor.getString(2);
                var object = new ConnectorObjectBuilder()
                        .setUid(new Uid(id))
                        .setName(name)
                        .setObjectClass(ObjectClass.ACCOUNT)
                        .build();

                if (!handler.handle(object)) {
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
