package com.w11k.lsql.tests.guice;

import com.w11k.lsql.Query;
import com.w11k.lsql.guice.AbstractLSqlDao;
import com.w11k.lsql.guice.QueryMethod;

import static org.testng.Assert.assertEquals;

public class TestDao extends AbstractLSqlDao {

    @QueryMethod
    public void query1() {
        Query query = methodQuery();
        assertEquals(query.asList().size(), 1);
    }

}
