package com.w11k.lsql;

import com.google.common.base.Optional;

import javax.annotation.Nullable;

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
    public Object get(@Nullable Object key) {
        // TODO check
        return super.get(key);
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
