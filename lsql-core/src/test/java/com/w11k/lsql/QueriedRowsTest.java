package com.w11k.lsql;

import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;

public class QueriedRowsTest extends AbstractLSqlTest {

    @BeforeMethod
    public void setupTablesAndData() {
        createTable("CREATE TABLE person (id INT PRIMARY KEY, name TEXT)");
        lSql.executeRawSql("INSERT INTO person (id, name) VALUES (1, 'person1')");
        lSql.executeRawSql("INSERT INTO person (id, name) VALUES (2, 'person2')");

        createTable("CREATE TABLE address (id INT PRIMARY KEY, person_id INT, city TEXT)");
        lSql.executeRawSql("INSERT INTO address (id, person_id, city) VALUES (1, 1, 'city1')");
        lSql.executeRawSql("INSERT INTO address (id, person_id, city) VALUES (2, 1, 'city2')");
        lSql.executeRawSql("INSERT INTO address (id, person_id, city) VALUES (3, 2, 'city3')");
        lSql.executeRawSql("INSERT INTO address (id, person_id, city) VALUES (4, 2, 'city4')");
    }

    @Test
    public void groupByColumn() {
        Map<Object, QueriedRows> personGroup = createQuery().groupByColumn("id");
        assertEquals(personGroup.size(), 2);
        for (Map.Entry<Object, QueriedRows> person : personGroup.entrySet()) {
            assertEquals(person.getValue().size(), 2);
            Map<Object, QueriedRows> addressGroup = person.getValue().groupByColumn("address_id");
            for (Map.Entry<Object, QueriedRows> address : addressGroup.entrySet()) {
                assertEquals(address.getValue().size(), 1);
            }
        }
    }

    private Query createQuery() {
        return lSql.executeRawQuery("SELECT p.*, " +
                "a.id AS address_id, a.person_id, a.city FROM person p LEFT OUTER JOIN address a ON a.person_id = p.id");
    }

}
