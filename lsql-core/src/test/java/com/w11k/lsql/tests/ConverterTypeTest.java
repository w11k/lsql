package com.w11k.lsql.tests;

import com.w11k.lsql.relational.Blob;
import com.w11k.lsql.relational.Row;
import com.w11k.lsql.relational.Table;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class ConverterTypeTest extends AbstractLSqlTest {

    private void testType(String sqlTypeName, Object value) {
        testType(sqlTypeName, value, value);
    }

    private void testType(String sqlTypeName, Object value, Object expected) {
        createTable("CREATE TABLE table1 (col " + sqlTypeName + ")");
        Table table1 = lSql.table("table1");
        try {
            table1.insert(Row.fromKeyVals("col", value));
            Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow();
            Object storedValue = row.get("col");
            assertEquals(storedValue.getClass(), expected.getClass());
            assertEquals(storedValue, expected);
        } finally {
            lSql.executeRawSql("DROP TABLE table1");
        }
    }

    @Test public void testBoolean() {
        testType("BOOL", true);
        testType("BOOL", false);
    }

    @Test public void testInt() {
        testType("INT", 5);
    }

    @Test public void testFloat() {
        testType("FLOAT", 123f, 123d);
    }

    @Test public void testDouble() {
        testType("DOUBLE", 123d);
    }

    @Test public void testText() {
        testType("TEXT", "foo");
    }

    @Test public void converterCanHandleClobNullValue() throws SQLException {
        createTable("CREATE TABLE table1 (col1 TEXT, col2 TEXT)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("col1", "val1"));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow();
        assertEquals(row.get("col1"), "val1");
    }

    @Test public void testChar() {
        testType("CHAR", 'a');
    }

    @Test public void testVarChar() {
        testType("VARCHAR(5)", "123".toCharArray());
    }

    @Test public void testDate() {
        testType("TIMESTAMP", DateTime.now());
    }

    @Test public void testBlob() {
        byte[] data = "123456789".getBytes();
        testType("BLOB", new Blob(data));
    }

}
