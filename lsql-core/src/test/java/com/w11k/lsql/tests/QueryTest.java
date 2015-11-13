package com.w11k.lsql.tests;

import com.google.common.base.Function;
import com.w11k.lsql.Query;
import com.w11k.lsql.Row;
import com.w11k.lsql.RowConsumer;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void queryList() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        List<Row> rows = lSql.executeRawQuery("SELECT * FROM table1").toList();
        assertEquals(rows.size(), 2);
    }

    @Test
    public void queryMap() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        Query q = lSql.executeRawQuery("SELECT * FROM table1");
        List<Integer> ages = q.map(new Function<Row, Integer>() {
            @Override
            public Integer apply(Row input) {
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
        Row row = lSql.executeRawQuery("SELECT * FROM table1").firstRow().get();
        assertNotNull(row);
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), (Integer) 20);
    }

    @Test
    public void canUseCalculatedColumns() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Row row = lSql.executeRawQuery("SELECT count(*) AS c FROM table1").firstRow().get();
        assertEquals(row.getInt("c"), (Integer) 2);
    }

    @Test
    public void canUseCalculatedColumnsTogetherWithNormalColumnsOneTable() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Row row = lSql.executeRawQuery("SELECT name, age, count(*) AS c FROM table1").firstRow().get();
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), (Integer) 20);
        assertEquals(row.getInt("c"), (Integer) 1);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void failsOnDublicateColumnsInResultSet() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        createTable("CREATE TABLE table2 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table2 (name, age) VALUES ('cus2', 30)");
        Query query = lSql.executeRawQuery("SELECT *, count(*) AS c FROM table1, table2");
        query.toList();
    }

    @Test
    public void map() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 10)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus2', 20)");

        Query query = lSql.executeRawQuery("SELECT * FROM table1");
        final AtomicInteger ai = new AtomicInteger();
        List<Object> results = query.map(new Function<Row, Object>() {
            public Object apply(Row input) {
                ai.incrementAndGet();
                return input;
            }
        });
        assertEquals(ai.get(), 2);
        assertEquals(results.size(), 2);
    }

    @Test
    public void flatMap() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', null)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus2', 20)");

        Query query = lSql.executeRawQuery("SELECT * FROM table1");
        List<Integer> ages = query.flatMap(new Function<Row, Integer>() {
            public Integer apply(Row input) {
                return input.getInt("age");
            }
        });
        assertEquals(ages.size(), 1);
        assertEquals(ages.get(0).intValue(), 20);
    }

    @Test
    public void forEach() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 10)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus2', 20)");

        Query query = lSql.executeRawQuery("SELECT * FROM table1");
        final AtomicInteger ai = new AtomicInteger();
        query.forEach(new RowConsumer() {
            public void each(Row row) {
                ai.incrementAndGet();
            }
        });
        assertEquals(ai.get(), 2);
    }

}
