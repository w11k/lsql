package com.w11k.lsql.tests;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.w11k.lsql.relational.QueriedRow;
import com.w11k.lsql.relational.Query;
import com.w11k.lsql.relational.Row;
import com.w11k.lsql.relational.Table;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class QueryTest extends AbstractLSqlTest {

    @Test(dataProvider = "lSqlProvider")
    public void query(LSqlProvider provider) {
        provider.init(this);

        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeRawQuery("SELECT * FROM table1");
        assertNotNull(rows);
    }

    @Test(dataProvider = "lSqlProvider")
    public void queryIterator(LSqlProvider provider) {
        provider.init(this);

        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        Query rows = lSql.executeRawQuery("SELECT * FROM table1");
        int sum = 0;
        for (QueriedRow row : rows) {
            sum += row.getInt("age");
        }
        assertEquals(sum, 50);
    }

    @Test(dataProvider = "lSqlProvider")
    public void queryList(LSqlProvider provider) {
        provider.init(this);

        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        List<QueriedRow> rows = lSql.executeRawQuery("SELECT * FROM table1").asList();
        assertEquals(rows.size(), 2);
    }

    @Test(dataProvider = "lSqlProvider")
    public void queryMap(LSqlProvider provider) {
        provider.init(this);

        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 30)");
        Query rows = lSql.executeRawQuery("SELECT * FROM table1");
        List<Integer> ages = rows.map(new Function<QueriedRow, Integer>() {
            @Override
            public Integer apply(QueriedRow input) {
                return input.getInt("age");
            }
        });
        assertTrue(ages.contains(20));
        assertTrue(ages.contains(30));
    }

    @Test(dataProvider = "lSqlProvider")
    public void queryGetFirstRow(LSqlProvider provider) {
        provider.init(this);

        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
        Query rows = lSql.executeRawQuery("SELECT * FROM table1");
        Row row = rows.getFirstRow().get();
        assertNotNull(row);
        assertEquals(row.getString("name"), "cus1");
        assertEquals(row.getInt("age"), 20);
    }

    @Test(dataProvider = "lSqlProvider")
    public void testTablePrefix(LSqlProvider provider) {
        provider.init(this);

        createTable("CREATE TABLE table1 (id SERIAL PRIMARY KEY, name1 TEXT)");
        createTable("CREATE TABLE table2 (id SERIAL PRIMARY KEY, name2 TEXT)");
        Optional<Object> id1 = lSql.table("table1").insert(Row.fromKeyVals("name1", "value1"));
        Optional<Object> id2 = lSql.table("table2").insert(Row.fromKeyVals("name2", "value2"));

        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow().get();
        assertEquals(row.keySet().size(), 2);
        assertEquals(row.getInt("id"), id1.get());
        assertEquals(row.getString("name1"), "value1");

        row = lSql.executeRawQuery("SELECT * FROM table1, table2").getFirstRow().get();
        assertEquals(row.keySet().size(), 4);
        assertEquals(row.getInt("table1.id"), id1.get());
        assertEquals(row.getString("table1.name1"), "value1");
        assertEquals(row.getInt("table2.id"), id2.get());
        assertEquals(row.getString("table2.name2"), "value2");
    }

    @Test(dataProvider = "lSqlProvider")
    public void groupByTable(LSqlProvider provider) {
        provider.init(this);
        setupCompanyEmployeeContact();

        Query query = lSql.executeRawQuery("SELECT * FROM company, employee, contact");
        Map<String, List<Row>> byTables = query.groupByTables();
        assertEquals(byTables.get("company").size(), 2);
        assertEquals(byTables.get("employee").size(), 4);
        assertEquals(byTables.get("contact").size(), 8);
    }

    @Test(dataProvider = "lSqlProvider")
    public void join(LSqlProvider provider) {
        provider.init(this);
        setupCompanyEmployeeContact();

        Table company = lSql.table("company");

        Query query = lSql.executeRawQuery("SELECT * FROM company, customer, employee, contact " +
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

    private void setupCompanyEmployeeContact() {
        createTable("CREATE TABLE company (id SERIAL PRIMARY KEY, name TEXT);");
        createTable("CREATE TABLE customer (id SERIAL PRIMARY KEY, name TEXT, company_id INT REFERENCES company (id));");
        createTable("CREATE TABLE employee (id SERIAL PRIMARY KEY, name TEXT, company_id INT REFERENCES company (id));");
        createTable("CREATE TABLE contact (id SERIAL PRIMARY KEY, name TEXT, employee_id INT REFERENCES employee (id));");

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
    }


}
