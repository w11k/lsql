package com.w11k.lsql;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.Map;

public class PojoMapper<T> {

    private final Map<String, PropertyDescriptor> propertyDescriptors = Maps.newHashMap();

    private final Constructor<T> constructor;

    public PojoMapper(LSql lSql, Class<T> pojoClass, boolean checkForConverters) {

        // Find constructor
        this.constructor = getConstructor(pojoClass);

        // Extract property names
        PropertyDescriptor[] descs = PropertyUtils.getPropertyDescriptors(pojoClass);
        for (PropertyDescriptor desc : descs) {
            Class<?> declaringClass = desc.getReadMethod().getDeclaringClass();
            if (declaringClass.equals(Object.class)) {
                continue;
            }
            setPropertyAccessible(desc);
            this.propertyDescriptors.put(desc.getName(), desc);

            // Check for a converter registration
            if (checkForConverters) {
                lSql.getDialect().getConverterRegistry()
                        .getConverterForJavaType(desc.getReadMethod().getReturnType());
            }
        }
    }

    public PropertyDescriptor getPropertyDescriptor(String fieldName) {
        return this.propertyDescriptors.get(fieldName);
    }

    public T newInstance() {
        try {
            return this.constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(T instance, String fieldName, Object value) {
        PropertyDescriptor descriptor = this.propertyDescriptors.get(fieldName);
        try {
            descriptor.getWriteMethod().invoke(instance, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Row pojoToRow(T pojo) {
        try {
            Row row = new Row();
            for (PropertyDescriptor desc : this.propertyDescriptors.values()) {
                Object value = desc.getReadMethod().invoke(pojo);
                row.put(desc.getName(), value);
            }
            return row;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public T rowToPojo(Row row) {
        T pojo = newInstance();
        assignRowToPojo(row, pojo);
        return pojo;
    }

    public void assignRowToPojo(Row row, T pojo) {
        for (PropertyDescriptor desc : this.propertyDescriptors.values()) {
            Object value = row.get(desc.getName());
            setValue(pojo, desc.getName(), value);
        }
    }

    private Constructor<T> getConstructor(Class<T> pojoClass) {
        try {
            Constructor<T> constructor = pojoClass.getConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void setPropertyAccessible(PropertyDescriptor desc) {
        try {
            if (!desc.getReadMethod().isAccessible()) {
                desc.getReadMethod().setAccessible(true);
            }
            if (!desc.getWriteMethod().isAccessible()) {
                desc.getWriteMethod().setAccessible(true);
            }
        } catch (SecurityException e) {
            throw new RuntimeException("Property read/write methods must be accessible", e);
        }
    }

}
