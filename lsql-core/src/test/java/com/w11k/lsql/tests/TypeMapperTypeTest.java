package com.w11k.lsql.tests;

import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.converter.predefined.JavaBoolToSqlStringConverter;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class TypeMapperTypeTest extends AbstractLSqlTest {

    private final Converter javaBoolToSqlYesNoStringConverter = new JavaBoolToSqlStringConverter("yes", "no");

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
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, col1 INT NULL)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("id", 1, "col1", null));
        Row row = table1.load(1).get();
        assertEquals(row.get("col1"), null);
    }

    @Test
    public void testDouble() {
        TestUtils.testType(lSql, "DECIMAL", 123d, 123d);
    }

    @Test
    public void testText() {
        testType("TEXT", "foo");
    }

    @Test
    public void converterCanHandleClobNullValue() throws SQLException {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, col1 TEXT, col2 TEXT)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("id", 1, "col1", "val1"));
        Row row = table1.load(1).get();
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

        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, datetime TIMESTAMP)");
        Table table1 = lSql.table("table1");
        Row insert = Row.fromKeyVals("id", 1, "datetime", now);
        table1.insert(insert);
        Row row = table1.load(1).get();
        assertEquals(row.get("datetime"), now);
    }

    @Test
    public void converterForColumnValue() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, yesno1 TEXT, yesno2 TEXT)");
        Table t1 = lSql.table("table1");
        t1.column("yesno1").setConverter(javaBoolToSqlYesNoStringConverter);
        t1.insert(Row.fromKeyVals("id", 1, "yesno1", true, "yesno2", "true"));
        LinkedRow row = t1.load(1).get();
        assertEquals(row.get("yesno1"), true);
        assertEquals(row.get("yesno2"), "true");
    }

    private void testType(String sqlTypeName, Object value) {
        TestUtils.testType(lSql, sqlTypeName, value, value);
    }

}
