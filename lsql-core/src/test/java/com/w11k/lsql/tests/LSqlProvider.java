package com.w11k.lsql.tests;

import com.w11k.lsql.LSql;

public class LSqlProvider {

    private final LSql lSql;

    public LSqlProvider(LSql lSql) {
        this.lSql = lSql;
    }

    public void init(AbstractLSqlTest test) {
        test.setupLSqlInstanceForTest(lSql);
    }

    @Override
    public String toString() {
        return "LSqlProvider{" + lSql + '}';
    }
}
