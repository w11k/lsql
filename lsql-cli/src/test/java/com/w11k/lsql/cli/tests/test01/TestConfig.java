package com.w11k.lsql.cli.tests.test01;

import com.w11k.lsql.Config;
import com.w11k.lsql.dialects.H2Dialect;

public final class TestConfig extends Config {

    public TestConfig() {
        setDialect(new H2Dialect());
        setGeneratedPackageName(this.getClass().getPackage().getName());
    }

}
