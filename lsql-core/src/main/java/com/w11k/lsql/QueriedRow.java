package com.w11k.lsql;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.QueryException;

import java.sql.ResultSet;
import java.util.Map;

public class QueriedRow extends Row {

    private final Map<String, Object> values = Maps.newHashMap();

    private final Map<String, Column> columns = Maps.newHashMap();

    private final LSql lSql;

    public QueriedRow(LSql lSql, Map<String, Query.ResultSetColumn> meta, ResultSet resultSet) {
        this.lSql = lSql;
        try {
            for (String name : meta.keySet()) {
                Query.ResultSetColumn resultSetColumn = meta.get(name);
                Converter columnConverter = resultSetColumn.column.getColumnConverter();
                Object value = columnConverter
                        .getValueFromResultSet(lSql, resultSet, resultSetColumn.index);
                values.put(name, value);
                columns.put(name, resultSetColumn.column);
            }
        } catch (Exception e) {
            throw new QueryException(e);
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

    public <A> A toObject(Class<A> clazz) {
        Gson gson = lSql.getGson();
        String json = gson.toJson(this);
        return gson.fromJson(json, clazz);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("values", values).toString();
    }

    @Override
    protected Map<String, Object> delegate() {
        return values;
    }

}
