package com.w11k.lsql.tests.dialects;

import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.dialects.H2Dialect;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class H2Test extends AbstractDialectTests {

    @Override
    public DataSource createDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(org.h2.Driver.class.getName());
        ds.setUrl("jdbc:h2:mem:testdb;mode=postgresql");
        ds.setDefaultAutoCommit(false);
        return ds;
    }

    @Override
    public BaseDialect createDialect() {
        return new H2Dialect();
    }

    @Override
    protected void setupTestTable() {
        lSql.executeRawSql("CREATE TABLE table1 (id SERIAL PRIMARY KEY, age INTEGER)");
    }

    @Override
    protected void setupCompanyEmployeeContactTables() {
        lSql.executeRawSql("CREATE TABLE company (id SERIAL PRIMARY KEY, name TEXT);");
        lSql.executeRawSql("CREATE TABLE customer (id SERIAL PRIMARY KEY, name TEXT, company_id INT REFERENCES company (id));");
        lSql.executeRawSql("CREATE TABLE employee (id SERIAL PRIMARY KEY, name TEXT, company_id INT REFERENCES company (id));");
        lSql.executeRawSql("CREATE TABLE contact (id SERIAL PRIMARY KEY, name TEXT, employee_id INT REFERENCES employee (id));");
    }

    @Override
    protected String getBlobColumnType() {
        return "BLOB";
    }
}
