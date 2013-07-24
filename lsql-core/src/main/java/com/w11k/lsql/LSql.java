package com.w11k.lsql;

import com.google.common.collect.Maps;
import com.w11k.lsql.exceptions.DatabaseAccessException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;

public class LSql {

    private Callable<Connection> connectionFactory;

    private final JavaSqlConverter globalConverter = new JavaSqlConverter();

    private final Map<String, Table> tables = Maps.newHashMap();

    /**
     * @param connectionFactory Factory to get an active JDBC Connection
     */
    public LSql(Callable<Connection> connectionFactory) {
        checkNotNull(connectionFactory);
        this.connectionFactory = connectionFactory;
    }

    public ConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }

    // ----- convenience methods for JDBC  -----

    public Connection getConnection() {
        try {
            return connectionFactory.call();
        } catch (Exception e) {
            throw new DatabaseAccessException(e);
        }
    }

    public Statement createStatement() {
        try {
            return getConnection().createStatement();
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
    }

    public Table table(String tableName) {
        if (!tables.containsKey(tableName)) {
            tables.put(tableName, new Table(this, tableName));
        }
        return tables.get(tableName);
    }

    public void execute(String sql) {
        Statement st = createStatement();
        try {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ----- DML and DQL abstractions -----

    public Query executeQuery(String sql) {
        return new Query(this, sql);
    }


}
