package com.w11k.lsql.query;

import com.google.common.collect.Lists;
import com.w11k.lsql.LSql;
import com.w11k.lsql.PojoMapper;
import com.w11k.lsql.QueryToTreeConverter;
import com.w11k.lsql.Row;
import rx.annotations.Experimental;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class PojoQuery<T> extends AbstractQuery<T> {

    private final PojoMapper<T> pojoMapper;

    private final Class<T> pojoClass;

    public PojoQuery(LSql lSql, PreparedStatement preparedStatement, Class<T> pojoClass) {
        super(lSql, preparedStatement);
        this.pojoMapper = new PojoMapper<T>(lSql, pojoClass, false);
        this.pojoClass = pojoClass;
    }

    @Experimental
    public List<T> toTree() {
        LinkedHashMap<Number, Row> tree = new QueryToTreeConverter(this).getTree();
        Collection<Row> roots = tree.values();
        List<T> rootPojos = Lists.newLinkedList();
        for (Row root : roots) {
            T rootPojo = getlSql().getObjectMapper().convertValue(root, this.pojoClass);
            rootPojos.add(rootPojo);
        }
        return rootPojos;
    }

    @Override
    protected T createEntity() {
        return this.pojoMapper.newInstance();
    }

    @Override
    protected void setValue(T entity, String name, Object value) {
        this.pojoMapper.setValue(entity, name, value);
    }
}
