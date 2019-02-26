package com.w11k.lsql.statement;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.DatabaseAccessException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AnnotatedSqlStatementToQuery<T> {

    private final AnnotatedSqlStatement AnnotatedSqlStatement;

    private final Map<String, Converter> parameterConverters = Maps.newHashMap();

    public AnnotatedSqlStatementToQuery(AnnotatedSqlStatement annotatedSqlStatement) {
        this(annotatedSqlStatement, Collections.emptyMap());
    }

    public AnnotatedSqlStatementToQuery(AnnotatedSqlStatement AnnotatedSqlStatement, Map<String, Converter> parameterConverters) {
        this.AnnotatedSqlStatement = AnnotatedSqlStatement;
        this.parameterConverters.putAll(parameterConverters);
    }

    public AnnotatedSqlStatementToQuery<T> setParameterConverter(String id, Converter converter) {
        this.parameterConverters.put(id, converter);
        return this;
    }

    public T query() {
        return query(Maps.newHashMap());
    }

    public T query(Object... keyVals) {
        return query(Row.fromKeyVals(keyVals));
    }

    public T query(Map<String, Object> queryParameters) {
        try {
            PreparedStatement ps = this.AnnotatedSqlStatement.createPreparedStatement(queryParameters, this.parameterConverters);
            return createQueryInstance(
                    this.AnnotatedSqlStatement.getlSql(),
                    ps,
                    this.AnnotatedSqlStatement.getOutConverters());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void execute() {
        execute(Maps.newHashMap());
    }

    public void execute(Object... keyVals) {
        execute(Row.fromKeyVals(keyVals));
    }

    public void execute(Map<String, Object> queryParameters) {
        try {
            PreparedStatement ps = this.AnnotatedSqlStatement.createPreparedStatement(queryParameters, this.parameterConverters);
            ps.execute();
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
    }

    public ImmutableMap<String, List<AnnotatedSqlStatement.Parameter>> getParameters() {
        return this.AnnotatedSqlStatement.getParameters();
    }

    abstract protected T createQueryInstance(LSql lSql, PreparedStatement ps, Map<String, Converter> outConverters);

}
