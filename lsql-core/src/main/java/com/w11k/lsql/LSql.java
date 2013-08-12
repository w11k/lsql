package com.w11k.lsql;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.w11k.lsql.exceptions.DatabaseAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;

public class LSql {

    private final Map<String, Table> tables = Maps.newHashMap();

    private JavaSqlConverter globalConverter = new JavaSqlConverter();

    private CaseFormat javaCaseFormat = CaseFormat.LOWER_UNDERSCORE;

    private CaseFormat sqlCaseFormat = CaseFormat.UPPER_UNDERSCORE;

    private Callable<Connection> connectionFactory;

    /**
     * @param connectionFactory Factory to get an active JDBC Connection
     */
    public LSql(Callable<Connection> connectionFactory) {
        checkNotNull(connectionFactory);
        this.connectionFactory = connectionFactory;
    }

    // ----- getter/setter -----

    public JavaSqlConverter getGlobalConverter() {
        return globalConverter;
    }

    public void setGlobalConverter(JavaSqlConverter globalConverter) {
        this.globalConverter = globalConverter;
    }

    public CaseFormat getJavaCaseFormat() {
        return javaCaseFormat;
    }

    public void setJavaCaseFormat(CaseFormat javaCaseFormat) {
        this.javaCaseFormat = javaCaseFormat;
    }

    public CaseFormat getSqlCaseFormat() {
        return sqlCaseFormat;
    }

    public void setSqlCaseFormat(CaseFormat sqlCaseFormat) {
        this.sqlCaseFormat = sqlCaseFormat;
    }

    // ----- public -----

    public String identifierSqlToJava(String sqlName) {
        return sqlCaseFormat.to(javaCaseFormat, sqlName);
    }

    public String identifierJavaToSql(String javaName) {
        return javaCaseFormat.to(sqlCaseFormat, javaName);
    }

    public Table table(String tableName) {
        if (!tables.containsKey(tableName)) {
            tables.put(tableName, new Table(this, tableName));
        }
        return tables.get(tableName);
    }

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

    public PreparedStatement prepareStatement(String sqlString) {
        try {
            return getConnection().prepareStatement(sqlString, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
    }

    public void execute(String sql) {
        Statement st = createStatement();
        try {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Query executeQuery(String sql) {
        return new Query(this, sql);
    }

    // ----- private -----

}
