package com.w11k.lsql.cli.java;

import com.google.common.collect.Lists;

import java.util.List;

public final class DataClassMeta {

    private final String className;

    private final String packageName;

    private final List<DataClassFieldMeta> fields = Lists.newLinkedList();

    public DataClassMeta(String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
    }

    public void addField(String fieldName, Class<?> fieldType) {
        this.fields.add(new DataClassFieldMeta(fieldName, fieldType));
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<DataClassFieldMeta> getFields() {
        return fields;
    }

    public static class DataClassFieldMeta {

        private final String fieldName;

        private final Class<?> fieldType;

        public DataClassFieldMeta(String fieldName, Class<?> fieldType) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Class<?> getFieldType() {
            return fieldType;
        }
    }
}
