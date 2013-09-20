package com.w11k.lsql.tests.dialects;

import com.w11k.lsql.*;
import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql.tests.TestUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public abstract class AbstractDialectTests {

    protected LSql lSql;

    @BeforeMethod
    public void beforeMethod() throws SQLException {
        DataSource dataSource = createDataSource();
        TestUtils.clear(dataSource);
        Connection con = dataSource.getConnection();
        this.lSql = new LSql(createDialect(), ConnectionProviders.fromInstance(con));
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        if (lSql != null) {
            lSql.getConnectionProvider().call().close();
        }
    }

    @Test
    public void run() throws SQLException {
        insertGetDelete();
        join();
        blob();
    }

    public void insertGetDelete() throws SQLException {
        setupTestTable();

        Table table1 = lSql.table("table1");

        // Insert
        Row row1 = new Row().addKeyVals("age", 10);
        Object id1 = table1.insert(row1).get();
        Row row2 = new Row().addKeyVals("age", 20);
        Object id2 = table1.insert(row2).get();

        // Verify insert
        int tableSize = lSql.executeRawQuery("SELECT * FROM table1;").asList().size();
        assertEquals(tableSize, 2);

        QueriedRow queried1 = table1.get(id1).get();
        assertEquals(queried1.getInt("id"), id1);
        assertEquals(queried1.getInt("age"), 10);
        assertEquals(row1.getInt("id"), id1);

        QueriedRow queried2 = table1.get(id2).get();
        assertEquals(queried2.getInt("id"), id2);
        assertEquals(queried2.getInt("age"), 20);
        assertEquals(row2.getInt("id"), id2);

        // Delete
        table1.delete(id2);

        // Verify delete
        tableSize = lSql.executeRawQuery("SELECT * FROM table1;").asList().size();
        assertEquals(tableSize, 1);
    }

    public void join() {
        setupCompanyEmployeeContactTables();

        lSql.executeRawSql("INSERT INTO company (name) VALUES ('Company1');\n" +
                "INSERT INTO company (name) VALUES ('Company2');\n" +

                "INSERT INTO customer (name, company_id) VALUES ('Company1-Customer1', 1);\n" +
                "INSERT INTO customer (name, company_id) VALUES ('Company1-Customer2', 1);\n" +

                "INSERT INTO employee (name, company_id) VALUES ('Company1-Employee1', 1);\n" +
                "INSERT INTO employee (name, company_id) VALUES ('Company1-Employee2', 1);\n" +
                "INSERT INTO employee (name, company_id) VALUES ('Company2-Employee1', 2);\n" +
                "INSERT INTO employee (name, company_id) VALUES ('Company2-Employee2', 2);\n" +

                "INSERT INTO contact (name, employee_id) VALUES ('Company1-Employee1-Contact1', 1);\n" +
                "INSERT INTO contact (name, employee_id) VALUES ('Company1-Employee1-Contact2', 1);\n" +
                "INSERT INTO contact (name, employee_id) VALUES ('Company1-Employee2-Contact1', 2);\n" +
                "INSERT INTO contact (name, employee_id) VALUES ('Company1-Employee2-Contact2', 2);\n" +

                "INSERT INTO contact (name, employee_id) VALUES ('Company2-Employee1-Contact1', 3);\n" +
                "INSERT INTO contact (name, employee_id) VALUES ('Company2-Employee1-Contact2', 3);\n" +
                "INSERT INTO contact (name, employee_id) VALUES ('Company2-Employee2-Contact1', 4);\n" +
                "INSERT INTO contact (name, employee_id) VALUES ('Company2-Employee2-Contact2', 4);\n");

        // Test groupByTable
        Query query = lSql.executeRawQuery("SELECT * FROM company, employee, contact");
        Map<String, List<Row>> byTables = query.groupByTables();
        assertEquals(byTables.get("company").size(), 2);
        assertEquals(byTables.get("employee").size(), 4);
        assertEquals(byTables.get("contact").size(), 8);

        // Test join
        Table company = lSql.table("company");
        query = lSql.executeRawQuery("SELECT * FROM company, customer, employee, contact " +
                "WHERE " +
                "employee.company_id = company.id " +
                "AND customer.company_id = company.id " +
                "AND contact.employee_id = employee.id " +
                "AND company.id = 1;");

        List<Row> rows = query.joinOn(company);
        assertEquals(rows.size(), 1);
        Row company1 = rows.get(0);

        List<Row> customers = company1.getJoinedRows("customer");
        assertEquals(customers.size(), 2);

        List<Row> employees = company1.getJoinedRows("employee");
        assertEquals(employees.size(), 2);
        for (Row employee : employees) {
            assertEquals(employee.getJoinedRows("contact").size(), 2);
        }
    }

    public void blob() {
        byte[] data = "123456789".getBytes();
        Blob blob = new Blob(data);
        TestUtils.testType(lSql, getBlobColumnType(), blob, blob);
    }


    public abstract DataSource createDataSource() throws SQLException;

    public abstract BaseDialect createDialect();

    /**
     * Create a table like
     * CREATE TABLE table1 (id SERIAL PRIMARY KEY, age INTEGER)
     */
    protected abstract void setupTestTable();

    /**
     * CREATE TABLE company (id SERIAL PRIMARY KEY, name TEXT);
     * CREATE TABLE customer (id SERIAL PRIMARY KEY, name TEXT, company_id INT REFERENCES company (id));
     * CREATE TABLE employee (id SERIAL PRIMARY KEY, name TEXT, company_id INT REFERENCES company (id));
     * CREATE TABLE contact (id SERIAL PRIMARY KEY, name TEXT, employee_id INT REFERENCES employee (id));
     */
    protected abstract void setupCompanyEmployeeContactTables();

    /**
     * BLOB, bytea, ...
     */
    protected abstract String getBlobColumnType();

}
