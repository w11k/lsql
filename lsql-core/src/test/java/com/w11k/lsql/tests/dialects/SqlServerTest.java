package com.w11k.lsql.tests.dialects;

import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.dialects.SqlServerDialect;

public class SqlServerTest extends AbstractDialectTests {

    @Override
    public BaseDialect createDialect() {
        return new SqlServerDialect();
    }

    @Override
    protected void setupTestTable() {
        lSql.executeRawSql("CREATE TABLE table1 (id INT IDENTITY PRIMARY KEY, age INTEGER)");
    }

    @Override
    protected void setupCompanyEmployeeContactTables() {
        lSql.executeRawSql("CREATE TABLE company (company_pk NUMERIC IDENTITY PRIMARY KEY, name TEXT);");

        lSql.executeRawSql("CREATE TABLE customer (customer_pk NUMERIC IDENTITY PRIMARY KEY, name TEXT, " +
                "customer_company_fk NUMERIC REFERENCES company (company_pk));");

        lSql.executeRawSql("CREATE TABLE employee (employee_pk NUMERIC IDENTITY PRIMARY KEY, name TEXT, " +
                "employee_company_fk NUMERIC REFERENCES company (company_pk));");

        lSql.executeRawSql("CREATE TABLE contact (contact_pk NUMERIC IDENTITY PRIMARY KEY, name TEXT, " +
                "contact_employee_fk NUMERIC REFERENCES employee (employee_pk));");
    }

    @Override
    protected String getBlobColumnType() {
        return "VARBINARY(10)";
    }
}
