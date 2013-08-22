package com.w11k.lsql.tests;

import com.google.common.base.CaseFormat;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.converter.JavaBoolToSqlStringConverter;
import com.w11k.lsql.converter.JsonClobConverter;
import com.w11k.lsql.relational.Row;
import com.w11k.lsql.relational.Table;
import com.w11k.lsql.tests.utils.Person;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ConverterTest extends AbstractLSqlTest {

    private final Converter javaBoolToSqlYesNoStringConverter = new JavaBoolToSqlStringConverter("yes", "no");

    @Test
    public void caseFormatConversion() {
        lSql.setJavaCaseFormat(CaseFormat.LOWER_CAMEL);
        lSql.setSqlCaseFormat(CaseFormat.UPPER_UNDERSCORE);

        lSql.executeRawSql("CREATE TABLE table1 (test_name1 TEXT, TEST_NAME2 TEXT)");
        lSql.executeRawSql("INSERT INTO table1 (test_name1, TEST_NAME2) VALUES ('name1', 'name2')");

        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow();
        assertEquals(row.get("testName1"), "name1");
        assertEquals(row.get("testName2"), "name2");
    }

    @Test public void converterGlobal() {
        lSql.executeRawSql("CREATE TABLE table1 (yesno TEXT)");
        Table table1 = lSql.table("table1");
        lSql.setGlobalConverter(javaBoolToSqlYesNoStringConverter);
        table1.insert(Row.fromKeyVals("yesno", true));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow();
        assertEquals(row.get("yesno"), true);
    }

    @Test public void converterForTable() {
        lSql.executeRawSql("CREATE TABLE table1 (yesno TEXT)");
        lSql.executeRawSql("CREATE TABLE table2 (yesno TEXT)");
        Table t1 = lSql.table("table1");
        Table t2 = lSql.table("table2");
        lSql.table("table1").setTableConverter(javaBoolToSqlYesNoStringConverter);
        t1.insert(Row.fromKeyVals("yesno", true));
        t2.insert(Row.fromKeyVals("yesno", "true"));
        Row row1 = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow();
        Row row2 = lSql.executeRawQuery("SELECT * FROM table2").getFirstRow();
        assertEquals(row1.get("yesno"), true);
        assertEquals(row2.get("yesno"), "true");
    }

    @Test public void converterForColumnValue() {
        lSql.executeRawSql("CREATE TABLE table1 (yesno1 TEXT, yesno2 TEXT)");
        Table t1 = lSql.table("table1");
        lSql.table("table1").column("yesno1").setColumnConverter(javaBoolToSqlYesNoStringConverter);
        t1.insert(Row.fromKeyVals("yesno1", true, "yesno2", "true"));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow();
        assertEquals(row.get("yesno1"), true);
        assertEquals(row.get("yesno2"), "true");
    }

    @Test public void jsonClobConverter() {
        lSql.executeRawSql("CREATE TABLE table1 (sometext TEXT, person TEXT)");
        Table t1 = lSql.table("table1");
        t1.column("person").setColumnConverter(new JsonClobConverter(Person.class));

        Person p = new Person("John", "Doe");
        t1.insert(Row.fromKeyVals("person", p, "sometext", "test"));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow();
        assertEquals(row.get("person"), p);
    }

    @Test public void converterForColumnWithMultipleTablesQuery() {
        lSql.executeRawSql("CREATE TABLE table1 (sometext TEXT, person TEXT)");
        lSql.executeRawSql("CREATE TABLE table2 (sometext TEXT, person TEXT)");

        Table t2 = lSql.table("table2");
        t2.insert(Row.fromKeyVals("sometext", "aaa", "person", "bbb"));

        Table t1 = lSql.table("table1");
        t1.column("person").setColumnConverter(new JsonClobConverter(Person.class));
        Person p = new Person("John", "Doe");
        t1.insert(Row.fromKeyVals("person", p, "sometext", "test"));

        Row row = lSql.executeRawQuery("SELECT * FROM table1, table2").getFirstRow();
        assertEquals(row.get("table1.person"), p);
    }

}
