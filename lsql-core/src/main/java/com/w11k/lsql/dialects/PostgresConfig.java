package com.w11k.lsql.dialects;

import com.w11k.lsql.Config;

public final class PostgresConfig extends Config {

    public PostgresConfig() {
        this.setDialect(new PostgresDialect());
    }

}
