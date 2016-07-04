package com.w11k.lsql.typemapper.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.typemapper.TypeMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ByteTypeMapper extends TypeMapper {

    public static final ByteTypeMapper INSTANCE = new ByteTypeMapper();

    public ByteTypeMapper() {
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
