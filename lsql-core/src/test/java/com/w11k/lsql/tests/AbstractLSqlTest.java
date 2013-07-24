package com.w11k.lsql.tests;

import com.w11k.lsql.ConnectionFactories;
import com.w11k.lsql.LSql;
import org.h2.jdbcx.JdbcDataSource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractLSqlTest {

    private static AtomicInteger dbNameIdCounter = new AtomicInteger();

    protected LSql lSql;

    @BeforeMethod public void beforeTest() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb_" + dbNameIdCounter.getAndIncrement() + ";MODE=PostgreSQL");
        final Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        lSql = new LSql(ConnectionFactories.fromInstance(connection));
    }

    @AfterMethod public void afterTest() throws SQLException {
        //lSql.getConnection().commit();
    }

}
