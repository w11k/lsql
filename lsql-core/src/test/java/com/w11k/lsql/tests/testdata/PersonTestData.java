package com.w11k.lsql.tests.testdata;

import com.w11k.lsql.LSql;

public class PersonTestData {

    public static final String SELECT_ALL_ORDER_BY_ID = "select * from person order by id;";

    public static void init(LSql lSql, boolean insertTestData) {
        lSql.executeRawSql("CREATE TABLE person (" +
                "id INTEGER PRIMARY KEY, " +
                "first_name TEXT, " +
                "age INT, " +
                "title VARCHAR(50) DEFAULT 'n/a'" +
                ")");

        if (insertTestData) {
            lSql.executeRawSql("INSERT INTO person (id, first_name, age) VALUES (1, 'Adam', 30)");
            lSql.executeRawSql("INSERT INTO person (id, first_name, age) VALUES (2, 'Eve', 29)");
        }
    }

}
