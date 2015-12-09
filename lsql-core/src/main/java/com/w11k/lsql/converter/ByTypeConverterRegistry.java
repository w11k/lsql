package com.w11k.lsql.converter;

import com.google.common.collect.Maps;

import java.util.Map;

public class ByTypeConverterRegistry {

    private final Map<Class<?>, Converter> javaValueToSqlConverters = Maps.newHashMap();

    private final Map<Integer, Converter> sqlValueToJavaConverters = Maps.newHashMap();

    public Converter getConverterForSqlType(int sqlType) {
        return sqlValueToJavaConverters.get(sqlType);
    }

    public Converter getConverterForJavaValue(Object value) {
        return javaValueToSqlConverters.get(value.getClass());
    }

    public void addConverter(Converter converter) {
        for (int sqlType : converter.getSqlTypes()) {
            sqlValueToJavaConverters.put(sqlType, converter);
        }
        javaValueToSqlConverters.put(converter.getJavaType(), converter);
    }

    public Map<Class<?>, Converter> getJavaValueToSqlConverters() {
        return javaValueToSqlConverters;
    }

    public Map<Integer, Converter> getSqlValueToJavaConverters() {
        return sqlValueToJavaConverters;
    }
}
