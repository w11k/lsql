package com.w11k.lsql;

import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql.relational.Query;
import com.w11k.lsql.relational.Table;
import com.w11k.lsql.sqlfile.SqlFile;
import com.w11k.lsql.utils.ConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConcurrentMap<String, Table> tables = Maps.newConcurrentMap();

    private final BaseDialect dialect;

    private Callable<Connection> connectionProvider;

    /**
     * Creates a new LSql instance.
     * <p/>
     * LSql will use the {@link Callable} for obtaining connections.
     *
     * @param dialect            the database dialect
     * @param connectionProvider provider to get a Connection instance
     */
    public LSql(BaseDialect dialect, Callable<Connection> connectionProvider) {
        this.dialect = dialect;
        checkNotNull(connectionProvider);
        this.connectionProvider = connectionProvider;
    }

    /**
     * Creates a new LSql instance.
     * <p/>
     * LSql will use the {@link DataSource} for obtaining connections.
     *
     * @param dialect    the database dialect
     * @param dataSource data source to get a Connection instance
     */
    public LSql(BaseDialect dialect, DataSource dataSource) {
        this(dialect, ConnectionProviders.fromDataSource(dataSource));
    }

    // ----- getter/setter -----

    public BaseDialect getDialect() {
        return dialect;
    }

    public Converter getGlobalConverter() {
        return dialect.getConverter();
    }

    public Callable<Connection> getConnectionProvider() {
        return connectionProvider;
    }

    // ----- public -----

    /**
     * Load an SQL file relative to a class.
     *
     * @param clazz    the class from which the basedir will be used
     * @param fileName the SQL file name
     * @return the SqlFile instance
     */
    public SqlFile sqlFileRelativeToClass(Class clazz, String fileName) {
        String p = clazz.getPackage().getName();
        p = "/" + p.replaceAll("\\.", "/") + "/";
        InputStream is = clazz.getResourceAsStream(p + fileName);
        return new SqlFile(this, fileName, is);
    }

    /**
     * Returns a Table instance.
     *
     * @param tableName the table name (Java identifier format)
     * @return the Table instance
     */
    public synchronized Table table(String tableName) {
        if (!tables.containsKey(tableName)) {
            tables.put(tableName, new Table(this, tableName));
        }
        return tables.get(tableName);
    }

    @Override
    public String toString() {
        return "LSql{" +
                "dialect=" + dialect +
                '}';
    }

    // ----- execute SQL methods -----

    /**
     * Executes the SQL string. Useful for DML and DDL statements.
     *
     * @param sql the SQL string
     */
    public void executeRawSql(String sql) {
        Statement st = ConnectionUtils.createStatement(this);
        try {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes the SQL SELECT string. Useful for testing and very simple
     * queries. {@link SqlFile}s should be used for complex queries.
     *
     * @param sql the SQL SELECT string
     * @return the Query instance
     */
    public Query executeRawQuery(String sql) {
        return new Query(this, sql);
    }

}
