package com.w11k.lsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql.sqlfile.LSqlFile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main LSql class. Normally, an application only needs to create
 * an instance once. Instances of this class are thread safe.
 * <p/>
 * This class will never call any transaction related methods. Hence the
 * user is responsible to apply transaction boundaries etc. {@link Connection}
 * instances will be obtained with the connection provider or DataSource.
 */
public class LSql {

    public static final ObjectMapper OBJECT_MAPPER = CREATE_JSON_MAPPER_INSTANCE();

    private final Map<String, Table> tables = Maps.newHashMap();

    private final BaseDialect dialect;

    private final Callable<Connection> connectionProvider;

    private InitColumnCallback initColumnCallback = new InitColumnCallback();

    /**
     * Creates a new LSql instance.
     * <p/>
     * LSql will use the {@link Callable} for obtaining connections.
     *
     * @param dialect            the database dialect
     * @param connectionProvider provider to load a Connection instance
     */
    public LSql(BaseDialect dialect, Callable<Connection> connectionProvider) {
        checkNotNull(connectionProvider);
        this.dialect = dialect;
        this.connectionProvider = connectionProvider;

        dialect.setlSql(this);
    }


    /**
     * Creates a new LSql instance.
     * <p/>
     * LSql will use the {@link DataSource} for obtaining connections.
     *
     * @param dialect    the database dialect
     * @param dataSource data source to load a Connection instance
     */
    public LSql(BaseDialect dialect, DataSource dataSource) {
        this(dialect, ConnectionProviders.fromDataSource(dataSource));
    }

    private static ObjectMapper CREATE_JSON_MAPPER_INSTANCE() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper;
    }

    public BaseDialect getDialect() {
        return dialect;
    }

    public Callable<Connection> getConnectionProvider() {
        return connectionProvider;
    }

    public ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public InitColumnCallback getInitColumnCallback() {
        return initColumnCallback;
    }

    public void setInitColumnCallback(InitColumnCallback initColumnCallback) {
        this.initColumnCallback = initColumnCallback;
    }

    public Iterable<Table> getTables() {
        return Iterables.unmodifiableIterable(tables.values());
    }

    /**
     * Loads an SQL file relative to a class.
     *
     * @param clazz    the class from which the basedir will be used
     * @param fileName the SQL file name
     * @return the {@code LSqlFile} instance
     */
    public LSqlFile readSqlFile(Class clazz, String fileName) {
        String p = clazz.getPackage().getName();
        p = "/" + p.replaceAll("\\.", "/") + "/";
        String path = p + fileName;
        return new LSqlFile(this, fileName, path);
    }

    /**
     * Loads a SQL file with the same name and location as the specified class.
     * Instead of '.class', the file extension '.sql' will be used.
     *
     * @param clazz the class which location and name will be used for the lookup
     * @return the {@code LSqlFile} instance
     */
    public LSqlFile readSqlFile(Class<?> clazz) {
        String fileName = clazz.getSimpleName() + ".sql";
        return readSqlFile(clazz, fileName);
    }

    /**
     * Returns a Table instance.
     *
     * @param tableName the table name (Java identifier format)
     * @return the Table instance
     */
    @SuppressWarnings("unchecked")
    public Table table(String tableName) {
        synchronized (tables) {
            if (!tables.containsKey(tableName)) {
                tables.put(tableName, Table.create(this, tableName));
            }
        }

        return tables.get(tableName);
    }

    /**
     * Executes the SQL string.
     *
     * @param sql the SQL string
     */
    public void executeRawSql(String sql) {
        Statement st = getDialect().getStatementCreator().createStatement(this);
        try {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes the SQL SELECT string. Useful for simple queries.
     * {@link LSqlFile}s should be used for complex queries.
     *
     * @param sql the SQL SELECT string
     * @return the Query instance
     */
    public Query executeRawQuery(String sql) {
        return new Query(this, sql);
    }

    @Override
    public String toString() {
        return "LSql{" +
          "dialect=" + dialect +
          '}';
    }

}
