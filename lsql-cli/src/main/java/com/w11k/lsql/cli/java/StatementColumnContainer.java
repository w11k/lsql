package com.w11k.lsql.cli.java;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.w11k.lsql.Column;
import com.w11k.lsql.ColumnsContainer;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.statement.AbstractSqlStatement;
import com.w11k.lsql.statement.SqlStatementToPreparedStatement;

import java.util.List;
import java.util.Map;

public final class StatementColumnContainer implements ColumnsContainer {

    private Map<String, Column> columns = Maps.newHashMap();

    public StatementColumnContainer(LSql lSql, AbstractSqlStatement<RowQuery> query) {
        ImmutableMap<String, List<SqlStatementToPreparedStatement.Parameter>> params = query.getParameters();
        for (String key : params.keySet()) {
            List<SqlStatementToPreparedStatement.Parameter> allParamOccurences = params.get(key);
            SqlStatementToPreparedStatement.Parameter p = allParamOccurences.get(0);

            System.out.println("key = " + key);
            System.out.println("p.getJavaTypeAlias() = " + p.getJavaTypeAlias());
            System.out.println("\n");


            Converter converterForAlias = lSql.getConverterForAlias(p.getJavaTypeAlias());
            Column c = new Column(null, key, converterForAlias.getSqlType(), converterForAlias, -1);
            this.columns.put(key, c);
        }
    }

    @Override
    public Map<String, Column> getColumns() {
        return this.columns;
    }

    @Override
    public String getSchemaName() {
        return "aaaaaaa";
    }

    @Override
    public String getSchemaAndTableName() {
        return this.getSchemaName() + "___" + this.getTableName();
    }

    @Override
    public String getTableName() {
        return "bbbbbb";
    }
}
