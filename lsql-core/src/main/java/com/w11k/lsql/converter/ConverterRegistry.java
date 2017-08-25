package com.w11k.lsql.converter;

import com.google.common.collect.Maps;
import com.w11k.lsql.utils.SqlTypesNames;

import java.util.Map;

import static com.w11k.lsql.utils.JavaClassUtils.convertPrimitiveClassToWrapperClass;

public class ConverterRegistry {

    private final Map<Class<?>, Converter> defaultJavaToSqlConverters = Maps.newHashMap();

    private final Map<Integer, Converter> defaultSqlToJavaConverters = Maps.newHashMap();

    public Converter getConverterForSqlType(int sqlType) {
        Converter converter = this.defaultSqlToJavaConverters.get(sqlType);
        if (converter != null) {
            return converter;
        }
        String msg = "No converter for SQL type '" + SqlTypesNames.getName(sqlType) + "'. ";
        throw new IllegalArgumentException(msg);
    }

    public Converter getConverterForJavaType(Class<?> clazz) {
        clazz = convertPrimitiveClassToWrapperClass(clazz);
        Converter converter = this.defaultJavaToSqlConverters.get(clazz);
        if (converter != null) {
            return converter;
        }
        String msg = "No converter for Java type '" + clazz + "'. ";
        throw new IllegalArgumentException(msg);
    }

    public void addConverter(Converter converter) {
        this.defaultSqlToJavaConverters.put(converter.getSqlType(), converter);
        this.defaultJavaToSqlConverters.put(converter.getJavaType(), converter);
    }

}
