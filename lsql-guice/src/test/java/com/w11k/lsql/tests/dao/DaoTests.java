package com.w11k.lsql.tests.dao;

import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class DaoTests extends AbstractLSqlTest {

    @Test
    public void testLSqlInjection() {
        assertNotNull(createTestDao().getlSql());
    }

    @Test
    public void testLSqlFileInjection() {
        assertNotNull(createTestDao().getStatement());
    }

    private TestDao createTestDao() {
        TestDao t = new TestDao();
        t.setlSql(this.lSql);
        return t;
    }

}
