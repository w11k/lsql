package com.w11k.lsql.tests;

import com.w11k.lsql.Query;
import com.w11k.lsql.ResultSetWithColumns;
import com.w11k.lsql.Row;
import org.testng.annotations.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import java.sql.SQLException;
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

        final Query query = lSql.executeRawQuery("SELECT * FROM table1");
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
                return query.extractRow(resultSetWithColumns);
            }
        }).toList().toBlocking().first();

        assertEquals(result.size(), 1);
    }

    //@Test
    public void groupBy() {
        // TODO

        createTable("CREATE TABLE table1 (id INT, name1 TEXT)");
        lSql.executeRawSql("INSERT INTO table1 VALUES (1, 'a')");
        lSql.executeRawSql("INSERT INTO table1 VALUES (2, 'b')");

        createTable("CREATE TABLE table2 (id INT, table1_id INT, name2 TEXT)");
        lSql.executeRawSql("INSERT INTO table2 VALUES (1, 1, 'a-2-a')");
        lSql.executeRawSql("INSERT INTO table2 VALUES (2, 1, 'a-2-b')");
        lSql.executeRawSql("INSERT INTO table2 VALUES (3, 2, 'b-2-a')");
        lSql.executeRawSql("INSERT INTO table2 VALUES (4, 2, 'b-2-b')");

        createTable("CREATE TABLE table3 (id INT, table1_id INT, name3 TEXT)");
        lSql.executeRawSql("INSERT INTO table3 VALUES (1, 1, 'a-3-a')");
        lSql.executeRawSql("INSERT INTO table3 VALUES (2, 1, 'a-3-b')");
        lSql.executeRawSql("INSERT INTO table3 VALUES (1, 2, 'b-3-a')");
        lSql.executeRawSql("INSERT INTO table3 VALUES (2, 2, 'b-3-b')");

        Query query = lSql.executeRawQuery("SELECT table1.*,  '|', table2.* FROM table1 " +
          "LEFT JOIN table2 on table2.table1_id = table1.id " +
          "LEFT JOIN table3 on table3.table1_id = table1.id");
        List<Row> rows = query.toList();


        for (Row row : rows) {
            System.out.println(row);
        }


        query.rxResultSet().subscribe(new Action1<ResultSetWithColumns>() {
            @Override
            public void call(ResultSetWithColumns resultSetWithColumns) {
                System.out.println("resultSetWithColumns = " + resultSetWithColumns);
            }
        });


    }

    @Test
    public void rxApiTests() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 10)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus2', 20)");

        Query query = lSql.executeRawQuery("SELECT * FROM table1");
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
