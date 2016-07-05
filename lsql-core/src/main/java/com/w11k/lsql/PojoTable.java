package com.w11k.lsql;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;

import java.util.Iterator;
import java.util.Map;

public class PojoTable<T> {

    private final Class<T> pojoClass;

    private final Table table;

    private final PojoMapper<T> pojoMapper;

    public PojoTable(Table table, final Class<T> pojoClass) {
        this.pojoClass = pojoClass;
        this.table = table;
        this.pojoMapper = new PojoMapper<T>(pojoClass);

        Map<String, Converter> converters = Maps.newHashMap();
        for (String column : table.getColumns().keySet()) {
            converters.put(column, table.getColumns().get(column).getConverter());
        }
        this.pojoMapper.checkConformity(converters);
    }

    public Class<T> getPojoClass() {
        return this.pojoClass;
    }

    public Table getTable() {
        return this.table;
    }

    public void insert(T pojo) {
        insert(pojo, false);
    }

    public void insert(T pojo, boolean pure) {
        Row row = this.pojoMapper.pojoToRow(pojo);

        // Remove null values so that the DB can insert the default values
        Iterator<Map.Entry<String, Object>> entryIterator = row.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> entry = entryIterator.next();
            if (entry.getValue() == null) {
                entryIterator.remove();
            }
        }

        Optional<Object> id = this.table.insert(row);
        if (!id.isPresent() || pure) {
            return;
        }

        LinkedRow linkedRow = this.table.load(id.get()).get();
        this.pojoMapper.assignRowToPojo(linkedRow, pojo);
    }

    public Optional<T> load(Object id) {
        Optional<LinkedRow> row = this.table.load(id);
        if (!row.isPresent()) {
            return Optional.absent();
        }

        T t = this.pojoMapper.rowToPojo(row.get());
        return Optional.of(t);
    }

    public void delete(T pojo) {
        Row row = this.pojoMapper.pojoToRow(pojo);
        this.table.delete(row);
    }

    public void update(T pojo) {
        Row row = this.pojoMapper.pojoToRow(pojo);
        this.table.update(row);
    }

}
