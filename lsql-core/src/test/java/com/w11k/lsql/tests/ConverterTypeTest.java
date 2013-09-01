package com.w11k.lsql.tests;

import com.w11k.lsql.relational.Blob;
import com.w11k.lsql.relational.Row;
import com.w11k.lsql.relational.Table;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class ConverterTypeTest extends AbstractLSqlTest {

    @Test(dataProvider = "lSqlProvider")
    public void testBoolean(LSqlProvider provider) {
        provider.init(this);
        testType("BOOL", false);
        testType("BOOL", true);
    }

    @Test(dataProvider = "lSqlProvider")
    public void testInt(LSqlProvider provider) {
        provider.init(this);
        testType("INT", 5);
    }

    @Test(dataProvider = "lSqlProvider")
    public void testFloat(LSqlProvider provider) {
        provider.init(this);
        testType("FLOAT", 123f, 123d);
    }

    @Test(dataProvider = "lSqlProvider")
    public void testText(LSqlProvider provider) {
        provider.init(this);
        testType("TEXT", "foo");
    }

    @Test(dataProvider = "lSqlProvider")
    public void converterCanHandleClobNullValue(LSqlProvider provider) throws SQLException {
        provider.init(this);
        createTable("CREATE TABLE table1 (col1 TEXT, col2 TEXT)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("col1", "val1"));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.get("col1"), "val1");
    }

    @Test(dataProvider = "lSqlProvider")
    public void testChar(LSqlProvider provider) {
        provider.init(this);
        testType("CHAR", 'a');
    }

    @Test(dataProvider = "lSqlProvider")
    public void testDate(LSqlProvider provider) {
        provider.init(this);
        testType("TIMESTAMP", DateTime.now());
    }

    @Test(dataProvider = "lSqlProvider_h2")
    public void testBlobH2(LSqlProvider provider) {
        provider.init(this);
        byte[] data = "123456789".getBytes();
        testType("BLOB", new Blob(data));
    }

    @Test(dataProvider = "lSqlProvider_postgresql")
    public void testBlobPostgres(LSqlProvider provider) {
        provider.init(this);
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
