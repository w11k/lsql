package com.w11k.lsql.sqlfile;

import com.google.common.base.Optional;
import com.w11k.lsql.LSql;

import java.sql.PreparedStatement;

public class LazyPreparedStatement {

    private final String sqlString;

    private final LSql lSql;

    private Optional<PreparedStatement> preparedStatementOptional = Optional.absent();

    LazyPreparedStatement(LSql lSql, String sqlString) {
        this.lSql = lSql;
        this.sqlString = sqlString;
    }

    public PreparedStatement getPreparedStatement() {
        if (!preparedStatementOptional.isPresent()) {
            PreparedStatement ps = lSql.prepareStatement(sqlString);
            preparedStatementOptional = Optional.of(ps);
        }
        return preparedStatementOptional.get();
    }

}
