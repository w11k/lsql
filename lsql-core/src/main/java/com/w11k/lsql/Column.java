package com.w11k.lsql;

import com.google.common.base.Optional;

public class Column {

    private final String columnName;
    private final Table table;
    private Optional<JavaSqlConverter> columnConverter = Optional.absent();

    public Column(Table table, String columnName) {
        this.table = table;
        this.columnName = columnName;
    }

    // ----- getter/setter -----

    public void setColumnConverter(JavaSqlConverter columnConverter) {
        this.columnConverter = Optional.fromNullable(columnConverter);
    }

    public JavaSqlConverter getColumnConverter() {
        return columnConverter.or(table.getTableConverter());
    }

    public String getColumnName() {
        return columnName;
    }

    public Table getTable() {
        return table;
    }

}
