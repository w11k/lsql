package com.w11k.lsql.tests;

import com.google.common.collect.Maps;
import com.w11k.lsql.Config;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.dialects.H2Dialect;
import com.w11k.lsql.dialects.IdentifierConverter;
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

    private Connection connection;

    public static class TestConfig extends Config {

        public static Map<String, Map<String, Converter>> CONVERTERS = Maps.newHashMap();

        public static boolean USE_COLUMN_TYPE_FOR_CONVERTER_LOOKUP = false;

        public static IdentifierConverter IDENTIFIER_CONVERTER = null;

        public static void reset() {
            CONVERTERS = Maps.newHashMap();
            USE_COLUMN_TYPE_FOR_CONVERTER_LOOKUP = false;
            IDENTIFIER_CONVERTER = null;
        }

        static String driverClassName = null;

        public TestConfig() {
            if (driverClassName.equals("org.h2.Driver")) {
                this.setDialect(new H2Dialect());
            } else if (driverClassName.equals("org.postgresql.Driver")) {
                this.setDialect(new PostgresDialect());
            }

            this.setConverters(CONVERTERS);
            if (IDENTIFIER_CONVERTER != null) {
                this.getDialect().setIdentifierConverter(IDENTIFIER_CONVERTER);
            }
            this.setUseColumnTypeForConverterLookupInQueries(USE_COLUMN_TYPE_FOR_CONVERTER_LOOKUP);
        }

    }

    protected LSql lSql;

    public void setConverter(String tableName, String columnName, Class<?> classForConverterLookup) {
        this.setConverter(
                tableName,
                columnName,
                this.lSql.getConverterForJavaType(classForConverterLookup));
    }

    public void setConverter(String tableName, String columnName, Converter converter) {
        if (!TestConfig.CONVERTERS.containsKey(tableName)) {
            TestConfig.CONVERTERS.put(tableName, Maps.<String, Converter>newHashMap());
        }

        Map<String, Converter> columnClassMap = TestConfig.CONVERTERS.get(tableName);
        columnClassMap.put(columnName, converter);

        this.createLSqlInstance();
    }

    public void setIdentifierConverter(IdentifierConverter identifierConverter) {
        TestConfig.IDENTIFIER_CONVERTER = identifierConverter;
        this.createLSqlInstance();
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

        TestConfig.reset();

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
        try {
            this.connection = ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        TestConfig.driverClassName = driverClassName;
        this.createLSqlInstance();
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

    protected void createLSqlInstance() {
        this.lSql = new LSql(TestConfig.class, ConnectionProviders.fromInstance(this.connection));
    }

}
