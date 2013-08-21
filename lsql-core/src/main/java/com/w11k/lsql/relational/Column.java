package com.w11k.lsql.relational;

import com.google.common.base.Optional;
import com.w11k.lsql.converter.DefaultConverters;

public class Column {

    private final String columnName;
    private final Table table;
    private Optional<DefaultConverters> columnConverter = Optional.absent();

    public Column(Table table, String columnName) {
        this.table = table;
        this.columnName = columnName;
    }

    // ----- getter/setter -----

    public void setColumnConverter(DefaultConverters columnConverter) {
        this.columnConverter = Optional.fromNullable(columnConverter);
    }

    public DefaultConverters getColumnConverter() {
        return columnConverter.or(table.getTableConverter());
    }

    public String getColumnName() {
        return columnName;
    }

    public Table getTable() {
        return table;
    }

}
