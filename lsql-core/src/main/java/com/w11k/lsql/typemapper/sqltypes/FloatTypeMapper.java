package com.w11k.lsql.typemapper.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.typemapper.TypeMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class FloatTypeMapper extends TypeMapper {

    public static final FloatTypeMapper INSTANCE = new FloatTypeMapper();

    public FloatTypeMapper() {
        super(
          Float.class,
          new int[]{Types.FLOAT},
          Types.FLOAT
        );
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        ps.setFloat(index, (Integer) val);
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        return rs.getFloat(index);

    }
}
