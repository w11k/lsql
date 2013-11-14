package com.w11k.lsql;

import com.google.common.base.Optional;
import com.w11k.lsql.converter.Converter;

public class LinkedRow extends Row {

    private Optional<Table> table;

    public LinkedRow(Table table) {
        this.table = Optional.fromNullable(table);
    }

    public Optional<Table> getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = Optional.fromNullable(table);
    }

    @Override
    public Object put(String key, Object value) {
        if (!table.get().getColumns().containsKey(key)) {
            throw new IllegalArgumentException(
                    "Column '" + key + "' does not exist in table '" +
                            table.get().getTableName() + "'.");
        }

        Converter converter = table.get().column(key).getConverter();
        Class<?> targetType = converter.getSupportedJavaClass();
        if (!targetType.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Column '" + key + "' in table '" +
                    table.get().getTableName() + "' requires a value of type '" + targetType.getName() +
                    "', got '" + value.getClass().getName() + "' instead.");
        }

        return super.put(key, value);
    }

    public Optional<?> save() {
        failIfTableIsMissing();
        return table.get().save(this);
    }

    public void delete() {
        failIfTableIsMissing();
        Object id = get(table.get().getPrimaryKeyColumn().get());
        if (id == null) {
            throw new IllegalStateException("Can not delete this LinkedRow because the ID is not present.");
        }
        table.get().delete(id);
    }

    private void failIfTableIsMissing() {
        if (!table.isPresent()) {
            throw new IllegalStateException("Linked table is not defined.");
        }
    }

}
