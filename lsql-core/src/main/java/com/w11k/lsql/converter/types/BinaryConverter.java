package com.w11k.lsql.converter.types;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.sql.*;

public class BinaryConverter extends Converter {

    public static int[] SQL_TYPES = new int[]{
            Types.VARBINARY, Types.BINARY
    };


    public BinaryConverter(int sqlType) {
        super(com.w11k.lsql.Blob.class, sqlType);
    }

    @Override
    public void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        com.w11k.lsql.Blob blob = (com.w11k.lsql.Blob) val;
        ps.setBytes(index, blob.getData());
    }

    @Override
    public Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return new com.w11k.lsql.Blob(rs.getBytes(index));
    }
}
