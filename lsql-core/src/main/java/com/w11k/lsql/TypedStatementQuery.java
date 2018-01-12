package com.w11k.lsql;

import com.google.common.base.Optional;
import rx.Observable;
import rx.functions.Func1;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.of;

public abstract class TypedStatementQuery<T> {

    private final LSql lSql;

    private final String sqlStatement;

    public TypedStatementQuery(LSql lSql, String sqlStatement) {
        this.lSql = lSql;
        this.sqlStatement = sqlStatement;
    }

    public Observable<T> toObservable() {
        return this.lSql.createSqlStatement(this.sqlStatement)
                .query(this.getQueryParameters())
                .rx()
                .map(this::createTypedRow);
    }

    public List<T> toList() {
        return this.toObservable().toList().toBlocking().first();
    }

    public <R> List<R> map(Func1<T, R> fn) {
        return this.toObservable()
                .map(fn)
                .toList()
                .toBlocking()
                .first();
    }

    public Optional<T> first() {
        List<T> first = this.toObservable().take(1).toList().toBlocking().first();
        if (first.isEmpty()) {
            return Optional.absent();
        } else {
            return of(first.get(0));
        }
    }

    protected abstract T createTypedRow(Row row);

    protected abstract Map<String, Object> getQueryParameters();

}
