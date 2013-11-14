package com.w11k.lsql.converter;

import com.w11k.lsql.LSql;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectToJsonStringConverter extends Converter {

    private final Type type;

    public ObjectToJsonStringConverter(Type type) {
        this.type = type;
    }

    @Override
    public void setValue(LSql lSql, PreparedStatement ps, int index,
                                    Object val) throws SQLException {
        String json = lSql.getGson().toJson(val);
        ps.setString(index, json);
    }

    @Override
    public Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        String json = rs.getString(index);
        return lSql.getGson().fromJson(json, type);
    }

}
