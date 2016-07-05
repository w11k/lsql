package com.w11k.lsql.converter;

import com.google.common.collect.Maps;
import com.w11k.lsql.utils.SqlTypesNames;

import java.util.Map;

import static com.w11k.lsql.utils.JavaClassUtils.convertPrimitiveClassToWrapperClass;

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


    private final Map<Class<?>, Converter> defaultJavaToSqlConverters = Maps.newHashMap();

    private final Map<Integer, Converter> defaultSqlToJavaConverters = Maps.newHashMap();

    private final Map<JavaSqlTypePair, Converter> javaAndSqlTypePairConverters = Maps.newHashMap();

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
        for (int sqlType : converter.getSqlTypes()) {
            this.defaultSqlToJavaConverters.put(sqlType, converter);
            this.javaAndSqlTypePairConverters.put(
                    new JavaSqlTypePair(converter.getJavaType(), sqlType),
                    converter
            );
        }
        this.defaultJavaToSqlConverters.put(converter.getJavaType(), converter);
    }

}
