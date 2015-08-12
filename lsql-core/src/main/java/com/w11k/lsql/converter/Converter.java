package com.w11k.lsql.converter;

import com.google.common.base.Optional;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// TODO getSupportedJavaClass is no longer option. Check!

public abstract class Converter {

    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index,
                                    Object val, int sqlTypeForNullValue) throws SQLException {
        if (val != null) {

            if (convertWithJacksonOnWrongType()
                    && getSupportedJavaClass().isPresent()
                    && !val.getClass().equals(getSupportedJavaClass().get())) {
                // If type is not correct, try to convert
                val = convertValueToTargetType(val);
            }

            setValue(lSql, ps, index, val);
        } else {
            ps.setNull(index, sqlTypeForNullValue);
        }
    }

    public Object convertValueToTargetType(Object val) {
        return Row.fromKeyVals("v", val).getAs(getSupportedJavaClass().get(), "v");
    }

    public Object getValueFromResultSet(LSql lSql, ResultSet rs, int index) throws SQLException {
        rs.getObject(index);
        if (rs.wasNull()) {
            return null;
        }

        return getValue(lSql, rs, index);
    }

    public boolean isValueValid(Object value) {
        if (value == null) {
            return isNullValid();
        }
        if (getSupportedJavaClass().isPresent()) {
            return getSupportedJavaClass().get().isAssignableFrom(value.getClass());
        } else {
            return true;
        }
    }

    public int[] getSupportedSqlTypes() {
        throw new RuntimeException("This converter does not specify the supported SQL types.");
    }

    // TODO maybe remove the Optional, since we now have Object as default
    public Optional<? extends Class<?>> getSupportedJavaClass() {
        return Optional.of(Object.class);
    }

    public int getSqlTypeForNullValues() {
        return getSupportedSqlTypes()[0];
    }

    public boolean convertWithJacksonOnWrongType() {
        return true;
    }

    protected abstract void setValue(LSql lSql, PreparedStatement ps, int index,
                                     Object val) throws SQLException;

    protected abstract Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException;

    protected boolean isNullValid() {
        return true;
    }

    @Override
    public String toString() {
        if (getSupportedJavaClass().isPresent()) {
            return "Converter{Java type=" + getSupportedJavaClass().get() + "}";
        }
        return "Converter{}";
    }
}
