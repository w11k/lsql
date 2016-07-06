package com.w11k.lsql;

import com.google.common.base.Optional;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.validation.AbstractValidationError;

import java.util.Map;

public class LinkedRow extends Row {

    private Table table;

    public Table getTable() {
        return table;
    }

    void setTable(Table table) {
        this.table = table;
    }

    /**
     * @return the primary key value
     */
    public Object getId() {
        return get(table.getPrimaryKeyColumn().get());
    }

    /**
     * set the primary key value
     */
    public void setId(Object id) {
        put(table.getPrimaryKeyColumn().get(), id);
    }

    /**
     * @return the revision value
     */
    public Object getRevision() {
        return get(table.getRevisionColumn().get().getJavaColumnName());
    }

    /**
     * Convenience method to set the expected revision.
     *
     * @param revision Revision to use for DML statements.
     */
    public void setRevision(Object revision) {
        put(table.getRevisionColumn().get().getJavaColumnName(), revision);
    }

    /**
     * Removes the primary column and revision column value, if existent.
     */
    public void removeIdAndRevision() {
        remove(table.getPrimaryKeyColumn().get());
        if (table.getRevisionColumn().isPresent()) {
            remove(table.getRevisionColumn().get().getJavaColumnName());
        }
    }

    /**
     * Puts the given key/value pair into this instance and calls
     * {@link com.w11k.lsql.Table#validate(String, Object)}.
     * <p/>
     * Throws an exception if the validation fails.
     */
    @Override
    public Object put(String key, Object value) {
        Optional<? extends AbstractValidationError> validate = table.validate(key, value);
        if (validate.isPresent()) {
            validate.get().throwError();
        }
        return super.put(key, value);
    }

    /**
     * Puts all known entries into this instance. Tries to convert values with wrong type.
     */
    public LinkedRow putAllKnown(Row from) {
        for (String key : from.keySet()) {
            if (table.getColumns().containsKey(key)) {
                Object val = from.get(key);
                Converter converter = table.getColumns().get(key).getConverter();
//                val = converter.convertValueToTargetType(this.table.getlSql(), val);
                converter.failOnWrongValueType(val);
                put(key, val);
            }
        }
        return this;
    }

    /**
     * Delegates to {@link Table#insert(Row)}.
     */
    public Optional<?> insert() {
        return table.insert(this);
    }

    /**
     * Delegates to {@link Table#save(Row)}.
     */
    public Optional<?> save() {
        return table.save(this);
    }

    /**
     * Delegates to {@link Table#delete(Row)}.
     */
    public void delete() {
        Object id = get(table.getPrimaryKeyColumn().get());
        if (id == null) {
            throw new IllegalStateException("Can not delete this LinkedRow because the primary key value is not present.");
        }
        table.delete(this);
    }

    public <T> T convertTo(Class<T> pojoClass) {
        PojoMapper<T> mapper = new PojoMapper<T>(pojoClass);
        return mapper.rowToPojo(this);
    }

    @Override
    public LinkedRow addKeyVals(Object... keyVals) {
        super.addKeyVals(keyVals);
        return this;
    }

    void setData(Map<String, Object> row) {
        for (String key : row.keySet()) {
            put(key, row.get(key));
        }
    }

}
