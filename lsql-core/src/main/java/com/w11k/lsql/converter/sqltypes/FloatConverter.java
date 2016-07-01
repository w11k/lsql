package com.w11k.lsql.converter.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class FloatConverter extends Converter {

    public static final FloatConverter INSTANCE = new FloatConverter();

    public FloatConverter() {
        super(
          Float.class,
          new int[]{Types.FLOAT},
          Types.FLOAT
        );
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        ps.setFloat(index, (Integer) val);
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return rs.getFloat(index);

    }
}
