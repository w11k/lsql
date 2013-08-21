package com.w11k.lsql.converter;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;

import javax.sql.rowset.serial.SerialClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JsonClobConverter implements Converter {

    private final Gson gson;
    private final Class<?> type;

    public JsonClobConverter(Class<?> type) {
        this(null, type);
    }

    public JsonClobConverter(Gson gson, Class<?> type) {
        this.type = type;
        this.gson = gson == null ? new Gson() : gson;
    }

    @Override
    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception {
        String json = gson.toJson(type.cast(val));
        ps.setClob(index, new SerialClob(json.toCharArray()));
    }

    @Override public Object getValueFromResultSet(ResultSet rs, int index) throws Exception {
        String json = CharStreams.toString(rs.getClob(index).getCharacterStream());
        return gson.fromJson(json, type);
    }

}
