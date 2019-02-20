package com.w11k.lsql.query;

import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public class PlainQuery extends AbstractQuery<Row> {

    public PlainQuery(LSql lSql, PreparedStatement preparedStatement, Map<String, Converter> outConverters) {
        super(lSql, preparedStatement, outConverters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Row> toTree() {
        return (List<Row>) new QueryToTreeConverter(this, new RowEntityCreator()).getTree();
    }

    @Override
    protected Row createEntity() {
        return new Row();
    }

    @Override
    protected void setValue(LSql lSql, Row entity, String internalSqlColumnName, Object value) {
        entity.put(internalSqlColumnName, value);
    }

}
