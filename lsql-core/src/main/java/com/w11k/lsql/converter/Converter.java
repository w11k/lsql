package com.w11k.lsql.converter;

import com.w11k.lsql.LSql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public abstract class Converter {

    private final Class<?> javaType;

    private final int[] sqlTypes;

    private final int sqlTypeForNullValues;

    public Converter(Class<?> javaType, int[] sqlTypes, int sqlTypeForNullValues) {
        this.javaType = javaType;
        this.sqlTypes = sqlTypes;
        this.sqlTypeForNullValues = sqlTypeForNullValues;
    }

    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        if (val != null) {
//            val = convertValueToTargetType(lSql, val);
            failOnWrongValueType(val);
            setValue(lSql, ps, index, val);
        } else {
            ps.setNull(index, sqlTypeForNullValues);
        }
    }

    public void failOnWrongValueType(Object val) {
        if (!isValueValid(val)) {
            throw new IllegalArgumentException(
                    "value '" + val + "' of type '" + val.getClass().getCanonicalName() + "' " +
                            "does not match expected type " +
                            "'" + getJavaType().getCanonicalName() + "'"
            );
        }
    }

//    public Object convertValueToTargetType(LSql lSql, Object val) {
//        return lSql.getObjectMapper().convertValue(val, javaType);
//    }

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

//        if (getSupportedJavaClass().isPresent()) {
        return javaType.isAssignableFrom(value.getClass());
//        } else {
//            return true;
//        }
    }

//    public boolean supportsSqlType(int sqlType) {
//        for (int type : this.sqlTypes) {
//            if (sqlType == type) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public int[] getSupportedSqlTypes() {
//        throw new RuntimeException("This converter does not specify the supported SQL types.");
//    }

//    public Optional<? extends Class<?>> getSupportedJavaClass() {
//        return Optional.of(Object.class);
//    }

//    public int getSqlTypeForNullValues() {
//        return getSupportedSqlTypes()[0];
//    }

//    public boolean convertWithJacksonOnWrongType() {
//        return true;
//    }

    public boolean supportsSqlType(int sqlType) {
        for (int type : this.sqlTypes) {
            if (type == sqlType) {
                return true;
            }
        }
        return false;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public int[] getSqlTypes() {
        return sqlTypes;
    }

    public int getSqlTypeForNullValues() {
        return sqlTypeForNullValues;
    }

    @Override
    public String toString() {
        return "Converter{" +
                "javaType=" + javaType +
                ", sqlTypes=" + Arrays.toString(sqlTypes) +
                ", sqlTypeForNullValues=" + sqlTypeForNullValues +
                '}';
    }

    protected abstract void setValue(LSql lSql, PreparedStatement ps, int index,
                                     Object val) throws SQLException;

    protected abstract Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException;

    protected boolean isNullValid() {
        return true;
    }
}
