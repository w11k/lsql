package com.w11k.lsql;

public class ResultSetColumnMeta {

    private final String sqlTableName;
    private final String javaTableName;
    private final String sqlColumnName;
    private final String javaColumnName;
    private final JavaSqlConverter converter;

    public String getSqlTableName() {
        return sqlTableName;
    }

    public String getJavaTableName() {
        return javaTableName;
    }

    public String getSqlColumnName() {
        return sqlColumnName;
    }

    public String getJavaColumnName() {
        return javaColumnName;
    }

    public JavaSqlConverter getConverter() {
        return converter;
    }

    public ResultSetColumnMeta(String sqlTableName, String javaTableName, String sqlColumnName, String javaColumnName, JavaSqlConverter converter) {
        this.sqlTableName = sqlTableName;
        this.javaTableName = javaTableName;
        this.sqlColumnName = sqlColumnName;
        this.javaColumnName = javaColumnName;
        this.converter = converter;
    }
}
