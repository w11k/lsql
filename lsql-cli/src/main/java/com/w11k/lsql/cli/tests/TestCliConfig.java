package com.w11k.lsql.cli.tests;

import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.dialects.H2Dialect;

public final class TestCliConfig extends Config {

    public static void createTables(LSql lSql) {
        lSql.executeRawSql("create table person1 (id integer primary key, first_name text)");
        lSql.executeRawSql("create table person2 (id integer primary key, first_name text, age integer)");
        lSql.executeRawSql("create table a_table (id integer primary key)");
        lSql.executeRawSql("create table checks (yesno BOOLEAN);");
    }

    public TestCliConfig() {
        setDialect(new H2Dialect());
    }

}
