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

    private JavaSqlStringConversions javaSqlStringConversions;

    private TypesConverter typesConverter;

    private Callable<Connection> connectionFactory;

    /**
     * @param connectionFactory The factory to get an active JDBC Connection
     */
    public LSql(Callable<Connection> connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.javaSqlStringConversions = new JavaSqlStringConversions();
        this.typesConverter = new TypesConverter();
    }

    public JavaSqlStringConversions getJavaSqlStringConversions() {
        return javaSqlStringConversions;
    }

    public void setJavaSqlStringConversions(JavaSqlStringConversions javaSqlStringConversions) {
        this.javaSqlStringConversions = javaSqlStringConversions;
    }

    public TypesConverter getTypesConverter() {
        return typesConverter;
    }

    public void setTypesConverter(TypesConverter typesConverter) {
        this.typesConverter = typesConverter;
    }

    public Callable<Connection> getConnectionFactory() {
        return connectionFactory;
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

    public <T> List<T> executeQuery(String sql, Function<LMap, T> rowHandler) {
        List<T> list = Lists.newLinkedList();
        Statement st = createStatement();
        try {
            ResultSet resultSet = st.executeQuery(sql);
            while (resultSet.next()) {
                list.add(rowHandler.apply(new LMap(new ResultSetMap(this, resultSet))));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public LMap executeQueryAndGetFirstRow(String sql) {
        final List<LMap> rows = Lists.newLinkedList();
        executeQuery(sql, new Function<LMap, Object>() {
            public Object apply(LMap row) {
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
            resultSet.next();
            LMap row = new LMap(new ResultSetMap(this, resultSet));
            if (row.keySet().size() != 1) {
                throw new RuntimeException("INSERT operation did not return the generated keys.");
            }
            return row.values().toArray()[0];

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
