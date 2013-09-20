package com.w11k.lsql.tests.dialects;

import com.w11k.lsql.*;
import com.w11k.lsql.dialects.BaseDialect;
import com.w11k.lsql.jdbc.ConnectionProviders;
import com.w11k.lsql.tests.TestUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

public abstract class AbstractDialectTests {

    protected LSql lSql;

    protected Properties properties;

    protected DataSource dataSource;

    private boolean initOk = false;

    @BeforeMethod
    public void beforeMethod() throws SQLException {
        try {
            properties = readProperties();
            dataSource = createDataSource();
            TestUtils.clear(dataSource);
            Connection con = dataSource.getConnection();
            this.lSql = new LSql(createDialect(), ConnectionProviders.fromInstance(con));
            this.initOk = true;
        } catch (Exception e) {
            Reporter.getCurrentTestResult().setAttribute("warn", "Can not test dialect.");
            Reporter.getCurrentTestResult().setStatus(ITestResult.SKIP);
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        if (!initOk) {
            return;
        }
        if (lSql != null) {
            lSql.getConnectionProvider().call().close();
        }
    }

    @Test
    public void run() throws SQLException {
        if (!initOk) {
            return;
        }
        insertGetDelete();
        blob();
        join();
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
        assertEquals(queried1.get("id"), id1);
        assertEquals(queried1.get("age"), 10);
        assertEquals(row1.get("id"), id1);

        QueriedRow queried2 = table1.get(id2).get();
        assertEquals(queried2.get("id"), id2);
        assertEquals(queried2.getInt("age"), 20);
        assertEquals(row2.get("id"), id2);

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

                "INSERT INTO customer (name, customer_company_fk) VALUES ('Company1-Customer1', 1);\n" +
                "INSERT INTO customer (name, customer_company_fk) VALUES ('Company1-Customer2', 1);\n" +

                "INSERT INTO employee (name, employee_company_fk) VALUES ('Company1-Employee1', 1);\n" +
                "INSERT INTO employee (name, employee_company_fk) VALUES ('Company1-Employee2', 1);\n" +
                "INSERT INTO employee (name, employee_company_fk) VALUES ('Company2-Employee1', 2);\n" +
                "INSERT INTO employee (name, employee_company_fk) VALUES ('Company2-Employee2', 2);\n" +

                "INSERT INTO contact (name, contact_employee_fk) VALUES ('Company1-Employee1-Contact1', 1);\n" +
                "INSERT INTO contact (name, contact_employee_fk) VALUES ('Company1-Employee1-Contact2', 1);\n" +
                "INSERT INTO contact (name, contact_employee_fk) VALUES ('Company1-Employee2-Contact1', 2);\n" +
                "INSERT INTO contact (name, contact_employee_fk) VALUES ('Company1-Employee2-Contact2', 2);\n" +

                "INSERT INTO contact (name, contact_employee_fk) VALUES ('Company2-Employee1-Contact1', 3);\n" +
                "INSERT INTO contact (name, contact_employee_fk) VALUES ('Company2-Employee1-Contact2', 3);\n" +
                "INSERT INTO contact (name, contact_employee_fk) VALUES ('Company2-Employee2-Contact1', 4);\n" +
                "INSERT INTO contact (name, contact_employee_fk) VALUES ('Company2-Employee2-Contact2', 4);\n");

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
                "employee.employee_company_fk = company.company_pk " +
                "AND customer.customer_company_fk = company.company_pk " +
                "AND contact.contact_employee_fk = employee.employee_pk " +
                "AND company.company_pk = 1;");

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

    protected String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    protected Properties readProperties() {
        Properties p = new Properties();
        String className = getClass().getSimpleName();
        String fileName = className + "_" + getHostname() + ".dblocal";
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new RuntimeException(
                    "File '" + getClass().getPackage().getName().replace('.', '/') + "/" +
                            fileName + "' not found");
        }
        try {
            p.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }

    protected String getDatabaseUrl() {
        return properties.getProperty("url");
    }

    protected String getDatabaseDriver() {
        return properties.getProperty("driver");
    }

    protected String getDatabaseUser() {
        return properties.getProperty("user");
    }

    protected String getDatabasePassword() {
        return properties.getProperty("password");
    }

    protected DataSource createDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(getDatabaseDriver());
        ds.setUrl(getDatabaseUrl());
        ds.setUsername(getDatabaseUser());
        ds.setPassword(getDatabasePassword());
        ds.setDefaultAutoCommit(false);
        return ds;
    }

    protected abstract BaseDialect createDialect();

    protected abstract void setupTestTable();

    protected abstract void setupCompanyEmployeeContactTables();

    protected abstract String getBlobColumnType();

}
