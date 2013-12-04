package com.w11k.lsql.tests;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.QueriedRow;
import com.w11k.lsql.Query;
import com.w11k.lsql.Row;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class QueryTest extends AbstractLSqlTest {

    @Test
    public void query() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeRawQuery("SELECT * FROM table1");
        assertNotNull(rows);
    }

    @Test
    public void queryIterator() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        Query rows = lSql.executeRawQuery("SELECT * FROM table1");
        int sum = 0;
        for (QueriedRow row : rows) {
            sum += row.getInt("age");
        }
        assertEquals(sum, 50);
    }

    @Test
    public void queryList() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        List<QueriedRow> rows = lSql.executeRawQuery("SELECT * FROM table1").asList();
        assertEquals(rows.size(), 2);
    }

    @Test
    public void queryMap() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        Query rows = lSql.executeRawQuery("SELECT * FROM table1");
        List<Integer> ages = rows.map(new Function<QueriedRow, Integer>() {
            @Override
            public Integer apply(QueriedRow input) {
                return input.getInt("age");
            }
        });
        assertTrue(ages.contains(20));
        assertTrue(ages.contains(30));
    }

    @Test
    public void queryGetFirstRow() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeRawQuery("SELECT * FROM table1");
        QueriedRow row = rows.getFirstRow().get();
        assertNotNull(row);
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), 20);
    }

    @Test
    public void testTablePrefix() {
        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name1 TEXT)");
        createTable("CREATE TABLE table2 (id SERIAL PRIMARY KEY, name2 TEXT)");
        Optional<Object> id1 = lSql.table("table1").insert(Row.fromKeyVals("name1", "value1"));
        Optional<Object> id2 = lSql.table("table2").insert(Row.fromKeyVals("name2", "value2"));

        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.keySet().size(), 2);
        assertEquals(row.getInt("id"), id1.get());
        assertEquals(row.getString("name1"), "value1");

        row = lSql.executeRawQuery("SELECT * FROM table1, table2").getFirstRow().get();
        assertEquals(row.keySet().size(), 4);
        assertEquals(row.getInt("table1.id"), id1.get());
        assertEquals(row.getString("table1.name1"), "value1");
        assertEquals(row.getInt("table2.id"), id2.get());
        assertEquals(row.getString("table2.name2"), "value2");
    }

    @Test
    public void testTableCounterAndGroupByTables() {
        createTable("CREATE TABLE table2 (id SERIAL PRIMARY KEY, name2 TEXT)");
        createTable("CREATE TABLE table1 (" +
                "id SERIAL PRIMARY KEY, " +
                "table2a INT REFERENCES table2 (id), " +
                "table2b INT REFERENCES table2 (id))");

        Optional<Object> id2a = lSql.table("table2").insert(Row.fromKeyVals("name2", "value2a"));
        Optional<Object> id2b = lSql.table("table2").insert(Row.fromKeyVals("name2", "value2b"));
        Optional<Object> id1 = lSql.table("table1").insert(Row.fromKeyVals(
                "table2a", id2a.get(),
                "table2b", id2b.get()
        ));

        Query query = lSql.executeRawQuery("SELECT * FROM table1 " +
                "JOIN table2 t2a ON t2a.id = table2a " +
                "JOIN table2 t2b ON t2b.id = table2b;");

        QueriedRow row = query.getFirstRow().get();
        assertEquals(row.get("table1.id"), id1.get());
        assertEquals(row.get("table1.table2a"), id2a.get());
        assertEquals(row.get("table1.table2b"), id2b.get());
        assertEquals(row.get("table2.1.id"), id2a.get());
        assertEquals(row.get("table2.1.name2"), "value2a");
        assertEquals(row.get("table2.2.id"), id2b.get());
        assertEquals(row.get("table2.2.name2"), "value2b");

        Map<String, Map<Object, LinkedRow>> grouped = query.groupByTables();
        assertEquals(grouped.get("table1").get(id1.get()).get("table2a"), id2a.get());
        assertEquals(grouped.get("table1").get(id1.get()).get("table2b"), id2b.get());
        assertEquals(grouped.get("table2").get(id2a.get()).get("name2"), "value2a");
        assertEquals(grouped.get("table2").get(id2b.get()).get("name2"), "value2b");
    }

    @Test
    public void groupByTablesWithNullValues() {
        createTable("CREATE TABLE table2 (id SERIAL PRIMARY KEY, name2 TEXT)");
        createTable("CREATE TABLE table1 (" +
                "id SERIAL PRIMARY KEY, " +
                "table2a INT REFERENCES table2 (id), " +
                "table2b INT REFERENCES table2 (id))");

        Optional<Object> id2a = lSql.table("table2").insert(Row.fromKeyVals("name2", "value2a"));
        lSql.table("table1").insert(Row.fromKeyVals(
                "table2a", id2a.get()
        ));

        Query query = lSql.executeRawQuery("SELECT * FROM table1 " +
                "JOIN table2 t2a ON t2a.id = table2a " +
                "LEFT OUTER JOIN table2 t2b ON t2b.id = table2b;");

        Map<String, Map<Object, LinkedRow>> grouped = query.groupByTables();
        assertEquals(grouped.get("table1").size(), 1);
        assertEquals(grouped.get("table2").size(), 1);
    }

    @Test
    public void canUseCalculatedColumns() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeRawQuery("SELECT count(*) AS c FROM table1");
        Row row = rows.getFirstRow().get();
        assertEquals(row.getInt("c"), 2);
    }

    @Test
    public void canUseCalculatedColumnsTogetherWithNormalColumnsOneTable() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeRawQuery("SELECT name, age, count(*) AS c FROM table1");
        Row row = rows.getFirstRow().get();
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), 20);
        assertEquals(row.getInt("c"), 1);
    }

    @Test
    public void rowIsNotLinkedToTableWhenCalculatedColumnsAreInTheResultSet() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeRawQuery("SELECT name, age, count(*) AS c FROM table1");
        QueriedRow row = rows.getFirstRow().get();
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), 20);
        assertEquals(row.getInt("c"), 1);
        assertFalse(row.hasLinkedTable());
    }

    @Test
    public void canUseCalculatedColumnsTogetherWithNormalColumnsTwoTable() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        createTable("CREATE TABLE table2 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table2 (name, age) VALUES ('cus2', 30)");
        Query rows = lSql.executeRawQuery("SELECT *, count(*) AS c FROM table1, table2");

        Row row = rows.getFirstRow().get();
        assertEquals(row.getString("table1.name"), "cus1");
        assertEquals(row.getString("table2.name"), "cus2");
        assertEquals(row.getInt("table1.age"), 20);
        assertEquals(row.getInt("table2.age"), 30);
        assertEquals(row.getInt("c"), 1);
    }

    @Test
    public void canChangeAQueriedRowBasedOnOneTable() {
        createTable("CREATE TABLE table1 (name1 TEXT, age1 INT)");
        lSql.executeRawSql("INSERT INTO table1 (name1, age1) VALUES ('cus1', 20)");
        Query rows = lSql.executeRawQuery("SELECT * FROM table1");

        Row row = rows.getFirstRow().get();
        row.put("name1", "should not fail");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void canNotChangeAQueriedRowBasedOnMoreThanOneTable() {
        createTable("CREATE TABLE table1 (name1 TEXT, age1 INT)");
        createTable("CREATE TABLE table2 (name2 TEXT, age2 INT)");
        lSql.executeRawSql("INSERT INTO table1 (name1, age1) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table2 (name2, age2) VALUES ('cus2', 30)");
        Query rows = lSql.executeRawQuery("SELECT * FROM table1, table2");

        Row row = rows.getFirstRow().get();
        row.put("name1", "should fail");
    }

    @Test
    public void canChangeAQueriedRowBasedOnMoreThanOneTableAfterCopy() {
        createTable("CREATE TABLE table1 (name1 TEXT, age1 INT)");
        createTable("CREATE TABLE table2 (name2 TEXT, age2 INT)");
        lSql.executeRawSql("INSERT INTO table1 (name1, age1) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table2 (name2, age2) VALUES ('cus2', 30)");
        Query rows = lSql.executeRawQuery("SELECT * FROM table1, table2");

        Row row = rows.getFirstRow().get().copy();
        row.put("name1", "should fail");
    }

    @Test
    public void asRawList() {
        createTable("CREATE TABLE table1 (name1 TEXT, age1 INT)");
        lSql.executeRawSql("INSERT INTO table1 (name1, age1) VALUES ('cus1', 20)");
        Query query = lSql.executeRawQuery("SELECT name1 AS a, age1 AS b FROM table1");
        List<Row> list = query.asRawList();
        assertEquals(list.get(0).getString("a"), "cus1");
        assertEquals(list.get(0).getInt("b"), 20);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void asRawListFailsOnDublicateKeys() {
        createTable("CREATE TABLE table1 (name1 TEXT, age1 INT)");
        lSql.executeRawSql("INSERT INTO table1 (name1, age1) VALUES ('cus1', 20)");
        Query query = lSql.executeRawQuery("SELECT name1 AS a, age1 AS a FROM table1");
        query.asRawList();
    }


}
