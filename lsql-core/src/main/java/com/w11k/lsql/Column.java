package com.w11k.lsql;

import com.google.common.base.Optional;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.validation.AbstractValidationError;
import com.w11k.lsql.validation.StringTooLongError;
import com.w11k.lsql.validation.TypeError;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class Column {

    private final Optional<Table> table;

    private final String columnName;

    private final int columnSize;

    private Converter converter;

    /**
     * @param table      The corresponding table. Optional.absent(), if this column is based on a function (e.g. count).
     * @param columnName The name of the column.
     * @param converter  Converter instance used to convert between SQL and Java values.
     * @param columnSize The maximum column size. -1 if not applicable.
     */
    public Column(Optional<Table> table, String columnName, Converter converter, int columnSize) {
        this.table = table;
        this.columnName = columnName;
        this.converter = converter;
        this.columnSize = columnSize;
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

    public Optional<? extends AbstractValidationError> validateValue(Object value) {
        Class<?> targetType = converter.getSupportedJavaClass();
        if (!converter.isValueValid(value)) {
            return of(new TypeError(getTable().getTableName(), columnName, converter
                    .getSupportedJavaClass().getSimpleName(), value.getClass().getSimpleName()));
        }

        if (columnSize != -1 && String.class.isAssignableFrom(targetType)) {
            String string = (String) value;
            if (string.length() > columnSize) {
                return of(new StringTooLongError(
                        getTable().getTableName(), columnName, columnSize, string.length()));
            }
        }

        return absent();
    }
}
