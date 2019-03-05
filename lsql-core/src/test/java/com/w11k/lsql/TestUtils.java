package com.w11k.lsql;

import com.googlecode.flyway.core.Flyway;
import com.w11k.lsql.LSql;
import com.w11k.lsql.LinkedRow;
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
        lSql.executeRawSql("CREATE TABLE table_col_value_test (id int primary key, col " + sqlTypeName + ")");
        Table table1 = lSql.table("table_col_value_test");
        try {
            table1.insert(Row.fromKeyVals("id", 1, "col", value));
            LinkedRow row = table1.load(1).get();
            Object storedValue = row.get("col");
            assertEquals(storedValue, expected);
        } finally {
            lSql.executeRawSql("DROP TABLE table_col_value_test");
        }
    }

}
