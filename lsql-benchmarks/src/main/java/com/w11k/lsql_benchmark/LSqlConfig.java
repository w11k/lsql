package com.w11k.lsql_benchmark;

import com.w11k.lsql.Config;
import com.w11k.lsql.dialects.PostgresDialect;

public class LSqlConfig extends Config {

    public LSqlConfig() {
        setDialect(new PostgresDialect());
    }

}
