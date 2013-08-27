package com.w11k.lsql;

import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.relational.Query;
import com.w11k.lsql.relational.Table;
import com.w11k.lsql.sqlfile.SqlFile;
import com.w11k.lsql.utils.ConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;

public class LSql {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Table> tables = Maps.newHashMap();

    private final BaseDialect dialect;

    private Callable<Connection> connectionFactory;

    /**
     * @param connectionFactory Factory to get an active JDBC Connection
     */
    public LSql(BaseDialect dialect, Callable<Connection> connectionFactory) {
        this.dialect = dialect;
        checkNotNull(connectionFactory);
        this.connectionFactory = connectionFactory;
    }

    // ----- getter/setter -----

    public BaseDialect getDialect() {
        return dialect;
    }

    public Converter getGlobalConverter() {
        return dialect.getConverter();
    }

    public Callable<Connection> getConnectionFactory() {
        return connectionFactory;
    }

    // ----- public -----

    public String identifierSqlToJava(String sqlName) {
        return dialect.identifierSqlToJava(sqlName);
    }

    public String identifierJavaToSql(String javaName) {
        return dialect.identifierJavaToSql(javaName);
    }

    public SqlFile sqlFileRelativeToClass(Class clazz, String fileName) {
        String p = clazz.getPackage().getName();
        p = "/" + p.replaceAll("\\.", "/") + "/";
        InputStream is = clazz.getResourceAsStream(p + fileName);
        return new SqlFile(this, fileName, is);
    }


    public Table table(String tableName) {
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

    public void executeRawSql(String sql) {
        Statement st = ConnectionUtils.createStatement(this);
        try {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Query executeRawQuery(String sql) {
        return new Query(this, sql);
    }

    // ----- private -----

}
