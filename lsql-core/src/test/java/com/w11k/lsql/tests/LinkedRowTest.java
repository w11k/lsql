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

        LinkedRow queriedRow1 = table1.get(1).get();
        assertEquals(queriedRow1.getInt("age"), 1);

        queriedRow1.put("age", 99);
        queriedRow1.save();
        LinkedRow queriedRow1b = table1.get(1).get();
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
    public void queryWithOnlyOneTableShouldLinkRowToTable() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        lSql.table("table1").insert(Row.fromKeyVals("id", 1, "age", 1));
        Query query = lSql.executeRawQuery("SELECT * FROM table1;");
        QueriedRow row = query.getFirstRow().get();
        assertTrue(row.hasLinkedTable());
    }

    @Test
    public void queryWithTwoTablesShouldNotLinkRowToTable() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        createTable("CREATE TABLE table2 (id INTEGER PRIMARY KEY, age INT)");
        lSql.table("table1").insert(Row.fromKeyVals("id", 1, "age", 1));
        lSql.table("table2").insert(Row.fromKeyVals("id", 1, "age", 1));
        Query query = lSql.executeRawQuery("SELECT * FROM table1, table2;");
        QueriedRow row = query.getFirstRow().get();
        assertFalse(row.hasLinkedTable());
    }

    @Test
    public void queryWithTwoTablesAndGroupByTablesShouldLinkRowToTable() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        createTable("CREATE TABLE table2 (id INTEGER PRIMARY KEY, age INT)");
        lSql.table("table1").insert(Row.fromKeyVals("id", 1, "age", 1));
        lSql.table("table2").insert(Row.fromKeyVals("id", 1, "age", 2));
        Query query = lSql.executeRawQuery("SELECT * FROM table1, table2;");
        QueriedRow row = query.getFirstRow().get();
        Map<String, Map<Object, LinkedRow>> byTables = row.groupByTables();
        assertEquals(byTables.get("table1").size(), 1);
        assertEquals(byTables.get("table2").size(), 1);
        assertNotNull(byTables.get("table1").get(1).getTable());
        assertNotNull(byTables.get("table2").get(1).getTable());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void linkedRowPutThrowsExceptionOnTooLongString() {
        createTable("CREATE TABLE table1 (name VARCHAR(5))");
        LinkedRow row = lSql.table("table1").newLinkedRow();
        row.put("name", "123456");
    }

    @Test
    public void newLinkedRowCopiedDataWithoutIdAndRevisionColumn() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT, revision INT DEFAULT 0)");
        Table table1 = lSql.table("table1");
        table1.enableRevisionSupport();
        table1.insert(Row.fromKeyVals("id", 1, "age", 1));
        LinkedRow row = table1.get(1).get();
        assertTrue(row.containsKey("id"));
        assertTrue(row.containsKey("revision"));

        LinkedRow copy = table1.newLinkedRow(row);
        assertFalse(copy.containsKey("id"));
        assertFalse(copy.containsKey("revision"));
    }

}
