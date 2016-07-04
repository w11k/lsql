package com.w11k.lsql.typemapper.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.typemapper.TypeMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class IntTypeMapper extends TypeMapper {

    public static final IntTypeMapper INSTANCE = new IntTypeMapper();

    public IntTypeMapper() {
        super(
          Integer.class,
          new int[]{Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT},
          Types.INTEGER
        );
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        ps.setInt(index, (Integer) val);
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return rs.getInt(index);

    }
}
