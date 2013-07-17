package com.w11k.relda;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ResultSetMap extends ForwardingMap<String, Object> {

    private final HashMap<String, Object> values = Maps.newHashMap();

    public ResultSetMap(LSql lSql, ResultSet resultSet) {
        try {
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                String key = lSql.getJavaSqlConverter().identifierSqlToJava(resultSet.getMetaData().getColumnLabel(i));
                Object value = lSql.getJavaSqlConverter().getColumnValue(resultSet, i);
                values.put(key, value);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
