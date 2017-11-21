package com.w11k.lsql;

import com.google.common.base.Optional;

import java.lang.reflect.Constructor;
import java.util.Map;

public class TypedTable<T extends TableRow, I> {

    private final Table table;
    private final Constructor<T> tableRowConstructor;

    public TypedTable(LSql lSql, String tableName, Class<T> tableRowClass) {
        this.table = lSql.table(tableName);

        try {
            this.tableRowConstructor = tableRowClass.getConstructor(Map.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<I> insert(T row) {
        Map<String, Object> map = row.toMap();

        // Remove null values so that the DB can insert the default values
        map.entrySet().removeIf(entry -> entry.getValue() == null);

        return (Optional<I>) this.table.insert(new Row(map));
    }

    public T insertAndLoad(T row) {
        Optional<I> pk = this.insert(row);
        if (pk.isPresent()) {
            return this.load(pk.get()).get();
        } else {
            throw new IllegalStateException("insert did not return a primary key");
        }
    }

    public Optional<T> load(I id) {
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

    public void deleteById(I id) {
        this.table.delete(id);
    }

    public void update(T row) {
        Map<String, Object> map = row.toMap();
        this.table.update(new Row(map));
    }

    public void updateWhere(T row, Map<String, Object> where) {
        Map<String, Object> map = row.toMap();
        this.table.updateWhere(new Row(map), new Row(where));
    }

    public void save(T row) {
        Map<String, Object> map = row.toMap();
        this.table.save(new Row(map));
    }

}
