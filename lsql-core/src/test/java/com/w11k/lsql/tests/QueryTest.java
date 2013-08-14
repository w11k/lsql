package com.w11k.lsql.tests;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.w11k.lsql.QueriedRow;
import com.w11k.lsql.Query;
import com.w11k.lsql.Row;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

public class QueryTest extends AbstractLSqlTest {

    @Test public void query() {
        lSql.executeRawSql("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeRawQuery("select * from table1");
        assertNotNull(rows);
    }

    @Test public void queryIterator() {
        lSql.executeRawSql("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        Query rows = lSql.executeRawQuery("select * from table1");
        int sum = 0;
        for (Row row : rows) {
            sum += row.getInt("age");
        }
        assertEquals(sum, 50);
    }

    @Test public void queryList() {
        lSql.executeRawSql("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        List<QueriedRow> rows = lSql.executeRawQuery("select * from table1").asList();
        assertEquals(rows.size(), 2);
    }

    @Test public void queryMap() {
        lSql.executeRawSql("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        Query rows = lSql.executeRawQuery("select * from table1");
        List<Integer> ages = rows.map(new Function<QueriedRow, Integer>() {
            @Override public Integer apply(QueriedRow input) {
                return input.getInt("age");
            }
        });
        assertTrue(ages.contains(20));
        assertTrue(ages.contains(30));
    }

    @Test public void queryGetFirstRow() {
        lSql.executeRawSql("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeRawQuery("select * from table1");
        Row row = rows.getFirstRow();
        assertNotNull(row);
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), 20);
    }

    @Test public void testTablePrefix() {
        lSql.executeRawSql("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name1 TEXT)");
        lSql.executeRawSql("CREATE TABLE table2 (id SERIAL PRIMARY KEY, name2 TEXT)");
        Optional<Object> id1 = lSql.table("table1").insert(Row.fromKeyVals("name1", "value1"));
        Optional<Object> id2 = lSql.table("table2").insert(Row.fromKeyVals("name2", "value2"));

        Row row = lSql.executeRawQuery("select * from table1").getFirstRow();
        assertEquals(row.keySet().size(), 2);
        assertEquals(row.getInt("id"), id1.get());
        assertEquals(row.getString("name1"), "value1");

        row = lSql.executeRawQuery("select * from table1, table2").getFirstRow();
        assertEquals(row.keySet().size(), 4);
        assertEquals(row.getInt("table1.id"), id1.get());
        assertEquals(row.getString("table1.name1"), "value1");
        assertEquals(row.getInt("table2.id"), id2.get());
        assertEquals(row.getString("table2.name2"), "value2");
    }


    @Test public void groupByTable() {
        lSql.executeRawSql("CREATE TABLE city (id SERIAL PRIMARY KEY, zipcode TEXT, name TEXT)");
        lSql.executeRawSql("CREATE TABLE person (id SERIAL PRIMARY KEY, name TEXT, zipcode INTEGER REFERENCES city (id))");

        Row city1 = Row.fromKeyVals("zipcode", "53721", "name", "Siegburg");
        Optional<Object> city1Id = lSql.table("city").insert(city1);

        Row city2 = Row.fromKeyVals("zipcode", "50935", "name", "Cologne");
        Optional<Object> city2Id = lSql.table("city").insert(city2);

        Row person1 = Row.fromKeyVals("name", "John", "zipcode", city1Id.get());
        lSql.table("person").insert(person1);

        Row person2 = Row.fromKeyVals("name", "Jim", "zipcode", city2Id.get());
        lSql.table("person").insert(person2);

        Query query = lSql.executeRawQuery("select * from person, city");
        Map<String, List<Row>> byTables = query.groupByTables();
        Assert.assertTrue(byTables.get("city").contains(city1));
        Assert.assertTrue(byTables.get("city").contains(city2));
        Assert.assertTrue(byTables.get("person").contains(person1));
        Assert.assertTrue(byTables.get("person").contains(person2));
    }

}
