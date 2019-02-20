package com.w11k.lsql.cli.java;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.query.PlainQuery;
import com.w11k.lsql.statement.AbstractSqlStatement;
import com.w11k.lsql.statement.SqlStatementToPreparedStatement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public final class TypedStatementMeta {

    private final LSql lSql;

    private final SqlStatementToPreparedStatement statement;
    @Nullable
    private final String sourceFileName;

    private Map<String, Class<?>> parameters = Maps.newHashMap();

    public TypedStatementMeta(LSql lSql,
                              AbstractSqlStatement<PlainQuery> query,
                              SqlStatementToPreparedStatement statement,
                              @Nullable String sourceFileName) {

        this.lSql = lSql;
        this.statement = statement;
        this.sourceFileName = sourceFileName;

        ImmutableMap<String, List<SqlStatementToPreparedStatement.Parameter>> queryParameters =
                query.getParameters();

        for (String key : queryParameters.keySet()) {
            List<SqlStatementToPreparedStatement.Parameter> allParamOccurences = queryParameters.get(key);
            SqlStatementToPreparedStatement.Parameter p = allParamOccurences.get(0);

            Class<?> paramType;
            if (Strings.isNullOrEmpty(p.getJavaTypeAlias())) {
                paramType = Object.class;
            } else {
                Converter converterForAlias = lSql.getConverterForAlias(p.getJavaTypeAlias());
                paramType = converterForAlias.getJavaType();
            }

            this.parameters.put(key, paramType);
        }

    }

    public LSql getlSql() {
        return lSql;
    }

    public SqlStatementToPreparedStatement getStatement() {
        return statement;
    }

    public Map<String, Class<?>> getParameters() {
        return parameters;
    }

    @Nullable
    public String getSourceFileName() {
        return sourceFileName;
    }
}
