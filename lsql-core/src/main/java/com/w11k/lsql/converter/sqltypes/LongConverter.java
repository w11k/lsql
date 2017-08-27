package com.w11k.lsql.converter.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class LongConverter extends Converter {

    public LongConverter() {
        super(Long.class, Types.BIGINT);
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        ps.setLong(index, (Integer) val);
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return rs.getLong(index);

    }
}
