package com.w11k.lsql.tests.dao;

import com.w11k.lsql.SqlStatement;
import com.w11k.lsql.guice.LSqlDao;

public class TestDao extends LSqlDao {

    public SqlStatement getStatement() {
        return statement("query1");
    }

}

