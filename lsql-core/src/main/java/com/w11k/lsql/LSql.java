package com.w11k.lsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.dialects.GenericDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql.jdbc.ConnectionUtils;
import com.w11k.lsql.query.PojoQuery;
import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.statement.AbstractSqlStatement;
import com.w11k.lsql.statement.SqlStatementToPreparedStatement;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collections;
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

    static public final ObjectMapper OBJECT_MAPPER = CREATE_DEFAULT_JSON_MAPPER_INSTANCE();

    private final Map<String, Table> tables = Maps.newHashMap();

    private final Map<Class<?>, PojoTable<?>> pojoTables = Maps.newHashMap();

    private final GenericDialect dialect;

    private final Callable<Connection> connectionProvider;

    private final Config config;

    private InitColumnCallback initColumnCallback = new InitColumnCallback();

    private ObjectMapper objectMapper = CREATE_DEFAULT_JSON_MAPPER_INSTANCE();

    /**
     * Creates a new LSql instance.
     * <p/>
     * LSql will use the {@link Callable} for obtaining connections.
     */
    public LSql(Class<? extends Config> configClass, Callable<Connection> connectionProvider) {
        checkNotNull(connectionProvider);
        try {
            this.config = configClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.dialect = config.getDialect();
        this.connectionProvider = connectionProvider;

        dialect.setlSql(this);
    }

    /**
     * Creates a new LSql instance.
     * <p/>
     * LSql will use the {@link DataSource} for obtaining connections.
     */
    public LSql(Class<? extends Config> configClass, DataSource dataSource) {
        this(configClass, ConnectionProviders.fromDataSource(dataSource));
    }

    static private ObjectMapper CREATE_DEFAULT_JSON_MAPPER_INSTANCE() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper;
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
     * @param schemaAndTableName the table name (Java identifier format)
     * @return the Table instance
     */
    public synchronized Table table(String schemaAndTableName) {
        // check existing table names
        if (!schemaAndTableName.contains(".")) {
            for (Table table : tables.values()) {
                if (table.getTableName().equals(schemaAndTableName)) {
                    return table;
                }
            }
        }

        if (!this.tables.containsKey(schemaAndTableName)) {
            Table table = new Table(this, schemaAndTableName);

            this.tables.put(schemaAndTableName, table);
            this.tables.put(table.getSchemaAndTableName(), table);
        }
        return this.tables.get(schemaAndTableName);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> PojoTable<T> table(String tableName, Class<T> pojoClass) {
        if (!this.pojoTables.containsKey(pojoClass)) {
            this.pojoTables.put(pojoClass, new PojoTable<T>(table(tableName), pojoClass));
        }

        PojoTable<T> pojoTable = (PojoTable<T>) this.pojoTables.get(pojoClass);
        assert pojoTable.getPojoClass().equals(pojoClass);
        return pojoTable;
    }

    public void fetchMetaDataForAllTables() throws SQLException {
        Connection con = ConnectionUtils.getConnection(this);
        DatabaseMetaData md = con.getMetaData();

        ResultSet tables = md.getTables(null, null, null, new String[]{"TABLE"});
        while (tables.next()) {
            String sqlTableName = tables.getString(3);
            String javaTableName = identifierSqlToJava(sqlTableName);
            table(javaTableName);
        }
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
        SqlStatementToPreparedStatement st = new SqlStatementToPreparedStatement(this, "<raw>", sql);

        try {
            return new RowQuery(
                    this,
                    st.createPreparedStatement(Collections.<String, Object>emptyMap()),
                    st.getOutConverters());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        SqlStatementToPreparedStatement st = new SqlStatementToPreparedStatement(this, "<raw>", sql);

        try {
            return new PojoQuery<T>(
                    this,
                    st.createPreparedStatement(Collections.<String, Object>emptyMap()),
                    pojoClass,
                    st.getOutConverters());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public AbstractSqlStatement<RowQuery> executeQuery(String sqlString) {
        final SqlStatementToPreparedStatement stmtToPs =
                new SqlStatementToPreparedStatement(this, "executeQuery", sqlString);

        return new AbstractSqlStatement<RowQuery>(stmtToPs) {
            @Override
            protected RowQuery createQueryInstance(LSql lSql, PreparedStatement ps, Map<String, Converter> outConverters) {
                return new RowQuery(lSql, ps, outConverters);
            }
        };
    }

    public String identifierSqlToJava(String sqlName) {
        return getDialect().getIdentifierConverter().sqlToJava(sqlName);
    }

    public String identifierJavaToSql(String javaName) {
        return getDialect().getIdentifierConverter().javaToSql(javaName);
    }

    public Converter getConverterForSqlType(int sqlType) {
        return getDialect().getConverterRegistry().getConverterForSqlType(sqlType);
    }

    public Converter getConverterForJavaType(Class<?> clazz) {
        return getDialect().getConverterRegistry().getConverterForJavaType(clazz);
    }

    public Converter getConverterForAlias(String alias) {
        return getDialect().getConverterRegistry().getConverterForAlias(alias);
    }

    @Override
    public String toString() {
        return "LSql{" +
                "dialect=" + dialect +
                '}';
    }

    Config getConfig() {
        return config;
    }
}
