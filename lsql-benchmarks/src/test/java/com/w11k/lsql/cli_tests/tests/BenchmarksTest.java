package com.w11k.lsql.cli_tests.tests;

import com.w11k.lsql.LSql;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql_benchmark.LSqlConfig;
import com.w11k.lsql_benchmark.db.schema_public.Table1_Row;
import com.w11k.lsql_benchmark.db.schema_public.Table1_Table;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;

public class BenchmarksTest {

    protected LSql lSql;

    @BeforeMethod
    public void before()  {
        try {
            BasicDataSource ds = new BasicDataSource();
            ds.setUrl(System.getProperty("db.url", "jdbc:postgresql://localhost:33333/lsql_benchmark"));
            ds.setUsername(System.getProperty("db.user", "lsql_benchmark"));
            ds.setPassword(System.getProperty("db.pass", "lsql_benchmark"));
            ds.setDefaultAutoCommit(false);
            Connection connection = ds.getConnection();
            this.lSql = new LSql(LSqlConfig.class, ConnectionProviders.fromInstance(connection));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test() {
        Table1_Table table1Table = new Table1_Table(this.lSql);
        long start = System.currentTimeMillis();
        for (int i = 0; i <= 10 * 10; i++) {
            Table1_Row row1 = new Table1_Row().withField1(1);
            table1Table.save(row1);
        }
        long dur = System.currentTimeMillis() - start;
        System.out.println("dur = " + dur);
    }


}
