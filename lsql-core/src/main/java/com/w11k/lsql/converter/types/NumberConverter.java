package com.w11k.lsql.converter.types;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class NumberConverter extends Converter {

    public NumberConverter() {
        super(Number.class, Types.OTHER);
        setWriteOnly(true);
    }

    @Override
    public void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        if (val instanceof Double) {
            ps.setDouble(index, (Double) val);
        } else if (val instanceof Float) {
            ps.setFloat(index, (Float) val);
        } else if (val instanceof Long) {
            ps.setLong(index, (Long) val);
        } else if (val instanceof Integer) {
            ps.setInt(index, (Integer) val);
        }
    }

    @Override
    public Object getValue(LSql lSql, ResultSet rs, int index) {
        throw new IllegalStateException(getClass().getName() + " is write-only");
    }

}
