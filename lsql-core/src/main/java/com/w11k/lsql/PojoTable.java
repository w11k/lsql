package com.w11k.lsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

public class PojoTable<T> {

    private final LSql lSql;

    private final String tableName;

    private final Class<T> pojoClass;

    private final Table table;

    public PojoTable(LSql lSql, String tableName, Class<T> pojoClass) {
        this.lSql = lSql;
        this.tableName = tableName;
        this.pojoClass = pojoClass;
        this.table = new Table(lSql, tableName);
    }

    public Optional<Object> insert(T pojo) {
        ObjectMapper mapper = LSql.OBJECT_MAPPER;
        Row row = mapper.convertValue(pojo, Row.class);
        return table.insert(row);
    }

    public T load(Object id) {
        Optional<LinkedRow> row = table.load(id);
        return null;
    }

}
