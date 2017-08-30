package com.w11k.lsql.tests.cli;

import com.w11k.lsql.Config;
import com.w11k.lsql.dialects.H2Dialect;

public final class TestCliConfig extends Config {

    public TestCliConfig() {
        setDialect(new H2Dialect());

        getCliConfig().setGeneratedPackageName(this.getClass().getPackage().getName());
        String dir = TestCliConfig.class.getResource(".").getPath();
        getCliConfig().setSqlStatementFiles(dir + "/**/*.sql");
    }

}
