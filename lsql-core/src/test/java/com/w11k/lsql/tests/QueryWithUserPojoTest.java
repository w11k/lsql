package com.w11k.lsql.tests;

import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.QueriedRow;
import com.w11k.lsql.Query;
import com.w11k.lsql.Table;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class QueryWithUserPojoTest extends AbstractLSqlTest {

    public static class Person extends LinkedRow {
    }

    @Test
    public void extractLinkedRowByColumn() {
        createTable("CREATE TABLE person (id INT PRIMARY KEY, name TEXT)");
        Table<Person> table = lSql.table("person", Person.class);
        table.newLinkedRow().addKeyVals(
                "id", 1,
                "name", "A"
        ).save();
        Query query = lSql.executeRawQuery("select *, 1 as number from person;");
        QueriedRow queriedRow = query.getFirstRow().get();
        LinkedRow person = queriedRow.extractLinkedRowByColumn("id");
        assertTrue(person instanceof Person);
        assertEquals(person.size(), 2);
        assertEquals(person.get("id"), 1);
        assertEquals(person.get("name"), "A");
    }

//    @Test
//    public void extractLinkedRowByColumnWithAliasInQuery() {
//        createTable("CREATE TABLE person (id INT PRIMARY KEY, name TEXT)");
//        Table<Person> table = lSql.table("person", Person.class);
//        table.newLinkedRow().addKeyVals(
//                "id", 1,
//                "name", "A"
//        ).save();
//        Query query = lSql.executeRawQuery("select id as aaa, name as bbb from person;");
//        QueriedRow queriedRow = query.getFirstRow().get();
//        assertEquals(queriedRow.get("aaa"), 1);
//        assertEquals(queriedRow.get("bbb"), "A");
//
//        LinkedRow person = queriedRow.extractLinkedRowByColumn("aaa");
//        assertTrue(person instanceof Person);
//        assertEquals(person.size(), 2);
//        assertEquals(person.get("id"), 1);
//        assertEquals(person.get("name"), "A");
//    }

}
