package com.w11k.lsql;

import com.google.common.base.Optional;
import com.w11k.lsql.converter.Converter;

public class Column {

    private final Optional<Table> table;

    private final String columnName;

    private Converter converter;

    /**
     * @param table      The corresponding table. Optional.absent(), if this column is based on a function (e.g. count).
     * @param columnName The name of the column.
     * @param converter  Converter instance used to convert between SQL and Java values.
     */
    public Column(Optional<Table> table, String columnName, Converter converter) {
        this.table = table;
        this.columnName = columnName;
        this.converter = converter;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnNameWithPrefix() {
        if (table.isPresent()) {
            return table.get().getTableName() + "." + columnName;
        } else {
            return columnName;
        }
    }

    public Table getTable() {
        return table.get();
    }

    public boolean hasCorrespondingTable() {
        return table.isPresent();
    }

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }
}
