package com.w11k.lsql.tests;

import com.google.common.base.Function;
import com.w11k.lsql.Query;
import com.w11k.lsql.Row;
import com.w11k.lsql.Rows;
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
        for (Row row : rows) {
            sum += row.getInt("age");
        }
        assertEquals(sum, 50);
    }

    @Test
    public void queryList() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        Rows rows = lSql.executeRawQuery("SELECT * FROM table1").rows();
        assertEquals(rows.size(), 2);
    }

    @Test
    public void queryMap() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        Rows rows = lSql.executeRawQuery("SELECT * FROM table1").rows();
        List<Integer> ages = rows.map(new Function<Row, Integer>() {
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
        Rows rows = lSql.executeRawQuery("SELECT * FROM table1").rows();
        Row row = rows.first().get();
        assertNotNull(row);
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), (Integer) 20);
    }

    @Test
    public void canUseCalculatedColumns() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Row row = lSql.executeRawQuery("SELECT count(*) AS c FROM table1").rows().first().get();
        assertEquals(row.getInt("c"), (Integer) 2);
    }

    @Test
    public void canUseCalculatedColumnsTogetherWithNormalColumnsOneTable() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Row row = lSql.executeRawQuery("SELECT name, age, count(*) AS c FROM table1").rows().first().get();
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
        query.rows();
    }

}
