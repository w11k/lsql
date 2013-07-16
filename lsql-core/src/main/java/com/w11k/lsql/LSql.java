package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;

public class LSql {

    private NamingConventions namingConventions;

    private Callable<Connection> connectionFactory;

    /**
     * @param connectionFactory The factory to get an active JDBC Connection
     */
    public LSql(Callable<Connection> connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.namingConventions = new NamingConventions();
    }

    public NamingConventions getNamingConventions() {
        return namingConventions;
    }

    public void setNamingConventions(NamingConventions namingConventions) {
        this.namingConventions = namingConventions;
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
                list.add(rowHandler.apply(LMap.fromMap(new ResultSetMap(this, resultSet))));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public LMap executeQueryAndGetFirstRow(String sql) {
        Statement st = createStatement();
        try {
            ResultSet resultSet = st.executeQuery(sql);
            resultSet.next();
            return LMap.fromMap(new ResultSetMap(this, resultSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Object> executeInsert(String tableName, Map<String, Object> data) {
        List<String> columns = Lists.newLinkedList();
        List<Object> values = Lists.newLinkedList();
        for (Map.Entry<String, Object> keyValue : data.entrySet()) {
            columns.add(namingConventions.identifierJavaToSql(keyValue.getKey()));
            values.add(escapeJavaObjectForSqlStatement(keyValue.getValue()));
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
            if (resultSet.next()) {
                LMap row = LMap.fromMap(new ResultSetMap(this, resultSet));
                if (row.keySet().size() != 1) {
                    return Optional.absent();
                }
                return Optional.of(row.values().toArray()[0]);
            }
            return Optional.absent();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String escapeJavaObjectForSqlStatement(Object obj) {
        checkNotNull(obj);
        if (obj instanceof String) {
            // TODO check escaping
            return "'" + ((String) obj).replaceAll("'", "\'") + "'";
        } else {
            return obj.toString();
        }
    }

}
