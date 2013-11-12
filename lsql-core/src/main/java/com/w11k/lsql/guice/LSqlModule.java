package com.w11k.lsql.guice;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class LSqlModule extends AbstractModule {

    @Override
    protected void configure() {
        LSqlDaoListener listener = new LSqlDaoListener();
        requestInjection(listener);
        bindListener(Matchers.any(), listener);
    }

}
