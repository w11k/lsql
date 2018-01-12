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
        this.addField(fieldName, fieldName, fieldType);
    }

    public void addField(String fieldName, String fieldKeyInMap, Class<?> fieldType) {
        this.fields.add(new DataClassFieldMeta(fieldName, fieldKeyInMap, fieldType));
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

        private final String fieldKeyName;

        private final Class<?> fieldType;

        public DataClassFieldMeta(String fieldName, String fieldKeyName, Class<?> fieldType) {
            this.fieldName = fieldName;
            this.fieldKeyName = fieldKeyName;
            this.fieldType = fieldType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Class<?> getFieldType() {
            return fieldType;
        }

        public String getFieldKeyName() {
            return fieldKeyName;
        }
    }
}
