package com.w11k.lsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PojoTable<T> implements ITable {

    private final ObjectMapper mapper;

    private final Class<T> pojoClass;

    private final Table table;

    private final Set<String> propertyNames = Sets.newHashSet();

    public PojoTable(LSql lSql, String tableName, Class<T> pojoClass) {
        this.table = lSql.table(tableName);
        this.mapper = lSql.getObjectMapper();
        this.pojoClass = pojoClass;

        // Extract property names
        PropertyDescriptor[] descs = PropertyUtils.getPropertyDescriptors(pojoClass);
        for (PropertyDescriptor desc : descs) {
            propertyNames.add(desc.getName());
        }
    }

    public Class<T> getPojoClass() {
        return pojoClass;
    }

    public T insert(T pojo) {
        Row row = pojoToRow(pojo);
        Iterator<Map.Entry<String, Object>> entryIterator = row.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> entry = entryIterator.next();
            if (entry.getValue() == null) {
                entryIterator.remove();
            }
        }

        Optional<Object> id = table.insert(row);
        if (!id.isPresent()) {
            return null;
        }
        return load(id.get()).get();
    }

    public Optional<T> load(Object id) {
        Optional<LinkedRow> row = table.load(id);
        if (!row.isPresent()) {
            return Optional.absent();
        }

        T t = mapper.convertValue(row.get(), pojoClass);
        return Optional.of(t);
    }

    public void delete(T pojo) {
        Row row = pojoToRow(pojo);
        this.table.delete(row);
    }

    public void update(T pojo) {
        Row row = pojoToRow(pojo);
        this.table.update(row);
    }

    @Override
    public String getTableName() {
        return table.getTableName();
    }

    private Row pojoToRow(T pojo) {
        Row row = mapper.convertValue(pojo, Row.class);

        // Remove subclass attributes
        Iterator<String> iterator = row.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (!propertyNames.contains(key)) {
                iterator.remove();
            }
        }

        return row;
    }
}
