package com.w11k.lsql;

import com.google.common.base.Optional;
import com.w11k.lsql.exceptions.DatabaseAccessException;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TypedTable<T extends TableRow, I> {

    private static Table.RowKeyHandler ROW_KEY_HANDLER = new Table.RowKeyHandler() {
        @Override
        public String sqlToJava(String internalSql) {
            return internalSql;
        }

        @Override
        public String javaToSql(String javaIdentifier) {
            return javaIdentifier;
        }
    };
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

    @SuppressWarnings("unchecked")
    public Optional<I> insert(T instance) {
        Map<String, Object> map = instance.toInternalMap();

        // Remove null values so that the DB can insert the default values
        map.entrySet().removeIf(entry -> entry.getValue() == null);

        return (Optional<I>) this.table.get().insert(new Row(map), ROW_KEY_HANDLER);
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
        Optional<LinkedRow> internalRow = this.table.get().load(id, ROW_KEY_HANDLER);

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
        Map<String, Object> map = instance.toInternalMap();
        this.table.get().delete(new Row(map), ROW_KEY_HANDLER);
    }

    public void deleteById(I id) {
        this.table.get().delete(id, ROW_KEY_HANDLER);
    }

    public void update(T instance) {
        Map<String, Object> map = instance.toInternalMap();
        this.table.get().update(new Row(map), ROW_KEY_HANDLER);
    }

    public void updateWhere(T instance, Map<String, Object> where) {
        Map<String, Object> map = instance.toInternalMap();
        this.table.get().updateWhere(new Row(map), new Row(where), ROW_KEY_HANDLER);
    }

    public T updateWith(I id, Function<T, T> with) {
        Optional<T> load = this.load(id);
        T updated = with.apply(load.get());
        this.update(updated);
        return updated;
    }

    @SuppressWarnings("unchecked")
    public Optional<I> save(T instance) {
        Optional<String> primaryKeyColumn = this.table.get().getPrimaryKeyColumn();
        if (!primaryKeyColumn.isPresent()) {
            throw new DatabaseAccessException("save() requires a primary key column.");
        }

        Map<String, Object> map = instance.toInternalMap();
        Object pkValue = map.get(primaryKeyColumn.get());
        if (pkValue == null) {
            map.remove(primaryKeyColumn.get());
        }

        return (Optional<I>) this.table.get().save(new Row(map), ROW_KEY_HANDLER);
    }

    @SuppressWarnings("unchecked")
    public T saveAndLoad(T instance) {
        Object pk = this.save(instance);
        return this.load((I) pk).get();
    }

    protected abstract T createFromInternalMap(Map<String, Object> internalMap);

}
