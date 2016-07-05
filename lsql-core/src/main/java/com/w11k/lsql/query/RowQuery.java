package com.w11k.lsql.query;

import com.w11k.lsql.LSql;
import com.w11k.lsql.QueryToTreeConverter;
import com.w11k.lsql.Row;
import com.w11k.lsql.converter.Converter;
import rx.annotations.Experimental;

import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.Map;

public class RowQuery extends AbstractQuery<Row> {

    public RowQuery(LSql lSql, PreparedStatement preparedStatement) {
        super(lSql, preparedStatement);
    }

    @Experimental
    public LinkedHashMap<Number, Row> toTree() {
        return new QueryToTreeConverter(this).getTree();
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
