package com.w11k.lsql;

import com.google.common.base.Optional;
import com.w11k.lsql.exceptions.DatabaseAccessException;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TypedTable<T extends TableRow, I> {

    private Supplier<Table> table;

    public TypedTable(LSql lSql, String tableName, Class<T> tableRowClass) {
        this.setupLazyTableGetter(lSql, tableName);
    }

    private void setupLazyTableGetter(LSql lSql, String tableName) {
        final Table[] tableInstance = new Table[1];
        this.table = () -> {
            if (tableInstance[0] == null) {
                tableInstance[0] = lSql.tableBySqlName(tableName);
            }
            return tableInstance[0];
        };
    }

    public Optional<I> insert(T instance) {
        return this.insert(instance, RowSerializer.INSTANCE_DIRECT, RowDeserializer.INSTANCE_DIRECT);
    }

    @SuppressWarnings("unchecked")
    public Optional<I> insert(T instance, RowSerializer<Row> rowSerializer, RowDeserializer<Row> rowDeserializer) {
        Map<String, Object> map = instance.toInternalMap();

        // Remove null values so that the DB can insert the default values
        map.entrySet().removeIf(entry -> entry.getValue() == null);

        return (Optional<I>) this.table.get().insert(new Row(map), rowSerializer, rowDeserializer);
    }

    public T insertAndLoad(T instance) {
        return this.insertAndLoad(instance, RowSerializer.INSTANCE_DIRECT, RowDeserializer.INSTANCE_DIRECT);
    }

    public T insertAndLoad(T instance, RowSerializer<Row> rowSerializer, RowDeserializer<Row> rowDeserializer) {
        Optional<I> pk = this.insert(instance, rowSerializer, rowDeserializer);
        if (pk.isPresent()) {
            return this.load(pk.get()).get();
        } else {
            throw new IllegalStateException("insert did not return a primary key");
        }
    }

    public Optional<T> load(I id) {
        return this.load(id, RowDeserializer.INSTANCE_DIRECT);
    }

    public Optional<T> load(I id, RowDeserializer<Row> rowDeserializer) {
        Optional<? extends Row> internalRow = this.table.get().load(id, rowDeserializer);

        if (!internalRow.isPresent()) {
            return Optional.absent();
        }

        T tableRow;
        try {
            tableRow = this.createFromInternalMap(internalRow.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.of(tableRow);
    }

    public void delete(T instance) {
        this.delete(instance, RowDeserializer.INSTANCE_DIRECT);
    }

    public void delete(T instance, RowDeserializer<Row> rowDeserializer) {
        Map<String, Object> map = instance.toInternalMap();
        this.table.get().delete(new Row(map), rowDeserializer);
    }

    public void deleteById(I id) {
        this.deleteById(id, RowDeserializer.INSTANCE_DIRECT);
    }

    public void deleteById(I id, RowDeserializer<Row> rowDeserializer) {
        this.table.get().delete(id, rowDeserializer);
    }

    public void update(T instance) {
        this.update(instance, RowSerializer.INSTANCE_DIRECT, RowDeserializer.INSTANCE_DIRECT);
    }

    public void update(T instance, RowSerializer<Row> rowSerializer, RowDeserializer<Row> rowDeserializer) {
        Map<String, Object> map = instance.toInternalMap();
        this.table.get().update(new Row(map), rowSerializer, rowDeserializer);
    }

    public void updateWhere(T instance, Map<String, Object> where) {
        this.updateWhere(instance, where, RowSerializer.INSTANCE_DIRECT, RowDeserializer.INSTANCE_DIRECT);
    }

    public void updateWhere(
            T instance, Map<String, Object> where, RowSerializer<Row> rowSerializer, RowDeserializer<Row> rowDeserializer) {

        Map<String, Object> map = instance.toInternalMap();
        this.table.get().updateWhere(new Row(map), new Row(where), rowSerializer, rowDeserializer);
    }

    public T updateWith(I id, Function<T, T> with) {
        Optional<T> load = this.load(id);
        T updated = with.apply(load.get());
        this.update(updated);
        return updated;
    }

    public Optional<I> save(T instance) {
        return this.save(instance, RowSerializer.INSTANCE_DIRECT, RowDeserializer.INSTANCE_DIRECT);
    }

    @SuppressWarnings("unchecked")
    public Optional<I> save(T instance, RowSerializer<Row> rowSerializer, RowDeserializer<Row> rowDeserializer) {
        Optional<String> primaryKeyColumn = this.table.get().getPrimaryKeyColumn();
        if (!primaryKeyColumn.isPresent()) {
            throw new DatabaseAccessException("save() requires a primary key column.");
        }

        Map<String, Object> map = instance.toInternalMap();
        Object pkValue = map.get(primaryKeyColumn.get());
        if (pkValue == null) {
            map.remove(primaryKeyColumn.get());
        }

        return (Optional<I>) this.table.get().save(new Row(map), rowSerializer, rowDeserializer);
    }

    public T saveAndLoad(T instance) {
        return this.saveAndLoad(instance, RowSerializer.INSTANCE_DIRECT, RowDeserializer.INSTANCE_DIRECT);
    }

    @SuppressWarnings("unchecked")
    public T saveAndLoad(T instance, RowSerializer<Row> rowSerializer, RowDeserializer<Row> rowDeserializer) {
        Object pk = this.save(instance, rowSerializer, rowDeserializer);
        return this.load((I) pk, rowDeserializer).get();
    }

    protected abstract T createFromInternalMap(Map<String, Object> internalMap);

}
