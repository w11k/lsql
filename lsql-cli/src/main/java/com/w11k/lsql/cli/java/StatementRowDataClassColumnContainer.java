package com.w11k.lsql.cli.java;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.w11k.lsql.Column;
import com.w11k.lsql.ResultSetColumn;
import com.w11k.lsql.ResultSetWithColumns;
import com.w11k.lsql.TableLike;
import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.statement.AbstractSqlStatement;

import java.util.Map;

public final class StatementRowDataClassColumnContainer implements TableLike {

    private final StatementFileExporter statementFileExporter;

    private final TypedStatementMeta typedStatementMeta;

    private Map<String, Column> columns = Maps.newHashMap();

    public StatementRowDataClassColumnContainer(StatementFileExporter statementFileExporter,
                                                TypedStatementMeta typedStatementMeta,
                                                AbstractSqlStatement<RowQuery> query) {

        this.statementFileExporter = statementFileExporter;
        this.typedStatementMeta = typedStatementMeta;

        ResultSetWithColumns resultSetWithColumns = query.query().createResultSetWithColumns();
        for (ResultSetColumn resultSetColumn : resultSetWithColumns.getColumns()) {
            Column c = new Column(
                    null,
                    resultSetColumn.getName(),
                    resultSetColumn.getConverter().getSqlType(),
                    resultSetColumn.getConverter(),
                    -1);
            this.columns.put(resultSetColumn.getName(), c);
        }
    }

    @Override
    public Map<String, Column> getColumns() {
        return this.columns;
    }

    @Override
    public String getSchemaName() {
        return "aaaaaaaaaaaaaaaaaaaaaaaaaa";
    }

    @Override
    public String getSchemaAndTableName() {
        throw new RuntimeException();
//        return this.getSchemaName() + "___" + this.getTableName();
    }

    @Override
    public String getTableName() {
//        throw new RuntimeException();
        return this.typedStatementMeta.getStatement().getStatementName();
    }

    @Override
    public Optional<Class<?>> getPrimaryKeyType() {
        throw new RuntimeException();
//        return Optional.absent();
    }
}
