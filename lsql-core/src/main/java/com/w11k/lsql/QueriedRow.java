package com.w11k.lsql;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.w11k.lsql.exceptions.SelectException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class QueriedRow extends Row {

    private final Map<String, Object> values = Maps.newHashMap();
    
    private final Map<String, Column> columns = Maps.newHashMap();

    public QueriedRow(Map<String, Query.ResultSetColumn> meta, ResultSet resultSet) {
        try {
            for (String name : meta.keySet()) {
                Query.ResultSetColumn resultSetColumn = meta.get(name);
                JavaSqlConverter columnConverter = resultSetColumn.column.getColumnConverter();
                Object value = columnConverter.getColumnValue(resultSet, resultSetColumn.index);
                values.put(name, value);
                columns.put(name, resultSetColumn.column);
            }
        } catch (SQLException e) {
            throw new SelectException(e);
        }
    }
    
    public Map<String, Row> groupByTables() {
        Map<String, Row> byTables = Maps.newHashMap();
        for (String key : columns.keySet()) {
            Column column = columns.get(key);
            String tableName = column.getTable().getTableName();
            if (!byTables.containsKey(tableName)) {
                byTables.put(tableName, new Row());
            }
            Row row = byTables.get(tableName);
            row.put(column.getColumnName(), get(key));
        }
        return byTables;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("columns", values).toString();
    }

    @Override
    protected Map<String, Object> delegate() {
        return values;
    }

}
