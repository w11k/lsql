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

    public <R> List<R> toList(Func1<T, R> mapper) {
        return rx().map(mapper).toList().toBlocking().first();
    }

    public Optional<T> first() {
        List<T> first = this.rx().take(1).toList().toBlocking().first();
        if (first.isEmpty()) {
            return Optional.absent();
        } else {
            return of(first.get(0));
        }
    }

    public <R> Optional<R> first(final Func1<T, R> mapper) {
        return this.first().transform(mapper::call);
    }

    public abstract String getStatementFileName();

    public abstract String getStatementName();

    protected abstract T createTypedRow(Row row);

    protected abstract Map<String, Object> getQueryParameters();

}
