package com.w11k.lsql.converter;

import com.google.common.collect.Maps;
import com.w11k.lsql.utils.SqlTypesNames;

import java.util.Map;

import static com.w11k.lsql.utils.JavaClassUtils.convertPrimitiveClassToWrapperClass;

public class ConverterRegistry {

    private final Map<Class<?>, Converter> javaToSqlConverters = Maps.newHashMap();

    private final Map<String, Converter> typeAliasesForConverter = Maps.newHashMap();

    private final Map<Integer, Converter> sqlToJavaConverters = Maps.newHashMap();

    public Converter getConverterForSqlType(int sqlType) {
        Converter converter = this.sqlToJavaConverters.get(sqlType);
        if (converter != null) {
            return converter;
        }
        String msg = "No converter for SQL type '" + SqlTypesNames.getName(sqlType) + "'. ";
        throw new IllegalArgumentException(msg);
    }

    public Converter getConverterForJavaType(Class<?> clazz) {
        clazz = convertPrimitiveClassToWrapperClass(clazz);
        Converter converter = this.javaToSqlConverters.get(clazz);
        if (converter != null) {
            return converter;
        }
        String msg = "No converter for Java type '" + clazz + "'. ";
        throw new IllegalArgumentException(msg);
    }

    public Converter getConverterForAlias(String aliasName) {
        aliasName = aliasName.toLowerCase();
        if (this.typeAliasesForConverter.containsKey(aliasName)) {
            return this.typeAliasesForConverter.get(aliasName);
        }

        throw new IllegalStateException("No converter for alias '" + aliasName + "' registered!");
    }

    public void removeSqlToJavaConverter(int sqlType) {
        this.sqlToJavaConverters.remove(sqlType);
    }

    public void removeJavaToSqlConverter(Class<?> clazz) {
        this.javaToSqlConverters.remove(clazz);
    }

    public void addSqlToJavaConverter(Converter converter, boolean replaceExisting) {
        if (converter.isWriteOnly()) {
            return;
        }

        if (!replaceExisting && this.sqlToJavaConverters.containsKey(converter.getSqlType())) {
            throw new IllegalStateException(
                    "A converter for the SQL type "
                            + SqlTypesNames.getName(converter.getSqlType())
                            + " was already registered. You must explicitly remove it first with " +
                            "removeSqlToJavaConverter(...).");
        }
        this.sqlToJavaConverters.put(converter.getSqlType(), converter);
    }

    public void addJavaToSqlConverter(Converter converter, boolean replaceExisting) {
        if (!replaceExisting && this.javaToSqlConverters.containsKey(converter.getJavaType())) {
            throw new IllegalStateException(
                    "A converter for the Java type "
                            + converter.getJavaType().getCanonicalName()
                            + " was already registered. You must explicitly remove it first with " +
                            "removeJavaToSqlConverter(...).");
        }
        this.javaToSqlConverters.put(converter.getJavaType(), converter);
    }

    public void addConverter(Converter converter, boolean replaceExisting) {
        this.addSqlToJavaConverter(converter, replaceExisting);
        this.addJavaToSqlConverter(converter, replaceExisting);
    }

    public void addTypeAlias(String alias, Converter converter) {
        this.typeAliasesForConverter.put(alias.toLowerCase(), converter);
    }

    public void addTypeAlias(String alias, Class<?> javaType) {
        this.typeAliasesForConverter.put(alias.toLowerCase(), this.getConverterForJavaType(javaType));
    }

}
