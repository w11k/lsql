package com.w11k.lsql.tests;

import com.w11k.lsql.LSql;
import com.w11k.lsql.dialects.H2Dialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractLSqlTest {

    protected LSql lSql;

    @BeforeMethod
    public void beforeMethod() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(org.h2.Driver.class.getName());
        ds.setUrl("jdbc:h2:mem:testdb;mode=postgresql");
        ds.setDefaultAutoCommit(false);
        TestUtils.clear(ds);
        Connection connection;
        try {
            connection = ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.lSql = new LSql(new H2Dialect(), ConnectionProviders.fromInstance(connection));
        this.lSql.setFailOnDuplicateTableDefinition(false);
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        if (lSql != null) {
            lSql.getConnectionProvider().call().close();
        }
    }

    protected void createTable(String sql) {
        lSql.executeRawSql(sql);
    }

 }
