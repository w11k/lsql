package com.w11k.lsql.tests.dao;

import com.w11k.lsql.guice.LSqlDao;
import com.w11k.lsql.SelectStatement;

public class TestDao extends LSqlDao {

    public SelectStatement getStatement() {
        return statement("query1");
    }

}

