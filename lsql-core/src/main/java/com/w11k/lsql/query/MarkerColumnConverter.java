package com.w11k.lsql.query;

import com.w11k.lsql.LSql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class MarkerColumnConverter extends com.w11k.lsql.converter.Converter {


    public MarkerColumnConverter() {
        super(Object.class, new int[]{Types.OTHER}, Types.OTHER);
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        throw new RuntimeException(getClass().getCanonicalName() + " must not be used to set values");
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return rs.getObject(index);
    }
}
