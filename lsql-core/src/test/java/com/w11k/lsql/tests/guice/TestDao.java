package com.w11k.lsql.tests.guice;

import com.w11k.lsql.Query;
import com.w11k.lsql.guice.LSqlDao;
import com.w11k.lsql.guice.QueryMethod;

import static org.testng.Assert.assertEquals;

public class TestDao extends LSqlDao {

    @QueryMethod
    public void query1() {
        Query query = methodQuery();
        assertEquals(query.asList().size(), 1);
    }

}
