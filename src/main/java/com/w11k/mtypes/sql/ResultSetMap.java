package com.w11k.mtypes.sql;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class ResultSetMap extends ForwardingMap<String, Object> {

    private final HashMap<String, Object> values = Maps.newHashMap();

    private final HashMap<String, Integer> columnTypes = Maps.newHashMap();

    private final ResultSet resultSet;

    public ResultSetMap(LSql lSql, ResultSet resultSet) {
        try {
            this.resultSet = resultSet;
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                String key = lSql.getJavaSqlStringConversions().identifierSqlToJava(resultSet.getMetaData().getColumnLabel(i));
                values.put(key, getColumnValue(i, resultSet.getMetaData().getColumnType(i)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("values", values).toString();
    }

    @Override
    protected Map<String, Object> delegate() {
        return values;
    }

    private Object getColumnValue(int index, int type) {
        try {
            if (isOneOf(type,
                    Types.BIT,
                    Types.TINYINT,
                    Types.SMALLINT,
                    Types.INTEGER,
                    Types.BIGINT)) {
                return resultSet.getInt(index);
            } else if (isOneOf(type,
                    Types.LONGNVARCHAR,
                    Types.LONGVARCHAR,
                    Types.NCHAR,
                    Types.NVARCHAR,
                    Types.VARCHAR,
                    Types.CLOB)) {
                return resultSet.getString(index);
            } else if (isOneOf(type,
                    Types.FLOAT)) {
                return resultSet.getFloat(index);
            } else {
                throw new RuntimeException("SQL Type " + type + " is not implemented yet.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isOneOf(int toTest, int... possibleValues) {
        for (int possibleValue : possibleValues) {
            if (toTest == possibleValue) {
                return true;
            }
        }
        return false;
    }

}
