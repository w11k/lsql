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

    public DataClassFieldMeta addField(String columnInternalSqlName,
                                       String columnsJavaCodeName,
                                       String columnRowKeyName,
                                       Class<?> fieldType) {

        this.checkDuplicate(columnInternalSqlName, columnsJavaCodeName);

        DataClassFieldMeta field = new DataClassFieldMeta(columnInternalSqlName, columnsJavaCodeName, columnRowKeyName, fieldType);
        this.fields.add(field);
        return field;
    }

    private void checkDuplicate(String sqlname, String javaName) {
        for (DataClassFieldMeta field : this.fields) {
            if (field.columnsJavaCodeName.equals(javaName)) {
                throw new RuntimeException(
                        "Duplicate column: (sql:" + sqlname + ", java:" + javaName + ") conflicts with " +
                                "(sql:" + field.columnInternalSqlName + ", java:" + field.columnsJavaCodeName + ")");
            }
        }
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

        private final String columnInternalSqlName;
        private final String columnsJavaCodeName;
        private final String columnRowKeyName;
        private final Class<?> fieldType;

        private boolean nullable = true;

        public DataClassFieldMeta(String columnInternalSqlName,
                                  String columnsJavaCodeName,
                                  String columnRowKeyName,
                                  Class<?> fieldType) {

            this.columnInternalSqlName = columnInternalSqlName;
            this.columnsJavaCodeName = columnsJavaCodeName;
            this.columnRowKeyName = columnRowKeyName;
            this.fieldType = fieldType;
        }

        public String getColumnJavaCodeName() {
            return columnsJavaCodeName;
        }

        public Class<?> getFieldType() {
            return fieldType;
        }

        public String getColumnInternalSqlName() {
            return columnInternalSqlName;
        }

        public String getColumnRowKeyName() {
            return columnRowKeyName;
        }

        public boolean isNullable() {
            return nullable;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }
    }
}
