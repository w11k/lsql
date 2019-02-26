package com.w11k.lsql.tests.dao;

import com.w11k.lsql.query.PlainQuery;
import com.w11k.lsql.statement.AnnotatedSqlStatementToQuery;
import com.w11k.lsql.guice.LSqlDao;

public class TestDao extends LSqlDao {

    public AnnotatedSqlStatementToQuery<PlainQuery> getStatement() {
        return statement("query1");
    }

}

