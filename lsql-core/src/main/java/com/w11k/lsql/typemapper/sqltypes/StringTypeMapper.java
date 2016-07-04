package com.w11k.lsql.typemapper.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.typemapper.TypeMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class StringTypeMapper extends TypeMapper {

    public static final StringTypeMapper INSTANCE = new StringTypeMapper();

    public StringTypeMapper() {
        super(
          String.class,
          new int[]{Types.CHAR, Types.VARCHAR, Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR},
          Types.CHAR
        );
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        ps.setString(index, val.toString());

    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }
}
