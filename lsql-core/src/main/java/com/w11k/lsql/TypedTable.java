package com.w11k.lsql;

import com.google.common.base.Optional;

import java.util.Map;
import java.util.function.Function;

public abstract class TypedTable<T extends TableRow, I> {

    private Table table;
    private final String tableName;
    private final LSql lSql;
//    private final Constructor<T> tableRowConstructor;

    public TypedTable(LSql lSql, String tableName, Class<T> tableRowClass) {
        //this.table = lSql.tableBySqlName(tableName);
        this.tableName = tableName;
        this.lSql = lSql;
//        try {
//            this.tableRowConstructor = tableRowClass.getConstructor(Map.class);
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
    }

    /** use only getTable() to ensure no database connection is needed in constructor. */
    private Table getTable() {
        if (this.table == null) {
            this.table = lSql.tableBySqlName(tableName);
        }
        return this.table;
    }

    @SuppressWarnings("unchecked")
    public Optional<I> insert(T instance) {
        Map<String, Object> map = instance.toInternalMap();

        // Remove null values so that the DB can insert the default values
        map.entrySet().removeIf(entry -> entry.getValue() == null);

        return (Optional<I>) this.getTable().insert(new Row(map));
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
        Optional<LinkedRow> row = this.getTable().load(id);
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
        this.getTable().delete(new Row(map));
    }

    public void deleteById(I id) {
        this.getTable().delete(id);
    }

    public void update(T instance) {
        Map<String, Object> map = instance.toInternalMap();
        this.getTable().update(new Row(map));
    }

    public void updateWhere(T instance, Map<String, Object> where) {
        Map<String, Object> map = instance.toInternalMap();
        this.getTable().updateWhere(new Row(map), new Row(where));
    }

    public T updateWith(I id, Function<T, T> with) {
        Optional<T> load = this.load(id);
        T updated = with.apply(load.get());
        this.update(updated);
        return updated;
    }

    @SuppressWarnings("unchecked")
    public Optional<I> save(T instance) {
        Map<String, Object> map = instance.toInternalMap();
        return (Optional<I>) this.getTable().save(new Row(map));
    }

    @SuppressWarnings("unchecked")
    public T saveAndLoad(T instance) {
        Object pk = this.save(instance);
        return this.load((I) pk).get();
    }

    protected abstract T createFromInternalMap(Map<String, Object> internalMap);

}
