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

    //private static final Pattern CREATE_TABLE_STATEMENT = Pattern.compile(
    //        "create table (\\w+).*",
    //        Pattern.CASE_INSENSITIVE);

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
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        if (lSql != null) {
            lSql.getConnectionProvider().call().close();
        }
    }

    /*
    @DataProvider(name = "lSqlProvider_h2")
    public Iterator<Object[]> createLSqlProviderForH2() throws SQLException {
        List<Object[]> providers = Lists.newLinkedList();

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(org.h2.Driver.class.getName());
        ds.setUrl("jdbc:h2:mem:testdb;mode=postgresql");
        ds.setDefaultAutoCommit(false);

        clear(ds);

        Connection connection = ds.getConnection();

        providers.add(new Object[]{
                new LSqlProvider(new LSql(new H2Dialect(), ConnectionProviders.fromInstance(connection)))
        });

        return providers.iterator();
    }

    @DataProvider(name = "lSqlProvider_postgresql")
    public Iterator<Object[]> createLSqlProviderForPostgres() throws SQLException {
        List<Object[]> providers = Lists.newLinkedList();
        try {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(org.postgresql.Driver.class.getName());
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

    @DataProvider(name = "lSqlProvider_sqlserver")
    public Iterator<Object[]> createLSqlProviderForSqlServer() throws SQLException {
        List<Object[]> providers = Lists.newLinkedList();
        try {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(net.sourceforge.jtds.jdbc.Driver.class.getName());
            ds.setUrl("jdbc:jtds:sqlserver://augsburg:1433/lsqltests");
            ds.setUsername("sa");
            ds.setPassword("l4g4vul1n!");
            ds.setDefaultAutoCommit(false);

            clear(ds);

            Connection connection = ds.getConnection();

            providers.add(new Object[]{
                    new LSqlProvider(new LSql(new BaseDialect(), ConnectionProviders.fromInstance(connection)))
            });
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return providers.iterator();
    }
    */

    /*
    @DataProvider(name = "lSqlProvider")
    public Iterator<Object[]> createLSqlProviders() throws SQLException {
        List<Object[]> providers = Lists.newLinkedList();
        providers.addAll(Lists.newArrayList(createLSqlProviderForH2()));
        providers.addAll(Lists.newArrayList(createLSqlProviderForPostgres()));
        //providers.addAll(Lists.newArrayList(createLSqlProviderForSqlServer()));
        return providers.iterator();
    }
    */

    /*
    public void setupLSqlInstanceForTest(LSql upLSqlInstanceForTest) {
        lSql = upLSqlInstanceForTest;
    }
    */

    protected void createTable(String sql) {
        //Matcher startMatcher = CREATE_TABLE_STATEMENT.matcher(sql);
        //startMatcher.find();
        //String tableName = startMatcher.group(1);
        lSql.executeRawSql(sql);
    }

 }
