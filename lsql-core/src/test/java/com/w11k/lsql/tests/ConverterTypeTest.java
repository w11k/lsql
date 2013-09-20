package com.w11k.lsql.tests;

import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class ConverterTypeTest extends AbstractLSqlTest {

    @Test
    public void testBoolean() {
        testType("BOOL", false);
        testType("BOOL", true);
    }

    @Test
    public void testInt() {
        testType("INT", 5);
    }

    @Test
    public void testFloat() {
        TestUtils.testType(lSql, "FLOAT", 123f, 123d);
    }

    @Test
    public void testText() {
        testType("TEXT", "foo");
    }

    @Test
    public void converterCanHandleClobNullValue() throws SQLException {
        createTable("CREATE TABLE table1 (col1 TEXT, col2 TEXT)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("col1", "val1"));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.get("col1"), "val1");
    }

    @Test
    public void testChar() {
        testType("CHAR", 'a');
    }

    @Test
    public void testDate() {
        testType("TIMESTAMP", DateTime.now());
    }

    private void testType(String sqlTypeName, Object value) {
        TestUtils.testType(lSql, sqlTypeName, value, value);
    }

}
