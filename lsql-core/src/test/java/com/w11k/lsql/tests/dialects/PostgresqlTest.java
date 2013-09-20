package com.w11k.lsql.tests.dialects;

import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.dialects.PostgresDialect;

public class PostgresqlTest extends AbstractDialectTests {

    @Override
    public BaseDialect createDialect() {
        return new PostgresDialect();
    }

    @Override
    protected void setupTestTable() {
        lSql.executeRawSql("CREATE TABLE table1 (id SERIAL PRIMARY KEY, age INTEGER)");
    }

    @Override
    protected void setupCompanyEmployeeContactTables() {
        lSql.executeRawSql("CREATE TABLE company (company_pk SERIAL PRIMARY KEY, name TEXT);");

        lSql.executeRawSql("CREATE TABLE customer (customer_pk SERIAL PRIMARY KEY, name TEXT, " +
                "customer_company_fk INT REFERENCES company (company_pk));");

        lSql.executeRawSql("CREATE TABLE employee (employee_pk SERIAL PRIMARY KEY, name TEXT, " +
                "employee_company_fk INT REFERENCES company (company_pk));");

        lSql.executeRawSql("CREATE TABLE contact (contact_pk SERIAL PRIMARY KEY, name TEXT, " +
                "contact_employee_fk INT REFERENCES employee (employee_pk));");
    }

    @Override
    protected String getBlobColumnType() {
        return "bytea";
    }
}
