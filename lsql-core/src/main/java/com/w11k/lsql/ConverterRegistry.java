package com.w11k.lsql;

import com.google.common.collect.Maps;

import java.util.Map;

public class ConverterRegistry {

    /**
     * "" (empty string) -> global converter
     * "java:tableName" -> converter for table, lookup by Java name
     * "sql:table_name" -> converter for table, lookup by SQL name
     * "java:tableName:columnName" -> converter for column, lookup by Java name
     * "sql:table_name:column_name" -> converter for column, lookup by SQL name
     */
    private Map<String, JavaSqlConverter> converters = Maps.newHashMap();

    public ConverterRegistry() {
        converters.put("", new JavaSqlConverter());
    }

    public JavaSqlConverter getGlobalConverters() {
        return converters.get("");
    }

    public void setGlobalConverter(JavaSqlConverter converter) {
        converters.put("", converter);
    }

    public void setTableConverter(String javaTableName, JavaSqlConverter converter) {
        converters.put("java:" + javaTableName, converter);
        converters.put("sql:" + converter.identifierJavaToSql(javaTableName), converter);
    }

    public void setColumnConverter(String javaTableName, String javaColumnName, JavaSqlConverter converter) {
        converters.put("java:" + javaTableName + ":" + javaColumnName, converter);
        converters.put("sql:" + converter.identifierJavaToSql(javaTableName) + ":"
                + converter.identifierJavaToSql(javaColumnName), converter);
    }

    public JavaSqlConverter getConverterByJavaName(String javaTableName, String javaColumnName) {
        return getConverterForColumn("java", javaTableName, javaColumnName);
    }

    public JavaSqlConverter getConverterBySqlName(String sqlTableName, String sqlColumnName) {
        return getConverterForColumn("sql", sqlTableName, sqlColumnName);
    }

    private JavaSqlConverter getConverterForColumn(String javaOrSql, String tableName, String columnName) {
        String keyForTable = javaOrSql + ":" + tableName;
        String keyForColumn = javaOrSql + ":" + tableName + ":" + columnName;

        if (converters.containsKey(keyForColumn)) {
            return converters.get(keyForColumn);
        } else if (converters.containsKey(keyForTable)) {
            return converters.get(keyForTable);
        } else {
            return converters.get("");
        }
    }

}
