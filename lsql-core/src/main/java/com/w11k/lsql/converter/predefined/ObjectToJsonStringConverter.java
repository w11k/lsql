package com.w11k.lsql.converter.predefined;

import com.fasterxml.jackson.core.type.TypeReference;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ObjectToJsonStringConverter extends Converter {

    private Class<?> clazz;
    private TypeReference typeReference;

    public <A> ObjectToJsonStringConverter(Class<A> clazz, TypeReference typeReference) {
        super(
          clazz,
          new int[]{Types.VARCHAR},
          Types.VARCHAR
        );

        this.clazz = clazz;
        this.typeReference = typeReference;
    }

    @Override
    public boolean isValueValid(Object value) {
        if (value == null && isNullValid()) {
            return true;
        } else if (value == null) {
            return false;
        }

        return clazz.isAssignableFrom(value.getClass());
    }

    @Override
    public void setValue(LSql lSql, PreparedStatement ps, int index,
                         Object val) throws SQLException {
        try {
            String json = lSql.getObjectMapper().writer().writeValueAsString(val);
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
                return lSql.getObjectMapper().readValue(json, typeReference);
            } else {
                return lSql.getObjectMapper().readValue(json, clazz);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
