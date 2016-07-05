package com.w11k.lsql.converter.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ByteConverter extends Converter {

    public static final ByteConverter INSTANCE = new ByteConverter();

    public ByteConverter() {
        super(
          com.w11k.lsql.Blob.class,
          new int[]{},
          Types.BINARY
        );
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        ps.setByte(index, (Byte) val);
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return rs.getByte(index);
    }
}
