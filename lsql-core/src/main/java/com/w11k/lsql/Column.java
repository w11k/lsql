package com.w11k.lsql;

import com.w11k.lsql.converter.Converter;

import javax.annotation.Nullable;

public class Column {

    private final String columnName;

    private final Table table;

    private Converter converter;

    public Column(@Nullable Table table, String columnName, Converter converter) {
        this.table = table;
        this.columnName = columnName;
        this.converter = converter;
    }

    public String getColumnName() {
        return columnName;
    }

    public Table getTable() {
        return table;
    }

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }
}
