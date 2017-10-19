package com.w11k.lsql;

import com.google.common.collect.Maps;
import rx.Observable;

import java.util.List;
import java.util.Map;

public abstract class TypedStatementQuery<T> {

    private final LSql lSql;

    private final String sqlStatement;

    protected Map<String, Object> parameterValues = Maps.newHashMap();

    public TypedStatementQuery(LSql lSql, String sqlStatement) {
        this.lSql = lSql;
        this.sqlStatement = sqlStatement;
    }

    public Observable<T> toStream() {
        return this.lSql.executeQuery(this.sqlStatement)
                .query(this.parameterValues)
                .rx()
                .map(this::createTypedRow);
    }

    public List<T> toList() {
        return this.toStream().toList().toBlocking().first();
    }

    protected abstract T createTypedRow(Row row);

}
