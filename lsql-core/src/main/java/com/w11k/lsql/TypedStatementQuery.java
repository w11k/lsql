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

    public Observable<T> rx() {
        return this.lSql.createSqlStatement(this.sqlStatement, this.getStatementFileName(), this.getStatementName())
                .query(this.getQueryParameters())
                .rx()
                .map(this::createTypedRow);
    }

    public List<T> toList() {
        return this.rx().toList().toBlocking().first();
    }

    public <R> List<R> map(Func1<T, R> fn) {
        return this.rx()
                .map(fn)
                .toList()
                .toBlocking()
                .first();
    }

    public Optional<T> first() {
        List<T> first = this.rx().take(1).toList().toBlocking().first();
        if (first.isEmpty()) {
            return Optional.absent();
        } else {
            return of(first.get(0));
        }
    }

    public abstract String getStatementFileName();

    public abstract String getStatementName();

    protected abstract T createTypedRow(Row row);

    protected abstract Map<String, Object> getQueryParameters();

}
