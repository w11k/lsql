package com.w11k.lsql;

import com.google.common.collect.Sets;
import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class IdGroupedRowCreatorTest extends AbstractLSqlTest {

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
    public void groupByIdsWithPluralName() {
        Set<Integer> collectedAddressIds = Sets.newLinkedHashSet();
        List<QueriedRow> persons = createQuery().groupByIds("id", "address_id");
        assertEquals(persons.size(), 2);
        for (QueriedRow person : persons) {
            List<QueriedRow> addresses = person.getJoined("address_ids");
            assertEquals(addresses.size(), 2);
            for (QueriedRow address : addresses) {
                collectedAddressIds.add(address.getInt("address_id"));
            }
        }
        assertTrue(collectedAddressIds.contains(1));
        assertTrue(collectedAddressIds.contains(2));
        assertTrue(collectedAddressIds.contains(3));
        assertTrue(collectedAddressIds.contains(4));
    }

    @Test
    public void groupByIdsWithUserDefinedName() {
        Set<Integer> collectedAddressIds = Sets.newLinkedHashSet();
        List<QueriedRow> persons = createQuery().groupByIds("id", "address_id as addresses");
        assertEquals(persons.size(), 2);
        for (QueriedRow person : persons) {
            List<QueriedRow> addresses = person.getJoined("addresses");
            assertEquals(addresses.size(), 2);
            for (QueriedRow address : addresses) {
                collectedAddressIds.add(address.getInt("address_id"));
            }
        }
        assertTrue(collectedAddressIds.contains(1));
        assertTrue(collectedAddressIds.contains(2));
        assertTrue(collectedAddressIds.contains(3));
        assertTrue(collectedAddressIds.contains(4));
    }

    private Query createQuery() {
        return lSql.executeRawQuery("SELECT p.*, " +
                "a.id AS address_id, a.person_id, a.city FROM person p LEFT OUTER JOIN address a ON a.person_id = p.id");
    }

}
