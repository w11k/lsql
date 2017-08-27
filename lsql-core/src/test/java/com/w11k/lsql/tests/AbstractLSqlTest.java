package com.w11k.lsql.tests;

import com.google.common.collect.Maps;
import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
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
import java.util.Map;

public abstract class AbstractLSqlTest {

    private static Map<String, Map<String, Converter>> CONVERTERS = Maps.newHashMap();

    public static class TestConfig extends Config {

        static String driverClassName = null;

        public TestConfig() {
            if (driverClassName.equals("org.h2.Driver")) {
                this.setDialect(new H2Dialect());
            } else if (driverClassName.equals("org.postgresql.Driver")) {
                this.setDialect(new PostgresDialect());
            }

            this.setConverters(CONVERTERS);
        }

    }

    protected LSql lSql;

    public void setConverter(String tableName, String columnName, Class<?> classForConverterLookup) {
        this.setConverter(
                tableName,
                columnName,
                this.lSql.getDialect().getConverterRegistry().getConverterForJavaType(classForConverterLookup));
    }

    public void setConverter(String tableName, String columnName, Converter converter) {
        if (!CONVERTERS.containsKey(tableName)) {
            CONVERTERS.put(tableName, Maps.<String, Converter>newHashMap());
        }

        Map<String, Converter> columnClassMap = CONVERTERS.get(tableName);
        columnClassMap.put(columnName, converter);
    }

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

        CONVERTERS = Maps.newHashMap();

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

        TestConfig.driverClassName = driverClassName;
        this.lSql = new LSql(TestConfig.class, ConnectionProviders.fromInstance(connection));
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
