package com.w11k.lsql;

import java.util.Map;

public abstract class TypedStatementCommand {

    private final LSql lSql;

    private final String sqlStatement;

    public TypedStatementCommand(LSql lSql, String sqlStatement) {
        this.lSql = lSql;
        this.sqlStatement = sqlStatement;
    }

    public void execute() {
        this.lSql.createSqlStatement(this.sqlStatement).execute(this.getQueryParameters());
    }

    protected abstract Map<String, Object> getQueryParameters();

}
