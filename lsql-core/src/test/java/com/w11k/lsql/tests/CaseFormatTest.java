package com.w11k.lsql.tests;

import com.google.common.base.CaseFormat;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class CaseFormatTest extends AbstractLSqlTest {

    private BaseDialect underscoreDialect = new BaseDialect() {
        @Override
        public String identifierSqlToJava(String sqlName) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, sqlName.toLowerCase());
        }

        @Override
        public String identifierJavaToSql(String javaName) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, javaName.toLowerCase()).toUpperCase();
        }
    };

    private void initLSql(BaseDialect dialect) {
        try {
            super.afterMethod();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
        this.lSql = new LSql(dialect, ConnectionProviders.fromInstance(connection));
    }


    @Test
    public void testTableNameCamelCase() {
        createTable("CREATE TABLE aaa_bbb (ccc_ddd INT NULL)");
        lSql.table("aaaBbb");
    }

    @Test
    public void testTableNameUnderscore() {
        initLSql(underscoreDialect);
        createTable("CREATE TABLE aaa_bbb (ccc_ddd INT NULL)");
        lSql.table("aaa_bbb");
    }

    @Test
    public void testColumnNameCamelCase() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, ccc_ddd INT NULL)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("id", 1, "cccDdd", 2));
        assertEquals(table1.load(1).get().getInt("cccDdd"), new Integer(2));
    }

    @Test
    public void testColumnNameUnderscore() {
        initLSql(underscoreDialect);
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, ccc_ddd INT NULL)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("id", 1, "ccc_ddd", 2));
        assertEquals(table1.load(1).get().getInt("ccc_ddd"), new Integer(2));
    }
}
