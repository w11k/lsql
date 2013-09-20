package com.w11k.lsql.tests;

import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.converter.JavaBoolToSqlStringConverter;
import com.w11k.lsql.converter.ObjectToJsonStringConverter;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.tests.utils.Person;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ConverterTest extends AbstractLSqlTest {

    private final Converter javaBoolToSqlYesNoStringConverter = new JavaBoolToSqlStringConverter("yes", "no");

    @Test
    public void converterForTable() {
        createTable("CREATE TABLE table1 (yesno TEXT)");
        createTable("CREATE TABLE table2 (yesno TEXT)");
        Table t1 = lSql.table("table1");
        Table t2 = lSql.table("table2");
        lSql.table("table1").setTableConverter(javaBoolToSqlYesNoStringConverter);
        t1.insert(Row.fromKeyVals("yesno", true));
        t2.insert(Row.fromKeyVals("yesno", "true"));
        Row row1 = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        Row row2 = lSql.executeRawQuery("SELECT * FROM table2").getFirstRow().get();
        assertEquals(row1.get("yesno"), true);
        assertEquals(row2.get("yesno"), "true");
    }

    @Test
    public void converterForColumnValue() {
        createTable("CREATE TABLE table1 (yesno1 TEXT, yesno2 TEXT)");
        Table t1 = lSql.table("table1");
        lSql.table("table1").column("yesno1").setColumnConverter(javaBoolToSqlYesNoStringConverter);
        t1.insert(Row.fromKeyVals("yesno1", true, "yesno2", "true"));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.get("yesno1"), true);
        assertEquals(row.get("yesno2"), "true");
    }

    @Test
    public void jsonConverter() {
        createTable("CREATE TABLE table1 (sometext TEXT, person TEXT)");
        Table t1 = lSql.table("table1");
        t1.column("person").setColumnConverter(new ObjectToJsonStringConverter(Person.class));

        Person p = new Person("John", "Doe");
        t1.insert(Row.fromKeyVals("person", p, "sometext", "test"));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.get("person"), p);
    }

    @Test
    public void converterForColumnWithMultipleTablesQuery() {
        createTable("CREATE TABLE table1 (sometext TEXT, person TEXT)");
        createTable("CREATE TABLE table2 (sometext TEXT, person TEXT)");

        Table t2 = lSql.table("table2");
        t2.insert(Row.fromKeyVals("sometext", "aaa", "person", "bbb"));

        Table t1 = lSql.table("table1");
        t1.column("person").setColumnConverter(new ObjectToJsonStringConverter(Person.class));
        Person p = new Person("John", "Doe");
        t1.insert(Row.fromKeyVals("person", p, "sometext", "test"));

        Row row = lSql.executeRawQuery("SELECT * FROM table1, table2").getFirstRow().get();
        assertEquals(row.get("table1.person"), p);
    }

}
