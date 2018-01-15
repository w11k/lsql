package com.w11k.lsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.dialects.GenericDialect;
import com.w11k.lsql.dialects.StatementCreator;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql.jdbc.ConnectionUtils;
import com.w11k.lsql.query.PojoQuery;
import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.statement.AbstractSqlStatement;
import com.w11k.lsql.statement.SqlStatementToPreparedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterators.getLast;

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

    private final Logger logger = LoggerFactory.getLogger(getClass());

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

    public Class<? extends GenericDialect> getDialectClass() {
        return dialect.getClass();
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
            this.tables.put(table.getSchemaAndTableName(), table);
            schemaAndTableName = table.getSchemaAndTableName();
        }

        return this.tables.get(schemaAndTableName);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> PojoTable<T> table(String tableName, Class<T> pojoClass) {
        if (!this.pojoTables.containsKey(pojoClass)) {
            this.pojoTables.put(pojoClass, new PojoTable<>(table(tableName), pojoClass));
        }

        return (PojoTable<T>) this.pojoTables.get(pojoClass);
    }

    public void fetchMetaDataForAllTables() throws SQLException {
        Connection con = ConnectionUtils.getConnection(this);
        DatabaseMetaData md = con.getMetaData();

        ResultSet tables = md.getTables(null, null, null, new String[]{"TABLE"});
        try {
            while (tables.next()) {
                String sqlTableName = tables.getString(3);
                String javaTableName = identifierSqlToJava(sqlTableName);
                Table table = table(javaTableName);
                this.logger.debug("Reading table " + table.getSchemaAndTableName());
            }
        } catch (Exception e) {
            this.logger.warn(e.getMessage());
        }
    }


    /**
     * Executes the SQL string.
     *
     * @param sql the SQL string
     */
    public void executeRawSql(String sql) {
        Statement st = this.dialect.getStatementCreator().createStatement(this);
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
     * Deprecated: Use LSql#createSqlStatement() instead.
     *
     * @param sql the SQL SELECT string
     * @return the Query instance
     */
    @Deprecated
    public RowQuery executeRawQuery(String sql) {
        SqlStatementToPreparedStatement st = new SqlStatementToPreparedStatement(this, "LSql", "executeRawQuery", "", sql);

        try {
            return new RowQuery(
                    this,
                    st.createPreparedStatement(Collections.emptyMap(), null),
                    st.getOutConverters());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes the SQL SELECT string. Useful for simple queries.
     * {@link LSqlFile}s should be used for complex queries.
     *
     * Deprecated: Use LSql#createSqlStatement() instead.
     *
     * * @param sql       the SQL SELECT string
     * @param pojoClass the POJO class
     * @return the Query instance
     */
    @Deprecated
    public <T> PojoQuery<T> executeRawQuery(String sql, Class<T> pojoClass) {
        SqlStatementToPreparedStatement st = new SqlStatementToPreparedStatement(this, "LSql", "executeRawQuery", "", sql);

        try {
            return new PojoQuery<>(
                    this,
                    st.createPreparedStatement(Collections.emptyMap(), null),
                    pojoClass,
                    st.getOutConverters());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes the SQL statement.
     *
     * @param sqlString the SQL SELECT string
     */
    public AbstractSqlStatement<RowQuery> createSqlStatement(String sqlString) {
        return this.createSqlStatement(sqlString, "LSql", "createSqlStatement");
    }

    /**
     * Executes the SQL statement.
     *
     * @param sqlString the SQL SELECT string
     */
    public AbstractSqlStatement<RowQuery> createSqlStatement(String sqlString, String sourceName, String stmtName) {
        final SqlStatementToPreparedStatement stmtToPs =
                new SqlStatementToPreparedStatement(this, sourceName, stmtName, "", sqlString);

        return new AbstractSqlStatement<RowQuery>(stmtToPs) {
            @Override
            protected RowQuery createQueryInstance(LSql lSql, PreparedStatement ps, Map<String, Converter> outConverters) {
                return new RowQuery(lSql, ps, outConverters);
            }
        };
    }

    public String identifierSqlToJava(String sqlName) {
        return this.dialect.getIdentifierConverter().sqlToJava(sqlName);
    }

    public String identifierJavaToSql(String javaName) {
        return this.dialect.getIdentifierConverter().javaToSql(javaName);
    }

    public CaseFormat getJavaCaseFormat() {
        return this.dialect.getIdentifierConverter().getToCaseFormat();
    }

    public Converter getConverterForSqlType(int sqlType) {
        return this.dialect.getConverterRegistry().getConverterForSqlType(sqlType);
    }

    public Converter getConverterForJavaType(Class<?> clazz) {
        return this.dialect.getConverterRegistry().getConverterForJavaType(clazz);
    }

    public Converter getConverterForAlias(String alias) {
        return this.dialect.getConverterRegistry().getConverterForAlias(alias);
    }

    public String getSqlSchemaAndTableNameFromResultSetMetaData(
            ResultSetMetaData metaData, int columnIndex) throws SQLException {

        return dialect.getSqlSchemaAndTableNameFromResultSetMetaData(metaData, columnIndex);
    }

    public Optional<Object> extractGeneratedPk(Table table, ResultSet resultSet) throws SQLException {
        return dialect.extractGeneratedPk(table, resultSet);
    }

    public StatementCreator getStatementCreator() {
        return dialect.getStatementCreator();
    }

    public String getSqlColumnNameFromResultSetMetaData(
            ResultSetMetaData metaData, int columnIndex) throws SQLException {

        return dialect.getSqlColumnNameFromResultSetMetaData(metaData, columnIndex);
    }

    public Converter getConverterForTableColumn(String schemaAndTableName, String javaColumnName, int sqlType) {
        Map<String, Map<String, Converter>> configuredConverters = this.config.getConverters();

        Map<String, Converter> convertersForTable = configuredConverters.get(schemaAndTableName);

        // if no converters were found for this schemaAndTable,
        // check if only the tableName was used
        if (convertersForTable == null && schemaAndTableName.contains(".")) {
            String tableName = getLast(Splitter.on(".").split(schemaAndTableName).iterator());
            convertersForTable = configuredConverters.get(tableName);
        }

        if (convertersForTable != null) {
            Converter converterForColumn = convertersForTable.get(javaColumnName);
            if (converterForColumn != null) {
                return converterForColumn;
            }
        }

        return this.getConverterForSqlType(sqlType);
    }

    public boolean isUseColumnTypeForConverterLookupInQueries() {
        return config.isUseColumnTypeForConverterLookupInQueries();
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "LSql{" +
                "dialect=" + dialect +
                '}';
    }

//    GenericDialect getDialect() {
//        return dialect;
//    }

//    Config getConfig() {
//        return config;
//    }
}
