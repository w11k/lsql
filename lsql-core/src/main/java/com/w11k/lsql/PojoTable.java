package com.w11k.lsql;

import com.google.common.base.Optional;
import com.w11k.lsql.converter.Converter;
import rx.functions.Func3;

import java.util.Iterator;
import java.util.Map;

public class PojoTable<T> {

    private final Class<T> pojoClass;

    private final Table table;

    private final PojoMapper<T> pojoMapper;

    public PojoTable(LSql lSql, String tableName, final Class<T> pojoClass) {
        this.pojoClass = pojoClass;
        this.pojoMapper = new PojoMapper<T>(lSql, pojoClass, true);

        this.table = lSql.table(tableName);
        this.table.setConverterProvider(new Func3<LSql, String, Integer, Converter>() {
            @Override
            public Converter call(LSql lSql, String javaColumnName, Integer sqlType) {
                Class<?> returnType = PojoTable.this.pojoMapper.getPropertyDescriptor(javaColumnName)
                        .getReadMethod().getReturnType();
                return lSql.getDialect().getConverterRegistry().getConverterForJavaType(returnType);
            }
        });
    }

    public String getTableName() {
        return this.table.getTableName();
    }

    public Class<T> getPojoClass() {
        return this.pojoClass;
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