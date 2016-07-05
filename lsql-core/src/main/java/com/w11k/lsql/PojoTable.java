package com.w11k.lsql;

import com.google.common.base.Optional;

import java.util.Iterator;
import java.util.Map;

public class PojoTable<T> {

    private final Class<T> pojoClass;

    private final Table table;

    private final PojoMapper<T> pojoMapper;

    public PojoTable(Table table, final Class<T> pojoClass) {
        this.pojoClass = pojoClass;
        this.table = table;
        this.pojoMapper = new PojoMapper<T>(table.getlSql(), pojoClass, true);

//        new Table(lSql, tableName) {
//            @Override
//            protected TypeMapper getConverter(String javaColumnName, int sqlType) {
//                Class<?> returnType = PojoTable.this.pojoMapper.getPropertyDescriptor(javaColumnName)
//                        .getReadMethod().getReturnType();
//                TypeMapper typeMapper = lSql.getDialect().getConverterRegistry().getConverterForJavaType(returnType);
//
//                if (!typeMapper.supportsSqlType(sqlType)) {
//                    String msg = "converter for Java type '" + typeMapper.getJavaType().getCanonicalName() + "' " +
//                            "does not support SQL type '" + SqlTypesNames.getName(sqlType) + "'";
//                    throw new IllegalArgumentException(msg);
//                }
//
//                return typeMapper;
//            }
//        };
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
