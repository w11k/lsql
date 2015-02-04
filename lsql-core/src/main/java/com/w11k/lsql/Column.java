package com.w11k.lsql;

import com.google.common.base.Optional;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.validation.AbstractValidationError;
import com.w11k.lsql.validation.StringTooLongError;
import com.w11k.lsql.validation.TypeError;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class Column {

    private final String columnName;

    private final int columnSize;

    private final int sqlType;

    private Optional<? extends Table<?>> table;

    private Converter converter;

    private boolean ignored = false;

    public static Column create(Table<?> table,
                                    String columnName,
                                    int sqlType,
                                    Converter converter,
                                    int columnSize) {

        Optional<? extends Table<?>> tableOptional = Optional.fromNullable(table);
        return new Column(tableOptional, columnName, sqlType, converter, columnSize);
    }

    /**
     * @param table      The corresponding table. Optional.absent(), if this column is
     *                   based on a function (e.g. count) or used for a raw list.
     * @param columnName The name of the column.
     * @param sqlType    The java.sql.Types value
     * @param converter  Converter instance used to convert between SQL and Java values.
     * @param columnSize The maximum column size. -1 if not applicable.
     */
    public Column(Optional<? extends Table<?>> table, String columnName, int sqlType, Converter converter, int columnSize) {
        this.table = table;
        this.columnName = columnName;
        this.sqlType = sqlType;
        this.converter = converter;
        this.columnSize = columnSize;
    }

    public String getColumnName() {
        return columnName;
    }

    public Optional<? extends Table> getTable() {
        return table;
    }

    public Optional<String> getTableName() {
        if (table.isPresent()) {
            return of(table.get().getTableName());
        } else {
            return absent();
        }
    }

    public int getSqlType() {
        return sqlType;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    public Optional<? extends AbstractValidationError> validateValue(Object value) {
        if (!converter.isValueValid(value)) {
            return of(new TypeError(getTableName().get(), columnName, converter
                    .getSupportedJavaClass().get().getSimpleName(), value.getClass().getSimpleName()));
        }

        Class<?> targetType = converter.getSupportedJavaClass().get();
        if (columnSize != -1 && String.class.isAssignableFrom(targetType)) {
            String string = (String) value;
            if (string != null && string.length() > columnSize) {
                return of(new StringTooLongError(
                        getTableName().get(), columnName, columnSize, string.length()));
            }
        }

        return absent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Column that = (Column) o;
        if (!columnName.equals(that.columnName)) {
            return false;
        }

        return table.equals(that.table);
    }

    @Override
    public int hashCode() {
        int result = table.hashCode();
        result = 31 * result + columnName.hashCode();
        return result;
    }


}
