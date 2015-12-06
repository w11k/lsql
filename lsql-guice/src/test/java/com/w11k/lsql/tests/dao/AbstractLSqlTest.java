package com.w11k.lsql.tests.dao;

import com.googlecode.flyway.core.Flyway;
import com.w11k.lsql.LSql;
import com.w11k.lsql.dialects.H2Dialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.h2.jdbcx.JdbcDataSource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractLSqlTest {

    protected LSql lSql;

    @BeforeMethod
    public void beforeMethod() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;mode=postgresql");
        Connection connection = ds.getConnection();

        Flyway flyway = new Flyway();
        flyway.setDataSource(ds);
        flyway.clean();

        this.lSql = new LSql(new H2Dialect(), ConnectionProviders.fromInstance(connection));
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        if (lSql != null) {
            lSql.getConnectionProvider().call().close();
        }
    }

 }
