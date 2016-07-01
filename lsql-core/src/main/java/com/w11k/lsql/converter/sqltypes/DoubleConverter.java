package com.w11k.lsql.converter.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class DoubleConverter extends Converter {

    public static final DoubleConverter INSTANCE = new DoubleConverter();

    public DoubleConverter() {
        super(
          Double.class,
          new int[]{Types.DOUBLE, Types.REAL, Types.DECIMAL, Types.NUMERIC},
          Types.DOUBLE
        );
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        ps.setDouble(index, (Double) val);
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return rs.getDouble(index);

    }
}
