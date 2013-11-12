package com.w11k.lsql.tests.guice;

import com.google.inject.AbstractModule;
import com.w11k.lsql.LSql;
import com.w11k.lsql.guice.LSqlModule;

public class TestModule extends AbstractModule {

    private LSql lSql;

    public TestModule(LSql lSql) {
        this.lSql = lSql;
    }

    @Override
    protected void configure() {
        install(new LSqlModule());
        bind(LSql.class).toInstance(lSql);
    }

}
