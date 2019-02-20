package com.w11k.lsql.query;

import com.w11k.lsql.LSql;
import com.w11k.lsql.PojoMapper;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public class PojoQuery<T> extends AbstractQuery<T> {

    private final PojoMapper<T> pojoMapper;

    private final Class<T> pojoClass;

    public PojoQuery(LSql lSql, PreparedStatement preparedStatement, Class<T> pojoClass, Map<String, Converter> outConverters) {
        super(lSql, preparedStatement, outConverters);
        this.pojoMapper = PojoMapper.getFor(pojoClass);
        this.pojoClass = pojoClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> toTree() {
        return (List<T>) new QueryToTreeConverter(this, new PojoEntityCreator<T>(this.pojoClass)).getTree();
    }

    @Override
    protected T createEntity() {
        return this.pojoMapper.newInstance();
    }

    @Override
    protected void setValue(LSql lSql, T entity, String internalSqlColumnName, Object value) {
        this.pojoMapper.setValue(entity, lSql.convertInternalSqlToRowKey(internalSqlColumnName), value);
    }
}
