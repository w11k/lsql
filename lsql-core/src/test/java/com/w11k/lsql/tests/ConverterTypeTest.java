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
    public void testNullInt() {
        createTable("CREATE TABLE table1 (col1 INT NULL)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("col1", null));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.get("col1"), null);
    }

    @Test
    public void testDouble() {
        TestUtils.testType(lSql, "DOUBLE", 123d, 123d);
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
        testType("CHAR", "a");
    }

    @Test
    public void testCharArray() {
        testType("CHAR(2)", "ab");
    }

    @Test
    public void testDate() {
        DateTime now = DateTime.now();
        testType("TIMESTAMP", now);

        createTable("CREATE TABLE table1 (datetime TIMESTAMP)");
        Table table1 = lSql.table("table1");
        Row insert = Row.fromKeyVals("datetime", now.toString());
        table1.insert(insert);
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.get("datetime"), now);
    }

    private void testType(String sqlTypeName, Object value) {
        TestUtils.testType(lSql, sqlTypeName, value, value);
    }

}
