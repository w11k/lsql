package com.w11k.lsql;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import com.w11k.lsql.exceptions.SelectException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

public class ResultSetMap extends ForwardingMap<String, Object> {

    private final Map<String, Object> values = Maps.newHashMap();

    public ResultSetMap(LSql lSql, ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String sqlTable = metaData.getTableName(i);
                String sqlColumn = metaData.getColumnLabel(i);

                JavaSqlConverter converter = lSql.getConverterRegistry().getConverterBySqlName(sqlTable, sqlColumn);

                String javaColumn = converter.identifierSqlToJava(sqlColumn);
                Object javaValue = converter.getColumnValue(resultSet, i);
                values.put(javaColumn, javaValue);
            }
        } catch (SQLException e) {
            throw new SelectException(e);
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
