package com.w11k.lsql.tests;

import com.googlecode.flyway.core.Flyway;

import javax.sql.DataSource;

public class TestUtils {

    public static void clear(DataSource ds) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(ds);
        flyway.clean();
    }

}
