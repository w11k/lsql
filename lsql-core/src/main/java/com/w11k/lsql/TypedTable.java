package com.w11k.lsql;

import com.google.common.base.Optional;

import java.util.Map;

public abstract class TypedTable<T extends TableRow, I> {

    private final Table table;
//    private final Constructor<T> tableRowConstructor;

    public TypedTable(LSql lSql, String tableName, Class<T> tableRowClass) {
        this.table = lSql.table(tableName);

//        try {
//            this.tableRowConstructor = tableRowClass.getConstructor(Map.class);
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
    }

    @SuppressWarnings("unchecked")
    public Optional<I> insert(T instance) {
        Map<String, Object> map = instance.toInternalMap();

        // Remove null values so that the DB can insert the default values
        map.entrySet().removeIf(entry -> entry.getValue() == null);

        return (Optional<I>) this.table.insert(new Row(map));
    }

    public T insertAndLoad(T instance) {
        Optional<I> pk = this.insert(instance);
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
            tableRow = this.createFromInternalMap(row.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.of(tableRow);
    }

    public void delete(T instance) {
        Map<String, Object> map = instance.toInternalMap();
        this.table.delete(new Row(map));
    }

    public void deleteById(I id) {
        this.table.delete(id);
    }

    public void update(T instance) {
        Map<String, Object> map = instance.toInternalMap();
        this.table.update(new Row(map));
    }

    public void updateWhere(T instance, Map<String, Object> where) {
        Map<String, Object> map = instance.toInternalMap();
        this.table.updateWhere(new Row(map), new Row(where));
    }

    @SuppressWarnings("unchecked")
    public Optional<I> save(T instance) {
        Map<String, Object> map = instance.toInternalMap();
        return (Optional<I>) this.table.save(new Row(map));
    }

    @SuppressWarnings("unchecked")
    public T saveAndLoad(T instance) {
        Object pk = this.save(instance);
        return this.load((I) pk).get();
    }

    protected abstract T createFromInternalMap(Map<String, Object> internalMap);

}
