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

    /**
     * Convenience method to set the expected revision.
     *
     * @param revision Revision to use for DML statements.
     */
    public void setRevision(Object revision) {
        put(table.getRevisionColumn().get().getColumnName(), revision);
    }

    /**
     * @return The revision of this LinkedRow.
     */
    public Object getRevision() {
        return get(table.getRevisionColumn().get().getColumnName());
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
        table.delete(this);
    }


}
