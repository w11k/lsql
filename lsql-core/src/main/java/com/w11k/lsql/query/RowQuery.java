package com.w11k.lsql.query;

import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public class RowQuery extends AbstractQuery<Row> {

    public RowQuery(LSql lSql, PreparedStatement preparedStatement) {
        super(lSql, preparedStatement);
    }

    @SuppressWarnings("unchecked")
    public List<Row> toTree() {
        return (List<Row>) new QueryToTreeConverter(this, new RowEntityCreator()).getTree();
    }

    @Override
    protected Row createEntity() {
        return new Row();
    }

    @Override
    protected void checkConformity(Map<String, Converter> converters) {
        // noop
    }

    @Override
    protected void setValue(Row entity, String name, Object value) {
        entity.put(name, value);
    }


}
