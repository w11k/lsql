package com.w11k.lsql.typemapper;

import com.google.common.collect.Maps;
import com.w11k.lsql.utils.SqlTypesNames;

import java.util.Map;

public class ConverterRegistry {

    private static class JavaSqlTypePair {

        private final Class<?> clazz;

        private final int sqlType;

        public JavaSqlTypePair(Class<?> clazz, int sqlType) {
            this.clazz = clazz;
            this.sqlType = sqlType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            JavaSqlTypePair that = (JavaSqlTypePair) o;
            return this.sqlType == that.sqlType && this.clazz.equals(that.clazz);
        }

        @Override
        public int hashCode() {
            int result = this.clazz.hashCode();
            result = 31 * result + this.sqlType;
            return result;
        }

        @Override
        public String toString() {
            return "JavaSqlTypePair{" +
                    "clazz=" + this.clazz +
                    ", sqlType=" + this.sqlType +
                    '}';
        }
    }


    private final Map<Class<?>, TypeMapper> defaultJavaToSqlConverters = Maps.newHashMap();

    private final Map<Integer, TypeMapper> defaultSqlToJavaConverters = Maps.newHashMap();

    private final Map<JavaSqlTypePair, TypeMapper> javaAndSqlTypePairConverters = Maps.newHashMap();

    public TypeMapper getConverterForSqlType(int sqlType) {
        TypeMapper typeMapper = this.defaultSqlToJavaConverters.get(sqlType);
        if (typeMapper != null) {
            return typeMapper;
        }
        String msg = "No converter for SQL type '" + SqlTypesNames.getName(sqlType) + "'. ";
        throw new IllegalArgumentException(msg);
    }

    public TypeMapper getConverterForJavaType(Class<?> clazz) {
        clazz = convertPrimitiveClassToWrapperClass(clazz);
        TypeMapper typeMapper = this.defaultJavaToSqlConverters.get(clazz);
        if (typeMapper != null) {
            return typeMapper;
        }
        String msg = "No converter for Java type '" + clazz + "'. ";
        throw new IllegalArgumentException(msg);
    }

    public void addConverter(TypeMapper typeMapper) {
        for (int sqlType : typeMapper.getSqlTypes()) {
            this.defaultSqlToJavaConverters.put(sqlType, typeMapper);
            this.javaAndSqlTypePairConverters.put(
                    new JavaSqlTypePair(typeMapper.getJavaType(), sqlType),
                    typeMapper
            );
        }
        this.defaultJavaToSqlConverters.put(typeMapper.getJavaType(), typeMapper);
    }

    private Class<?> convertPrimitiveClassToWrapperClass(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz.equals(Integer.TYPE)) {
                clazz = Integer.class;
            } else if (clazz.equals(Short.TYPE)) {
                clazz = Short.class;
            } else if (clazz.equals(Long.TYPE)) {
                clazz = Long.class;
            } else if (clazz.equals(Float.TYPE)) {
                clazz = Float.class;
            } else if (clazz.equals(Double.TYPE)) {
                clazz = Double.class;
            } else if (clazz.equals(Byte.TYPE)) {
                clazz = Byte.class;
            } else if (clazz.equals(Boolean.TYPE)) {
                clazz = Boolean.class;
            } else if (clazz.equals(Character.TYPE)) {
                clazz = Character.class;
            }
        }
        return clazz;
    }

}
