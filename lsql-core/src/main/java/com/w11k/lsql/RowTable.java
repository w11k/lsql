package com.w11k.lsql;

import com.google.common.base.Optional;

import java.lang.reflect.Constructor;
import java.util.Map;

public class RowTable<T extends TableRow> {

    private final Table table;
    private final Constructor<T> tableRowConstructor;

    public RowTable(LSql lSql, String tableName, Class<T> tableRowClass) {
        this.table = lSql.table(tableName);

        try {
            this.tableRowConstructor = tableRowClass.getConstructor(Map.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Object> insert(T row) {
        Map<String, Object> map = row.toMap();

        // Remove null values so that the DB can insert the default values
        map.entrySet().removeIf(entry -> entry.getValue() == null);

        return this.table.insert(new Row(map));
    }

    public Optional<T> load(Object id) {
        Optional<LinkedRow> row = this.table.load(id);
        if (!row.isPresent()) {
            return Optional.absent();
        }

        T tableRow;
        try {
            tableRow = this.tableRowConstructor.newInstance(row.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.of(tableRow);
    }

    public void delete(T row) {
        Map<String, Object> map = row.toMap();
        this.table.delete(new Row(map));
    }

}
