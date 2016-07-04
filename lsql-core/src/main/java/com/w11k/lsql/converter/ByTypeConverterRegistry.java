package com.w11k.lsql.converter;

import com.google.common.collect.Maps;

import java.util.Map;

public class ByTypeConverterRegistry {

    private final Map<Class<?>, Converter> javaValueToSqlConverters = Maps.newHashMap();

    private final Map<Integer, Converter> sqlValueToJavaConverters = Maps.newHashMap();

    public Converter getConverterForSqlType(int sqlType) {
        return this.sqlValueToJavaConverters.get(sqlType);
    }

    public Converter getConverterForJavaType(Class<?> clazz) {
        Converter converter = this.javaValueToSqlConverters.get(clazz);
        if (converter != null) {
            return converter;
        }

        String msg = "No converter for type '" + clazz + "'. ";
        throw new IllegalArgumentException(msg);
    }

    public void addConverter(Converter converter) {
        addConverter(converter.getJavaType(), converter);
    }

    public void addConverter(Class<?> javaType, Converter converter) {
        for (int sqlType : converter.getSqlTypes()) {
            this.sqlValueToJavaConverters.put(sqlType, converter);
        }
        this.javaValueToSqlConverters.put(javaType, converter);
    }

}
