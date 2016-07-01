package com.w11k.lsql.converter.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.sql.*;

public class BinaryConverter extends Converter {

    public static final BinaryConverter INSTANCE = new BinaryConverter();

    public BinaryConverter() {
        super(
          com.w11k.lsql.Blob.class,
          new int[]{Types.VARBINARY, Types.BINARY},
          Types.VARBINARY
        );
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        com.w11k.lsql.Blob blob = (com.w11k.lsql.Blob) val;
        ps.setBytes(index, blob.getData());
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return new com.w11k.lsql.Blob(rs.getBytes(index));
    }
}
