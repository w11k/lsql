package com.w11k.lsql.tests;

import com.googlecode.flyway.core.Flyway;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;

import javax.sql.DataSource;

import static org.testng.Assert.assertEquals;

public class TestUtils {

    public static void clear(DataSource ds) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(ds);
        flyway.clean();
    }

    public static void testType(LSql lSql, String sqlTypeName, Object value, Object expected) {
        lSql.executeRawSql("CREATE TABLE table_col_value_test (col " + sqlTypeName + ")");
        Table table1 = lSql.table("table_col_value_test");
        try {
            table1.insert(Row.fromKeyVals("col", value));
            Row row = lSql.executeRawQuery("SELECT * FROM table_col_value_test").getFirstRow().get();
            Object storedValue = row.get("col");
            //assertEquals(storedValue.getClass(), expected.getClass());
            assertEquals(storedValue, expected);
        } finally {
            lSql.executeRawSql("DROP TABLE table_col_value_test");
        }
    }

}
