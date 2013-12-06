package com.w11k.lsql;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.w11k.lsql.validation.AbstractValidationError;

import java.util.Map;

public class LinkedRow extends Row {

    private Table table;

    public LinkedRow(Table table) {
        this(table, Maps.<String, Object>newLinkedHashMap());
    }

    public LinkedRow(Table table, Map<String, Object> row) {
        super(row);
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Object getId() {
        return get(table.getPrimaryKeyColumn().get());
    }

    @Override
    public Object put(String key, Object value) {
        Optional<? extends AbstractValidationError> validate = table.validate(key, value);
        if (validate.isPresent()) {
            validate.get().throwError();
        }
        return super.put(key, value);
    }

    public Optional<?> insert() {
        return table.insert(this);
    }

    public Optional<?> save() {
        return table.save(this);
    }

    public void delete() {
        Object id = get(table.getPrimaryKeyColumn().get());
        if (id == null) {
            throw new IllegalStateException("Can not delete this LinkedRow because the ID is not present.");
        }
        table.delete(id);
    }


}
