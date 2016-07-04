package com.w11k.lsql.statement;

import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;
import com.w11k.lsql.exceptions.DatabaseAccessException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public abstract class AbstractSqlStatement<T> {

    private final SqlStatementToPreparedStatement sqlStatementToPreparedStatement;

    public AbstractSqlStatement(SqlStatementToPreparedStatement sqlStatementToPreparedStatement) {
        this.sqlStatementToPreparedStatement = sqlStatementToPreparedStatement;
    }

    public T query() {
        return query(Maps.<String, Object>newHashMap());
    }

    public T query(Object... keyVals) {
        return query(Row.fromKeyVals(keyVals));
    }

    public T query(Map<String, Object> queryParameters) {
        try {
            PreparedStatement ps = this.sqlStatementToPreparedStatement.createPreparedStatement(queryParameters);
            return createQueryInstance(this.sqlStatementToPreparedStatement.getlSql(), ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void execute() {
        execute(Maps.<String, Object>newHashMap());
    }

    public void execute(Object... keyVals) {
        execute(Row.fromKeyVals(keyVals));
    }

    public void execute(Map<String, Object> queryParameters) {
        try {
            PreparedStatement ps = this.sqlStatementToPreparedStatement.createPreparedStatement(queryParameters);
            ps.execute();
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
    }

    abstract protected T createQueryInstance(LSql lSql, PreparedStatement ps);

}
