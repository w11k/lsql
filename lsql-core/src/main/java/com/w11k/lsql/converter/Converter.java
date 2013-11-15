package com.w11k.lsql.converter;

import com.w11k.lsql.LSql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Converter {

    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index,
                                    Object val) throws SQLException {
        if (val != null) {
            setValue(lSql, ps, index, val);
        } else {
            ps.setNull(index, getSqlTypeForNullValues());
        }
    }

    public Object getValueFromResultSet(LSql lSql, ResultSet rs, int index) throws SQLException {
        Object value = getValue(lSql, rs, index);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    public int[] getSupportedSqlTypes() {
        throw new RuntimeException("This converter does not specify the supported SQL types.");
    }

    public Class<?> getSupportedJavaClass() {
        throw new RuntimeException("This converter does not specify the supported Java class.");
    }

    protected abstract void setValue(LSql lSql, PreparedStatement ps, int index,
                                  Object val) throws SQLException;

    protected abstract Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException;

    protected int getSqlTypeForNullValues() {
        return getSupportedSqlTypes()[0];
    }


}
