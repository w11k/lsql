package com.w11k.lsql.tests.dao;

import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.statement.AbstractSqlStatement;
import com.w11k.lsql.guice.LSqlDao;

public class TestDao extends LSqlDao {

    public AbstractSqlStatement<RowQuery> getStatement() {
        return statement("query1");
    }

}

