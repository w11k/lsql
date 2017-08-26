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

    public void removeSqlToJavaConverter(int sqlType) {
        this.defaultSqlToJavaConverters.remove(sqlType);
    }

    public void removeJavaToSqlConverter(Class<?> clazz) {
        this.defaultJavaToSqlConverters.remove(clazz);
    }

    public void addSqlToJavaConverter(Converter converter, boolean replaceExisting) {
        if (!replaceExisting && this.defaultSqlToJavaConverters.containsKey(converter.getSqlType())) {
            throw new IllegalStateException(
                    "A converter for the SQL type "
                            + SqlTypesNames.getName(converter.getSqlType())
                            + " was already registered. You must explicitly remove it first with " +
                            "removeSqlToJavaConverter(...).");
        }
        this.defaultSqlToJavaConverters.put(converter.getSqlType(), converter);
    }

    public void addJavaToSqlConverter(Converter converter, boolean replaceExisting) {
        if (!replaceExisting && this.defaultJavaToSqlConverters.containsKey(converter.getJavaType())) {
            throw new IllegalStateException(
                    "A converter for the Java type "
                            + converter.getJavaType().getCanonicalName()
                            + " was already registered. You must explicitly remove it first with " +
                            "removeJavaToSqlConverter(...).");
        }
        this.defaultJavaToSqlConverters.put(converter.getJavaType(), converter);
    }

    public void addConverter(Converter converter, boolean replaceExisting) {
        this.addSqlToJavaConverter(converter, replaceExisting);
        this.addJavaToSqlConverter(converter, replaceExisting);
    }

}
