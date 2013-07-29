package com.w11k.lsql.tests;

import com.google.common.base.Function;
import com.w11k.lsql.Query;
import com.w11k.lsql.Row;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class QueryTest extends AbstractLSqlTest {

    @Test public void query() throws SQLException {
        lSql.execute("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.execute("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeQuery("select * from table1");
        assertNotNull(rows);
    }

    @Test public void queryIterator() throws SQLException {
        lSql.execute("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.execute("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.execute("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        Query rows = lSql.executeQuery("select * from table1");
        int sum = 0;
        for (Row row : rows) {
            sum += row.getInt("age");
        }
        assertEquals(sum, 50);
    }

    @Test public void queryList() throws SQLException {
        lSql.execute("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.execute("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.execute("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        List<Row> rows = lSql.executeQuery("select * from table1").asList();
        assertEquals(rows.size(), 2);
    }

    @Test public void queryMap() throws SQLException {
        lSql.execute("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.execute("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.execute("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        Query rows = lSql.executeQuery("select * from table1");
        List<Integer> ages = rows.map(new Function<Row, Integer>() {
            @Override public Integer apply(Row input) {
                return input.getInt("age");
            }
        });
        assertTrue(ages.contains(20));
        assertTrue(ages.contains(30));
    }

    @Test public void queryGetFirstRow() throws SQLException {
        lSql.execute("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.execute("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeQuery("select * from table1");
        Row row = rows.getFirstRow();
        assertNotNull(row);
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), 20);
    }

}
