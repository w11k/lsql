package com.weiglewilczek.lsql;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Function;
import org.h2.jdbcx.JdbcDataSource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static junit.framework.Assert.*;

public class LSqlTest {

    private LSql lSql;

    private JdbcDataSource dataSource = new JdbcDataSource();

    public LSqlTest() {
        dataSource.setURL("jdbc:h2:mem:testdb");
    }

    @BeforeMethod
    public void beforeTest() throws SQLException {
        final Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);

        lSql = new LSql();
        lSql.setConnectionFactory(new Callable<Connection>() {
            @Override
            public Connection call() throws Exception {
                return connection;
            }
        });
    }

    @AfterMethod
    public void afterTest() throws SQLException {
        lSql.executeSql("drop table if exists table1");
        lSql.getConnection().commit();
    }

    @Test
    public void getConnectionFromConnectionFactory() throws SQLException {
        assertNotNull(lSql.getConnection());
    }

    @Test
    public void testSelectFieldAccess() throws SQLException {
        lSql.executeSql("create table table1 (name char(50), age int);" +
                "insert into table1 (name, age) values ('cus1', 20);" +
                "insert into table1 (name, age) values ('cus2', 30)");

        List<Integer> ages = lSql.select().from("table1").map(new Function<Row, Integer>() {
            @Override
            public Integer apply(@Nullable Row row) {
                return Integer.parseInt(row.get("AGE").toString());
            }
        });
        int sum = 0;
        for (int age : ages) {
            sum += age;
        }
        assertEquals(sum, 50);
    }

    @Test
    public void testSelectFullAccessToMapEntrySet() throws SQLException {
        lSql.executeSql("create table table1 (name char (50), age int);" +
                "insert into table1 (name, age) values ('cus1', 20);");

        // All key->values
        final List<String> entries = Lists.newArrayList();

        lSql.select().from("table1").map(new Function<Row, Integer>() {
            @Override
            public Integer apply(@Nullable Row input) {
                for (Map.Entry<String, Object> entry : input.entrySet()) {
                    entries.add(entry.getKey() + "->" + entry.getValue());
                }
                return null;
            }
        });
        assertTrue(entries.contains("NAME->cus1"));
        assertTrue(entries.contains("AGE->20"));
    }

}
