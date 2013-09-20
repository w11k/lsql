package com.w11k.lsql.tests;

import com.w11k.lsql.Blob;
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
        testType("FLOAT", 123f, 123d);
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

    @Test(dataProvider = "lSqlProvider_h2")
    public void testBlobH2() {
        byte[] data = "123456789".getBytes();
        testType("BLOB", new Blob(data));
    }

    @Test(dataProvider = "lSqlProvider_postgresql")
    public void testBlobPostgres() {
        byte[] data = "123456789".getBytes();
        testType("bytea", new Blob(data));
    }

    private void testType(String sqlTypeName, Object value) {
        testType(sqlTypeName, value, value);
    }

    private void testType(String sqlTypeName, Object value, Object expected) {
        createTable("CREATE TABLE table1 (col " + sqlTypeName + ")");
        Table table1 = lSql.table("table1");
        try {
            table1.insert(Row.fromKeyVals("col", value));
            Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
            Object storedValue = row.get("col");
            assertEquals(storedValue.getClass(), expected.getClass());
            assertEquals(storedValue, expected);
        } finally {
            lSql.executeRawSql("DROP TABLE table1");
        }
    }

}
