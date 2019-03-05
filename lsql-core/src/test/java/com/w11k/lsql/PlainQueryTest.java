package com.w11k.lsql;

import com.w11k.lsql.ResultSetWithColumns;
import com.w11k.lsql.Row;
import com.w11k.lsql.query.PlainQuery;
import io.reactivex.Observable;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;

import static org.testng.Assert.*;

public class PlainQueryTest extends AbstractLSqlTest {

    @Test
    public void query() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        PlainQuery rows = lSql.executeRawQuery("SELECT * FROM table1");
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
        Observable<Row> rx = lSql.executeRawQuery("SELECT * FROM table1").rx();

        List<Integer> ages = rx.map(row -> row.getInt("age")).toList().blockingGet();

        assertTrue(ages.contains(20));
        assertTrue(ages.contains(30));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void failOnDuplicateColumnsInTheResultSet() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawQuery("SELECT name, name, age FROM table1").first().get();
    }

    @Test
    public void ignoreDuplicateColumnsInTheResultSet() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawQuery("SELECT name, name, age FROM table1").ignoreDuplicateColumns().first().get();
    }

    @Test
    public void queryGetFirstRow() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Row row = lSql.executeRawQuery("SELECT * FROM table1").first().get();
        assertNotNull(row);
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), (Integer) 20);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void failsOnDublicateColumnsInResultSet() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        createTable("CREATE TABLE table2 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table2 (name, age) VALUES ('cus2', 30)");
        PlainQuery query = lSql.executeRawQuery("SELECT *, count(*) AS c FROM table1, table2");
        query.toList();
    }


    @Test
    public void flatMap() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', null)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus2', 20)");

        Observable<Row> rx = lSql.executeRawQuery("SELECT * FROM table1").rx();

        List<Integer> ages = rx.flatMap(row -> {
            Integer age = row.getInt("age");
            if (age != null) {
                return Observable.just(age);
            } else {
                return Observable.empty();
            }
        }).toList().blockingGet();

        assertEquals(ages.size(), 1);
        assertEquals(ages.get(0).intValue(), 20);
    }

    @Test
    public void rxResultSet() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 10)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus2', 20)");

        final PlainQuery query = lSql.executeRawQuery("SELECT * FROM table1");
        Observable<ResultSetWithColumns> rx = query.rxResultSet();
        List<Row> result = rx.filter(resultSetWithColumns -> {
            try {
                return resultSetWithColumns.getResultSet().getInt("age") > 15;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).map(resultSetWithColumns -> {
            try {
                return Row.fromKeyVals("age", resultSetWithColumns.getResultSet().getInt("age"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).toList().blockingGet();

        assertEquals(result.size(), 1);
    }

    @Test
    public void rxApiTests() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 10)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus2', 20)");

        PlainQuery query = lSql.executeRawQuery("SELECT * FROM table1");
        query.rx()
                .filter((Row row) -> row.getInt("age") < 100)
                .subscribe();
    }

}
