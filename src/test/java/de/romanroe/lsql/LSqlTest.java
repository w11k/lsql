package de.romanroe.lsql;

import org.h2.jdbcx.JdbcDataSource;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import static junit.framework.Assert.assertNotNull;

@Test
public class LSqlTest {

    private LSql lSql;

    private JdbcDataSource dataSource = new JdbcDataSource();

    public LSqlTest() {
        dataSource.setURL("jdbc:h2:mem:testdb");
    }

    @BeforeTest
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

        lSql.executeSql("create table table1 (name char(50), age int)");
        lSql.executeSql("insert into table1 (name, age) values ('cus1', 20)");
        lSql.executeSql("insert into table1 (name, age) values ('cus2', 30)");
    }

    @AfterTest
    public void afterTest() throws SQLException {
        System.out.println("clean");
        lSql.executeSql("drop table table1");
    }

    public void getConnectionFromConnectionFactory() throws SQLException {
        assertNotNull(lSql.getConnection());
    }

    public void testSelect() throws SQLException {
        Iterable<Row> users = lSql.table("table1").select().run();
        for (Row user : users) {
            System.out.println(user);
        }
    }

}
