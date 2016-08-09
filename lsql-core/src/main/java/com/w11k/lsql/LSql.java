package com.w11k.lsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.w11k.lsql.dialects.GenericDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql.query.PojoQuery;
import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.statement.AbstractSqlStatement;
import com.w11k.lsql.statement.SqlStatementToPreparedStatement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

    static private ObjectMapper CREATE_DEFAULT_JSON_MAPPER_INSTANCE() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper;
    }

    static public final ObjectMapper OBJECT_MAPPER = CREATE_DEFAULT_JSON_MAPPER_INSTANCE();

    private final Map<String, Table> tables = Maps.newHashMap();

    private final Map<String, PojoTable<?>> pojoTables = Maps.newHashMap();

    private final GenericDialect dialect;

    private final Callable<Connection> connectionProvider;

    private InitColumnCallback initColumnCallback = new InitColumnCallback();

    private ObjectMapper objectMapper = CREATE_DEFAULT_JSON_MAPPER_INSTANCE();

    /**
     * Creates a new LSql instance.
     * <p/>
     * LSql will use the {@link Callable} for obtaining connections.
     *
     * @param dialect            the database dialect
     * @param connectionProvider provider to load a Connection instance
     */
    public LSql(GenericDialect dialect, Callable<Connection> connectionProvider) {
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
    public LSql(GenericDialect dialect, DataSource dataSource) {
        this(dialect, ConnectionProviders.fromDataSource(dataSource));
    }

    public void clearTables() {
        tables.clear();
        pojoTables.clear();
    }

    public GenericDialect getDialect() {
        return dialect;
    }

    public Callable<Connection> getConnectionProvider() {
        return connectionProvider;
    }

    public Iterable<Table> getTables() {
        return Iterables.unmodifiableIterable(tables.values());
    }

    public Iterable<PojoTable<?>> getPojoTables() {
        return Iterables.unmodifiableIterable(pojoTables.values());
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public InitColumnCallback getInitColumnCallback() {
        return this.initColumnCallback;
    }

    public void setInitColumnCallback(InitColumnCallback initColumnCallback) {
        this.initColumnCallback = initColumnCallback;
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
    public synchronized Table table(String tableName) {
        if (!this.tables.containsKey(tableName)) {
            this.tables.put(tableName, new Table(this, tableName));
        }
        return this.tables.get(tableName);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> PojoTable<T> table(String tableName, Class<T> pojoClass) {
        if (!this.pojoTables.containsKey(tableName)) {
            this.pojoTables.put(tableName, new PojoTable<T>(table(tableName), pojoClass));
        }

        PojoTable<T> pojoTable = (PojoTable<T>) this.pojoTables.get(tableName);
        assert pojoTable.getPojoClass().equals(pojoClass);
        return pojoTable;
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
    public RowQuery executeRawQuery(String sql) {
        return new RowQuery(
                this,
                getDialect().getStatementCreator().createPreparedStatement(this, sql, false));
    }

    /**
     * Executes the SQL SELECT string. Useful for simple queries.
     * {@link LSqlFile}s should be used for complex queries.
     *
     * @param sql       the SQL SELECT string
     * @param pojoClass the POJO class
     * @return the Query instance
     */
    public <T> PojoQuery<T> executeRawQuery(String sql, Class<T> pojoClass) {
        return new PojoQuery<T>(
                this,
                getDialect().getStatementCreator().createPreparedStatement(this, sql, false),
                pojoClass);
    }

    public AbstractSqlStatement<RowQuery> executeQuery(String sqlString) {
        final SqlStatementToPreparedStatement stmtToPs =
                new SqlStatementToPreparedStatement(this, "executeQuery", sqlString);

        return new AbstractSqlStatement<RowQuery>(stmtToPs) {
            @Override
            protected RowQuery createQueryInstance(LSql lSql, PreparedStatement ps) {
                return new RowQuery(lSql, ps);
            }
        };
    }

    public String identifierSqlToJava(String sqlName) {
        return getDialect().getIdentifierConverter().sqlToJava(sqlName);
    }

    public String identifierJavaToSql(String javaName) {
        return getDialect().getIdentifierConverter().javaToSql(javaName);
    }

    @Override
    public String toString() {
        return "LSql{" +
                "dialect=" + dialect +
                '}';
    }

}
