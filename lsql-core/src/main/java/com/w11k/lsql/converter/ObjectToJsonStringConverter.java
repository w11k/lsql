package com.w11k.lsql.converter;

import com.w11k.lsql.LSql;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ObjectToJsonStringConverter extends Converter {

    private Class<?> clazz;
    private TypeReference typeReference;

    public <A> ObjectToJsonStringConverter(Class<A> clazz) {
        this.clazz = clazz;
    }

    public ObjectToJsonStringConverter(TypeReference typeReference) {
        this.typeReference = typeReference;
    }

    @Override
    public Class<?> getSupportedJavaClass() {
        return clazz == null ? typeReference.getType().getClass() : clazz;
    }

    @Override
    public int[] getSupportedSqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public boolean isValueValid(Object value) {
        // TODO
        return true;
    }

    @Override
    public void setValue(LSql lSql, PreparedStatement ps, int index,
                         Object val) throws SQLException {
        try {
            String json = lSql.getJsonMapper().writer().writeValueAsString(val);
            ps.setString(index, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        String json = rs.getString(index);
        try {
            if (typeReference != null) {
                return lSql.getJsonMapper().readValue(json, typeReference);
            } else {
                return lSql.getJsonMapper().readValue(json, clazz);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
