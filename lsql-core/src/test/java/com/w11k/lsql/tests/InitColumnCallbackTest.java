package com.w11k.lsql.tests;

import com.w11k.lsql.Column;
import com.w11k.lsql.InitColumnCallback;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class InitColumnCallbackTest extends AbstractLSqlTest {

    @Test
    public void callbackGetsCalled() {
        final boolean[] called = new boolean[] {false, false};

        lSql.setInitColumnCallback(new InitColumnCallback() {
            @Override
            public void onNewColumn(Column column) {
                if (column.getJavaColumnName().equals("c1")) {
                    called[0] = true;
                } else if (column.getJavaColumnName().equals("c2")) {
                    called[1] = true;
                }
            }
        });

        createTable("CREATE TABLE table1 (c1 INTEGER PRIMARY KEY, c2 INT)");
        lSql.table("table1");

        assertTrue(called[0]);
        assertTrue(called[1]);
    }

}
