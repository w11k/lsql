package com.w11k.lsql.tests.pojo;

import com.google.common.base.Optional;
import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.PojoTable;
import com.w11k.lsql.Table;
import com.w11k.lsql.tests.AbstractLSqlTest;
import com.w11k.lsql.tests.testdata.Person;
import com.w11k.lsql.tests.testdata.PersonSubclass;
import com.w11k.lsql.tests.testdata.PersonTestData;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PojoTableTest extends AbstractLSqlTest {

    @Test
    public void insert() {
        PersonTestData.init(this.lSql, false);
        PojoTable<Person> personTable = this.lSql.table("person", Person.class);
        Person p1 = new Person(1, "Max", 30);
        personTable.insert(p1);
        Table personRowTable = this.lSql.table("person");
        LinkedRow linkedRow = personRowTable.load(1).get();
        assertEquals(linkedRow.getInt("id"), Integer.valueOf(1));
        assertEquals(linkedRow.getString("firstName"), "Max");
    }

    @Test
    public void update() {
        PersonTestData.init(this.lSql, false);
        PojoTable<Person> personTable = this.lSql.table("person", Person.class);
        Person p1 = new Person(1, "Max", 30);
        personTable.insert(p1);

        p1.setFirstName("Walter");
        personTable.update(p1);

        Table personRowTable = this.lSql.table("person");
        LinkedRow linkedRow = personRowTable.load(1).get();
        assertEquals(linkedRow.getInt("id"), Integer.valueOf(1));
        assertEquals(linkedRow.getString("firstName"), "Walter");
    }

    @Test
    public void delete() {
        PersonTestData.init(this.lSql, false);
        PojoTable<Person> personTable = this.lSql.table("person", Person.class);
        Person p1 = new Person(1, "Max", 30);
        personTable.insert(p1);

        personTable.delete(p1);

        Table personRowTable = this.lSql.table("person");
        Optional<LinkedRow> load = personRowTable.load(1);
        assertFalse(load.isPresent());
    }

    @Test
    public void insertAssignsDefaultValue() {
        PersonTestData.init(this.lSql, false);
        PojoTable<Person> personTable = this.lSql.table("person", Person.class);
        Person p1 = new Person();
        p1.setId(1);
        personTable.insert(p1);
        assertEquals(p1.getTitle(), "n/a");
    }

    @Test
    public void insertIgnoresDefaultValueOnPureInsert() {
        PersonTestData.init(this.lSql, false);
        PojoTable<Person> personTable = this.lSql.table("person", Person.class);
        Person p1 = new Person();
        p1.setId(1);
        personTable.insert(p1, true);
        assertNull(p1.getTitle());
    }

    @Test
    public void loadById() {
        PersonTestData.init(this.lSql, true);
        PojoTable<Person> personTable = this.lSql.table("person", Person.class);

        Person person = personTable.load(1).get();
        assertEquals(person.getId(), 1);
        assertEquals(person.getFirstName(), "Adam");
    }

    @Test
    public void insertIgnoresFieldsFromSubclass() {
        PersonTestData.init(this.lSql, false);
        PojoTable<Person> personTable = this.lSql.table("person", Person.class);
        PersonSubclass p = new PersonSubclass(1, "Adam", 30);
        personTable.insert(p);
    }

//    @Test
//    public void fieldsUseConverterRegistry() {
//        PersonTestData.init(this.lSql, false);
//        this.lSql.getDialect().getConverterRegistry().addConverter(new AtomicIntegerConverter());
//        PojoTable<Table1WithAtomicInteger> table1 = this.lSql.table("table1", Table1WithAtomicInteger.class);
//        Table1WithAtomicInteger t1 = new Table1WithAtomicInteger();
//        t1.setId(1);
//        t1.setAi(new AtomicInteger(2));
//        t1.setFirstName("Max");
//        table1.insert(t1);
//    }
//
//    @Test(
//            expectedExceptions = IllegalArgumentException.class,
//            expectedExceptionsMessageRegExp = ".*No converter.*"
//    )
//    public void failOnFieldsWithMissingConverters() {
//        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT, ai INTEGER)");
//        this.lSql.table("table1", Table1WithAtomicInteger.class);
//    }
//
//    @Test(
//            expectedExceptions = IllegalArgumentException.class,
//            expectedExceptionsMessageRegExp = ".*converter.*not support.*"
//    )
//    public void failWhenConverterCanNotConvertBetweenJavaAndSqlType() {
//        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT, ai VARCHAR(10))");
//        this.lSql.getDialect().getConverterRegistry()
//                .addConverter(new AtomicIntegerConverter());
//        this.lSql.table("table1", Table1WithAtomicInteger.class);
//    }

}
