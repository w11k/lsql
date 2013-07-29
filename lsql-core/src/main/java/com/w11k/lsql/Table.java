package com.w11k.lsql;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.exceptions.InsertException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class Table {

    private final LSql lSql;

    private final String tableName;

    private Optional<JavaSqlConverter> tableConverter = Optional.absent();

    private final Map<String, Column> columns = Maps.newHashMap();

    public Table(LSql lSql, String tableName) {
        this.lSql = lSql;
        this.tableName = tableName;
    }

    // ----- getter/setter -----

    public void setTableConverter(JavaSqlConverter tableConverter) {
        this.tableConverter = Optional.fromNullable(tableConverter);
    }

    public JavaSqlConverter getTableConverter() {
        return tableConverter.or(lSql.getGlobalConverter());
    }

    public LSql getlSql() {
        return lSql;
    }

    public String getTableName() {
        return tableName;
    }

    // ----- public -----

    public Column column(String columnName) {
        if (!columns.containsKey(columnName)) {
            columns.put(columnName, new Column(this, columnName));
        }
        return columns.get(columnName);
    }

    public Optional<Object> insert(Row row) {
        List<String> columns = Lists.newLinkedList();
        List<Object> values = Lists.newLinkedList();
        for (Map.Entry<String, Object> keyValue : row.entrySet()) {
            String key = keyValue.getKey();
            Object value = keyValue.getValue();

            JavaSqlConverter converter = column(key).getColumnConverter();

            columns.add(converter.identifierJavaToSql(key));
            values.add(converter.javaToSqlStringRepr(value));
        }

        Statement st = lSql.createStatement();
        String sql = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("insert into `");
            sb.append(tableName);
            sb.append("` (`");
            sb.append(Joiner.on("`,`").join(columns));
            sb.append("`) values (");
            sb.append(Joiner.on(",").join(values));
            sb.append(");");

            sql = sb.toString();
            st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = st.getGeneratedKeys();
            if (resultSet.next()) {
                Row rowWithKey = new Row(new ResultSetMap(lSql, resultSet));
                if (rowWithKey.keySet().size() == 0) {
                    return Optional.absent();
                } else if (rowWithKey.keySet().size() > 1) {
                    throw new IllegalStateException("ResultSet for retrieval of the generated " +
                            "ID contains more than one column.");
                }
                return Optional.of(rowWithKey.values().toArray()[0]);
            }
            return Optional.absent();
        } catch (SQLException e) {
            throw new InsertException(e, sql);
        }
    }

}
