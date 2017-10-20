package com.w11k.lsql.cli.java;

import com.google.common.collect.Maps;
import com.w11k.lsql.*;
import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.statement.AbstractSqlStatement;

import java.io.File;
import java.util.Map;

public final class StatementRowColumnContainer implements ColumnsContainer {

    private final StatementFileExporter statementFileExporter;

    private final TypedStatementMeta typedStatementMeta;

    private Map<String, Column> columns = Maps.newHashMap();

    public StatementRowColumnContainer(StatementFileExporter statementFileExporter,
                                       TypedStatementMeta typedStatementMeta,
                                       LSql lSql,
                                       AbstractSqlStatement<RowQuery> query,
                                       String sqlStatementsLocation,
                                       File statementFile) {

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
        return this.statementFileExporter.getPackageName();
    }

    @Override
    public String getSchemaAndTableName() {
        return this.getSchemaName() + "___" + this.getTableName();
    }

    @Override
    public String getTableName() {
        return this.typedStatementMeta.getStatement().getStatementName();
    }


}
