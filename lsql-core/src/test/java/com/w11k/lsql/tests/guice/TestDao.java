package com.w11k.lsql.tests.guice;

import com.w11k.lsql.guice.LSqlDao;
import com.w11k.lsql.sqlfile.LSqlFileStatement;

public class TestDao extends LSqlDao {

    public LSqlFileStatement getStatement() {
        return statement("query1");
    }

}

