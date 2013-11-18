package com.w11k.lsql.converter;

import com.google.gson.reflect.TypeToken;
import com.w11k.lsql.LSql;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectToJsonStringConverter extends Converter {

    private final Type type;

    private final Class<?> clazz;

    public ObjectToJsonStringConverter(Class<?> clazz) {
        this.type = clazz;
        this.clazz = clazz;
    }

    public ObjectToJsonStringConverter(TypeToken typeToken) {
        this.type = typeToken.getType();
        this.clazz = typeToken.getRawType();
    }

    @Override
    public Class<?> getSupportedJavaClass() {
        return clazz.getClass();
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
