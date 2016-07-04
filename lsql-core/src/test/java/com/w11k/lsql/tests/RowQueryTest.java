package com.w11k.lsql.tests;

import com.w11k.lsql.query.RowQuery;
import com.w11k.lsql.ResultSetWithColumns;
import com.w11k.lsql.Row;
import org.testng.annotations.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import java.sql.SQLException;
import java.util.List;

import static org.testng.Assert.*;

public class RowQueryTest extends AbstractLSqlTest {

    @Test
    public void query() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        RowQuery rows = lSql.executeRawQuery("SELECT * FROM table1");
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

        List<Integer> ages = rx.map(new Func1<Row, Integer>() {
            public Integer call(Row row) {
                System.out.println("MAPPPP");
                return row.getInt("age");
            }
        }).toList().toBlocking().first();

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
        RowQuery query = lSql.executeRawQuery("SELECT *, count(*) AS c FROM table1, table2");
        query.toList();
    }


    @Test
    public void flatMap() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', null)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus2', 20)");

        Observable<Row> rx = lSql.executeRawQuery("SELECT * FROM table1").rx();

        List<Integer> ages = rx.flatMap(new Func1<Row, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Row row) {
                Integer age = row.getInt("age");
                if (age != null) {
                    return Observable.just(age);
                } else {
                    return Observable.empty();
                }

            }
        }).toList().toBlocking().first();

        assertEquals(ages.size(), 1);
        assertEquals(ages.get(0).intValue(), 20);
    }

    @Test
    public void rxResultSet() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 10)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus2', 20)");

        final RowQuery query = lSql.executeRawQuery("SELECT * FROM table1");
        Observable<ResultSetWithColumns> rx = query.rxResultSet();
        List<Row> result = rx.filter(new Func1<ResultSetWithColumns, Boolean>() {
            @Override
            public Boolean call(ResultSetWithColumns resultSetWithColumns) {
                try {
                    return resultSetWithColumns.getResultSet().getInt("age") > 15;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }).map(new Func1<ResultSetWithColumns, Row>() {
            @Override
            public Row call(ResultSetWithColumns resultSetWithColumns) {
                try {
                    return Row.fromKeyVals("age", resultSetWithColumns.getResultSet().getInt("age"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }).toList().toBlocking().first();

        assertEquals(result.size(), 1);
    }

    @Test
    public void rxApiTests() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 10)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus2', 20)");

        RowQuery query = lSql.executeRawQuery("SELECT * FROM table1");
        Observable<Row> rx = query.rx();

        rx.filter(new Func1<Row, Boolean>() {
            @Override
            public Boolean call(Row row) {
                return row.getInt("age") < 100;
            }
        }).subscribe(new Action1<Row>() {
            @Override
            public void call(Row row) {
                System.out.println("row = " + row);
            }
        });
    }

}
