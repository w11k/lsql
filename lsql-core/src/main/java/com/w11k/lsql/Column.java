package com.w11k.lsql;

import com.google.common.base.Optional;
import com.w11k.lsql.converter.Converter;

public class Column {

    private final String columnName;
    private final Table table;
    private Optional<Converter> columnConverter = Optional.absent();

    public Column(Table table, String columnName) {
        this.table = table;
        this.columnName = columnName;
    }

    // ----- getter/setter -----

    public Converter getColumnConverter() {
        return columnConverter.or(table.getTableConverter());
    }

    public void setColumnConverter(Converter columnConverter) {
        this.columnConverter = Optional.fromNullable(columnConverter);
    }

    public String getColumnName() {
        return columnName;
    }

    public Table getTable() {
        return table;
    }

}
