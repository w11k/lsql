package com.w11k.lsql.tests;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.flyway.core.Flyway;
import com.w11k.lsql.LSql;
import com.w11k.lsql.dialects.H2Dialect;
import com.w11k.lsql.dialects.PostgresDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;
import org.postgresql.Driver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractLSqlTest {

    private static final Pattern CREATE_TABLE_STATEMENT = Pattern.compile(
            "create table (\\w+).*",
            Pattern.CASE_INSENSITIVE);

    protected LSql lSql;

    @DataProvider(name = "lSqlProvider_postgresql")
    public Iterator<Object[]> createLSqlProviderForPostgres() throws SQLException {
        List<Object[]> providers = Lists.newLinkedList();
        try {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(Driver.class.getName());
            ds.setUrl("jdbc:postgresql://localhost/lsqltests?user=lsqltestsuser&password=lsqltestspass");
            ds.setDefaultAutoCommit(false);

            clear(ds);

            Connection connection = ds.getConnection();

            providers.add(new Object[]{
                    new LSqlProvider(new LSql(new PostgresDialect(), ConnectionProviders.fromInstance(connection)))
            });
        } catch (Exception ignored) {
        }
        return providers.iterator();
    }

    @DataProvider(name = "lSqlProvider_h2")
    public Iterator<Object[]> createLSqlProviderForH2() throws SQLException {
        List<Object[]> providers = Lists.newLinkedList();

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(Driver.class.getName());
        ds.setUrl("jdbc:h2:mem:testdb;mode=postgresql");
        ds.setDefaultAutoCommit(false);

        clear(ds);

        Connection connection = ds.getConnection();

        providers.add(new Object[]{
                new LSqlProvider(new LSql(new H2Dialect(), ConnectionProviders.fromInstance(connection)))
        });

        return providers.iterator();
    }

    @DataProvider(name = "lSqlProvider")
    public Iterator<Object[]> createLSqlProviders() throws SQLException {
        List<Object[]> providers = Lists.newLinkedList();
        providers.addAll(Lists.newArrayList(createLSqlProviderForPostgres()));
        providers.addAll(Lists.newArrayList(createLSqlProviderForH2()));
        return providers.iterator();
    }

    public void setupLSqlInstanceForTest(LSql upLSqlInstanceForTest) {
        lSql = upLSqlInstanceForTest;
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        if (lSql != null) {
            lSql.getConnectionProvider().call().close();
        }
    }

    protected void createTable(String sql) {
        Matcher startMatcher = CREATE_TABLE_STATEMENT.matcher(sql);
        startMatcher.find();
        String tableName = startMatcher.group(1);
        lSql.executeRawSql(sql);
    }

    protected void prettyPrintJson(Object result) {
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.setPrettyPrinting().create();
        String s = g.toJson(result);
        System.out.println(s);
    }

    private void clear(DataSource ds) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(ds);
        flyway.clean();
    }

}
