package com.w11k.lsql.tests;

import com.google.common.base.Optional;
import com.w11k.lsql.*;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

public class LinkedRowTest extends AbstractLSqlTest {

    @Test
    public void save() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");

        LinkedRow row1 = table1.newLinkedRow();
        row1.addKeyVals("id", 1, "age", 1);
        Optional<?> row1Id = row1.save();
        assertTrue(row1Id.isPresent());
        assertEquals(row1Id.get(), 1);

        QueriedRow queriedRow1 = table1.get(1).get();
        assertEquals(queriedRow1.getInt("age"), 1);

        queriedRow1.put("age", 99);
        queriedRow1.save();
        QueriedRow queriedRow1b = table1.get(1).get();
        assertEquals(queriedRow1b.getInt("age"), 99);
    }

    @Test
    public void delete() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");
        LinkedRow row1 = table1.newLinkedRow();
        row1.addKeyVals("id", 1, "age", 1);
        row1.save();
        assertEquals(table1.get(1).get().getInt("age"), 1);
        row1.delete();
        assertFalse(table1.get(1).isPresent());
    }

    @Test
    public void queryWithOnlyTableShouldLinkRowToTable() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        lSql.table("table1").insert(Row.fromKeyVals("id", 1, "age", 1));
        Query query = lSql.executeRawQuery("SELECT * FROM table1;");
        QueriedRow row = query.getFirstRow().get();
        assertTrue(row.getTable().isPresent());
    }

    @Test
    public void queryWithTwoTablesShouldNotLinkRowToTable() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        createTable("CREATE TABLE table2 (id INTEGER PRIMARY KEY, age INT)");
        lSql.table("table1").insert(Row.fromKeyVals("id", 1, "age", 1));
        lSql.table("table2").insert(Row.fromKeyVals("id", 1, "age", 1));
        Query query = lSql.executeRawQuery("SELECT * FROM table1, table2;");
        QueriedRow row = query.getFirstRow().get();
        assertFalse(row.getTable().isPresent());
    }

    @Test
    public void queryWithTwoTablesAndGroupByTablesShouldLinkRowToTable() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        createTable("CREATE TABLE table2 (id INTEGER PRIMARY KEY, age INT)");
        lSql.table("table1").insert(Row.fromKeyVals("id", 1, "age", 1));
        lSql.table("table2").insert(Row.fromKeyVals("id", 1, "age", 1));
        Query query = lSql.executeRawQuery("SELECT * FROM table1, table2;");
        QueriedRow row = query.getFirstRow().get();
        Map<String, LinkedRow> byTables = row.groupByTables();
        assertTrue(byTables.get("table1").getTable().isPresent());
        assertTrue(byTables.get("table2").getTable().isPresent());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void putShouldFailOnWrongColumnName() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");
        LinkedRow row = table1.newLinkedRow();
        row.put("wrong", 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void putShouldFailOnWrongColumnValue() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");
        LinkedRow row = table1.newLinkedRow();
        row.put("age", "1");
    }

}
