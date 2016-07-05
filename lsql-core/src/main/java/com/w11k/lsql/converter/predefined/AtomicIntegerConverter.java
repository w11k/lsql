package com.w11k.lsql.converter.predefined;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerConverter extends Converter {

    public AtomicIntegerConverter() {
        super(AtomicInteger.class, new int[]{Types.INTEGER}, Types.INTEGER);
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        ps.setInt(index, ((AtomicInteger) val).get());
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return new AtomicInteger(rs.getInt(index));
    }

}
