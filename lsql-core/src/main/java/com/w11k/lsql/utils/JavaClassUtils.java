package com.w11k.lsql.utils;

public class JavaClassUtils {

    public static Class<?> convertPrimitiveClassToWrapperClass(Class<?> clazz) {
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
