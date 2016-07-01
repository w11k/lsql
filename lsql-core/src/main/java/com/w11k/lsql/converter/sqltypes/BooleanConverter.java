package com.w11k.lsql.converter.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class BooleanConverter extends Converter {

    public static final BooleanConverter INSTANCE = new BooleanConverter();

    public BooleanConverter() {
        super(
          Boolean.class,
          new int[]{Types.BIT, Types.BOOLEAN},
          Types.BIT
        );
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        ps.setBoolean(index, (Boolean) val);
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return rs.getBoolean(index);
    }
}
