package com.w11k.lsql.tests;

import com.w11k.lsql.Column;
import com.w11k.lsql.InitColumnCallback;
import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class InitColumnCallbackTest extends AbstractLSqlTest {

    @Test
    public void callbackGetsCalled() {
        final boolean[] called = new boolean[1];
        called[0] = false;

        lSql.setInitColumnCallback(new InitColumnCallback() {
            @Override
            public void onNewColumn(Column column) {
                called[0] = true;
            }
        });

        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, c2 INT)");
        lSql.table("table1");

        assertTrue(called[0]);
    }

}
