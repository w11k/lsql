package com.weiglewilczek.lsql;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class LSql {

    private JavaSqlStringConversions javaSqlStringConversions = new JavaSqlStringConversions();

    private Callable<Connection> connectionFactory;

    public JavaSqlStringConversions getJavaSqlStringConversions() {
        return javaSqlStringConversions;
    }

    public void setJavaSqlStringConversions(JavaSqlStringConversions javaSqlStringConversions) {
        this.javaSqlStringConversions = javaSqlStringConversions;
    }

    public Callable<Connection> getConnectionFactory() {
        return connectionFactory;
    }

    public LSql(Callable<Connection> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Connection getConnection() {
        try {
            return connectionFactory.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Statement createStatement() {
        try {
            return getConnection().createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

<<<<<<< HEAD:src/main/java/com/weiglewilczek/lsql/LSql.java
    public SelectStatement select() {
        return new SelectStatement(this);
    }

    public SelectStatement select(String columns) {
        return new SelectStatement(this, columns);
=======
    public <T> List<T> executeQuery(String sql, Function<Row, T> rowHandler) {
        List<T> list = Lists.newLinkedList();
        Statement st = createStatement();
        try {
            ResultSet resultSet = st.executeQuery(sql);
            while (resultSet.next()) {
                list.add(rowHandler.apply(new Row(this, resultSet)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Map<String, Object> executeQueryAndGetFirstRow(String sql) {
        final List<Map<String, Object>> rows = Lists.newLinkedList();
        executeQuery(sql, new Function<Row, Object>() {
            @Override
            public Object apply(Row row) {
                row.fetchAllValues();
                rows.add(row);
                return null;
            }
        });
        return rows.get(0);
    }

    public Object executeInsert(String tableName, Map<String, Object> data) {
        List<String> columns = Lists.newLinkedList();
        List<Object> values = Lists.newLinkedList();
        for (Map.Entry<String, Object> keyValue : data.entrySet()) {
            columns.add(javaSqlStringConversions.identifierJavaToSql(keyValue.getKey()));
            values.add(javaSqlStringConversions.escapeJavaObjectForSqlStatement(keyValue.getValue()));
        }

        Statement st = createStatement();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("insert into `");
            sb.append(tableName);
            sb.append("` (`");
            sb.append(Joiner.on("`,`").join(columns));
            sb.append("`) values (");
            sb.append(Joiner.on(",").join(values));
            sb.append(");");

            String sql = sb.toString();
            st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = st.getGeneratedKeys();
            Row row = new Row(this, resultSet);
            if (row.keySet().size() != 1) {
                throw new RuntimeException("INSERT operation did not return the generated keys.");
            }
            resultSet.next();
            return row.values().toArray()[0];

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
>>>>>>> origin/master:src/main/java/de/romanroe/lsql/LSql.java
    }

}
