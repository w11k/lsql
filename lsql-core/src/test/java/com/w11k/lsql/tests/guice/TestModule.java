package com.w11k.lsql.tests.guice;

import com.google.inject.AbstractModule;
import com.w11k.lsql.LSql;
import com.w11k.lsql.guice.LSqlDaoProvider;
import com.w11k.lsql.guice.LSqlModule;

public class TestModule extends AbstractModule {

    private LSql lSql;

    public TestModule(LSql lSql) {
        this.lSql = lSql;
    }

    @Override
    protected void configure() {
        bind(LSql.class).toInstance(lSql);
        install(new LSqlModule());
        LSqlDaoProvider.bind(binder(), TestDao.class);
    }

}
