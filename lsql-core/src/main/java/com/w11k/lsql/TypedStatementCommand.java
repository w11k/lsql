package com.w11k.lsql;

import com.google.common.collect.Maps;

import java.util.Map;

public abstract class TypedStatementCommand {

    private final LSql lSql;

    private final String sqlStatement;

    protected Map<String, Object> parameterValues = Maps.newHashMap();

    public TypedStatementCommand(LSql lSql, String sqlStatement) {
        this.lSql = lSql;
        this.sqlStatement = sqlStatement;
    }

    public void execute() {
        this.lSql.executeQuery(this.sqlStatement).execute(this.parameterValues);
    }

}
