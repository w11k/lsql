package com.w11k.lsql.converter;

import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectToJsonStringConverter implements Converter {

    private final Gson gson;
    private final Class<?> type;

    public ObjectToJsonStringConverter(Class<?> type) {
        this(null, type);
    }

    public ObjectToJsonStringConverter(Gson gson, Class<?> type) {
        this.type = type;
        this.gson = gson == null ? new Gson() : gson;
    }

    @Override
    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws SQLException {
        String json = gson.toJson(type.cast(val));
        ps.setString(index, json);
    }

    @Override public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
        String json = rs.getString(index);
        return gson.fromJson(json, type);
    }

}
