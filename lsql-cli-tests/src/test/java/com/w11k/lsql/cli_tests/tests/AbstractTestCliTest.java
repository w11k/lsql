package com.w11k.lsql.cli_tests.tests;

import com.w11k.lsql.LSql;
import com.w11k.lsql.cli.tests.TestCliConfig;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.annotations.BeforeMethod;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static com.w11k.lsql.cli.tests.TestCliConfig.createTables;

public class AbstractTestCliTest {

    protected LSql lSql;

    @BeforeMethod
    public void before() throws SQLException {
        String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";mode=postgresql";
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setDefaultAutoCommit(false);
        Connection connection = ds.getConnection();
        this.lSql = new LSql(TestCliConfig.class, ConnectionProviders.fromInstance(connection));

        createTables(lSql);
    }

}
