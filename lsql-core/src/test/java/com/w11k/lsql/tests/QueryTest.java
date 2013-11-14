package com.w11k.lsql.tests;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.w11k.lsql.QueriedRow;
import com.w11k.lsql.Query;
import com.w11k.lsql.Row;
import org.testng.annotations.Test;

import java.util.List;

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
        Row row = rows.getFirstRow().get();
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
    public void canUseCalculatedColumns() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeRawQuery("SELECT count(*) as c FROM table1");
        Row row = rows.getFirstRow().get();
        assertEquals(row.getInt("c"), 2);
    }


}
