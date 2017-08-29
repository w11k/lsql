package com.w11k.lsql.dialects;

import com.w11k.lsql.Config;

public final class H2Config extends Config {

    public H2Config() {
        this.setDialect(new H2Dialect());
    }
}
