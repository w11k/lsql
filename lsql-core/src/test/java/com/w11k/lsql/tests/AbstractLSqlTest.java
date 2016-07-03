package com.w11k.lsql.tests;

import com.w11k.lsql.LSql;
import com.w11k.lsql.dialects.GenericDialect;
import com.w11k.lsql.dialects.H2Dialect;
import com.w11k.lsql.dialects.PostgresDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractLSqlTest {

    protected LSql lSql;

    @Parameters({
            TestParameter.jdbcDriverClassName,
            TestParameter.jdbcUrl,
            TestParameter.jdbcUsername,
            TestParameter.jdbcPassword
    })
    @BeforeMethod()
    public final void beforeMethod(@Optional String driverClassName,
                                   @Optional String url,
                                   @Optional String username,
                                   @Optional String password) {

        driverClassName = driverClassName != null ? driverClassName : "org.h2.Driver";
        url = url != null ? url : "jdbc:h2:mem:testdb;mode=postgresql";
        username = username != null ? username : "";
        password = password != null ? password : "";

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);

        ds.setDefaultAutoCommit(false);
        TestUtils.clear(ds);
        Connection connection;
        try {
            connection = ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        GenericDialect dialect = null;
        if (driverClassName.equals("org.h2.Driver")) {
            dialect = new H2Dialect();
        } else if (driverClassName.equals("org.postgresql.Driver")) {
            dialect = new PostgresDialect();
        }
        this.lSql = new LSql(dialect, ConnectionProviders.fromInstance(connection));
        this.beforeMethodHook();
    }

    @AfterMethod
    public final void afterMethod() throws Exception {
        lSql.getConnectionProvider().call().close();
    }

    protected void beforeMethodHook() {

    }

    protected void createTable(String sql) {
        lSql.executeRawSql(sql);
    }

}
