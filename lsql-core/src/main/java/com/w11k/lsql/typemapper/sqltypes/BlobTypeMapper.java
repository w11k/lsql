package com.w11k.lsql.typemapper.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.typemapper.TypeMapper;

import java.sql.*;

public class BlobTypeMapper extends TypeMapper {

    public static final BlobTypeMapper INSTANCE = new BlobTypeMapper();

    public BlobTypeMapper() {
        super(
          com.w11k.lsql.Blob.class,
          new int[]{Types.BLOB},
          Types.BLOB
        );
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        com.w11k.lsql.Blob blob = (com.w11k.lsql.Blob) val;
        ps.setBlob(index, blob.getInputStream());
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        Blob blob = rs.getBlob(index);
        return new com.w11k.lsql.Blob(blob.getBinaryStream());
    }
}
