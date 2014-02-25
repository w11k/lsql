package com.w11k.lsql;

import com.google.common.collect.Sets;
import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

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
    public void groupByOnlyKeepsColumnOfTheIdColumnTable() {
        List<QueriedRow> persons = createQuery().groupByIds("id", "address_id");
        assertEquals(persons.size(), 2);
        for (QueriedRow person : persons) {
            assertTrue(person.containsKey("id"));
            assertTrue(person.containsKey("name"));
            assertFalse(person.containsKey("address_id"));
            assertFalse(person.containsKey("person_id"));
            assertFalse(person.containsKey("city"));
            List<QueriedRow> addresses = person.getJoined("address_ids");
            assertEquals(addresses.size(), 2);
            for (QueriedRow address : addresses) {
                assertTrue(address.containsKey("address_id"));
                assertTrue(address.containsKey("person_id"));
                assertTrue(address.containsKey("city"));
                assertFalse(address.containsKey("id"));
                assertFalse(address.containsKey("name"));
            }
        }
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

    /**
     * https://github.com/w11k/lsql/issues/2
     */
    @Test
    public void soeOnToString() {
        List<QueriedRow> persons = createQuery().groupByIds("id", "address_id as addresses");
        for (QueriedRow person : persons) {
            String s = person.toString();
            assertNotNull(s);
        }
    }

    /**
     * https://github.com/w11k/lsql/issues/3
     */
    @Test
    public void npeWhenJoinedRowIsEmpty() {
        lSql.executeRawSql("INSERT INTO person (id, name) VALUES (3, 'person3')");
        List<QueriedRow> persons = createQuery().groupByIds("id", "address_id as addresses");
        assertEquals(persons.size(), 3);

        QueriedRow person3 = null;
        for (QueriedRow person : persons) {
            if (person.getInt("id") == 3) {
                person3 = person;
            }
        }

        assertNotNull(person3);
        assertTrue(person3.getJoined("addresses").isEmpty());
    }


    private Query createQuery() {
        return lSql.executeRawQuery("SELECT p.*, " +
                "a.id AS address_id, a.person_id, a.city FROM person p LEFT OUTER JOIN address a ON a.person_id = p.id");
    }

}
