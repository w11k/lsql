package com.w11k.lsql.tests.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class GuiceTests extends AbstractLSqlTest {

    @Test
    public void testLSqlInjection() {
        Injector inj = Guice.createInjector(new TestModule(lSql));
        TestDao dao = inj.getInstance(TestDao.class);
        assertNotNull(dao.getlSql());
    }

    @Test
    public void testLSqlFileInjection() {
        Injector inj = Guice.createInjector(new TestModule(lSql));
        TestDao dao = inj.getInstance(TestDao.class);
        assertNotNull(dao.getStatement());
    }

}
