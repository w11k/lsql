package com.w11k.lsql.tests.dialects;

import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.dialects.SqlServerDialect;

public class SqlServerTest extends AbstractDialectTests {

    @Override
    public BaseDialect createDialect() {
        return new SqlServerDialect();
    }

    @Override
    protected void setupTestTable() {
        lSql.executeRawSql("CREATE TABLE table1 (id INT IDENTITY PRIMARY KEY, age INTEGER)");
    }

    @Override
    protected String getBlobColumnType() {
        return "VARBINARY(10)";
    }

}
