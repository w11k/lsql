package com.w11k.lsql;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.google.gson.reflect.TypeToken;
import com.w11k.lsql.converter.ObjectToJsonStringConverter;
import com.w11k.lsql.tests.AbstractLSqlTest;
import com.w11k.lsql.tests.utils.Person;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class ObjectToJsonStringConverterTest extends AbstractLSqlTest {

    @Test
    public void pojo() {
        createTable("CREATE TABLE table1 (sometext TEXT, person TEXT)");
        Table t1 = lSql.table("table1");
        t1.column("person").setConverter(new ObjectToJsonStringConverter(Person.class));

        Person p = new Person("John", "Doe");
        t1.insert(Row.fromKeyVals("person", p, "sometext", "test"));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.get("person"), p);
    }

    @Test
    public void listOfString() {
        createTable("CREATE TABLE table1 (data TEXT)");
        Table t1 = lSql.table("table1");
        t1.column("data").setConverter(new ObjectToJsonStringConverter(new TypeToken<List<String>>() {}));

        List<String> list = Lists.newLinkedList();
        list.add("first");
        list.add("second");

        t1.insert(Row.fromKeyVals("data", list));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.get("data"), list);
    }

    @Test
    public void listOfMapStringString() {
        createTable("CREATE TABLE table1 (data TEXT)");
        Table t1 = lSql.table("table1");
        t1.column("data").setConverter(new ObjectToJsonStringConverter(
                new TypeToken<List<Map<String, String>>>() { }));

        List<Map<String, String>> list = Lists.newLinkedList();
        Map<String, String> e = Maps.newHashMap();
        e.put("a", "1");
        e.put("b", "2");
        list.add(e);
        list.add(e);

        t1.insert(Row.fromKeyVals("data", list));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.get("data"), list);
    }



}
