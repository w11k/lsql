package com.w11k.lsql.tests;

import com.google.common.base.Optional;
import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TableWithUserPojoTest extends AbstractLSqlTest {

    public static class PersonA extends Row {

        public int getId() {
            return getAs(int.class, "id");
        }

        public void setId(Integer id) {
            put("id", id);
        }

    }

    public static class PersonB extends PersonA {
        public int getAge() {
            return getAs(int.class, "age");
        }

        public void setAge(Integer age) {
            put("age", age);
        }
    }

    public static class Animal extends Row {
    }

    @Test
    public void onlyNeedsToSetTheClassOnce() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, age INT)");
        lSql.table("table1", PersonB.class);
        lSql.table("table1");
    }

    @Test
    public void canRepeatSameClass() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, age INT)");
        lSql.table("table1", PersonB.class);
        lSql.table("table1", PersonB.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void failsOnSubclasses() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, age INT)");
        lSql.table("table1", PersonA.class);
        lSql.table("table1", PersonB.class);
    }

    @Test
    public void canUseOnSuperclasses() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, age INT)");
        lSql.table("table1", PersonB.class);
        lSql.table("table1", PersonA.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void failsOnDifferentClass() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, age INT)");
        lSql.table("table1", PersonA.class);
        lSql.table("table1", Animal.class);
    }

    @Test
    public void convertPojoToRowAndBack() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, age INT)");
        Table<PersonA> table1 = lSql.table("table1", PersonA.class);

        PersonA pojo = new PersonA();
        pojo.setId(999);
        Row row = table1.pojoToRow(pojo);
        assertEquals(row.getInt("id").intValue(), 999);
        pojo = table1.rowToPojo(row);
        assertEquals(pojo.getId(), 999);
    }

    @Test
    public void saveAndLoadPojo() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, age INT)");
        Table<PersonB> table1 = lSql.table("table1", PersonB.class);

        PersonB b1 = new PersonB();
        b1.setId(999);
        b1.setAge(50);
        table1.save(b1);

        PersonB b2 = table1.load(999).get().toPojo();
        assertEquals(b2.getId(), 999);
        assertEquals(b2.getAge(), 50);
    }

    @Test
    public void apiShouldNotBeVerbose() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, age INT)");
        Table<PersonB> table1 = lSql.table("table1", PersonB.class);

        PersonB b1 = new PersonB();
        b1.setId(999);
        b1.setAge(50);
        table1.save(b1);

        Optional<LinkedRow<PersonB>> linkedRowOptional = methodForTestApiShouldNotBeVerbose(table1);
        PersonB personB = linkedRowOptional.get().toPojo();
        assertEquals(personB.getId(), 999);
    }

    public Optional<LinkedRow<PersonB>> methodForTestApiShouldNotBeVerbose(Table<PersonB> person) {
        return person.load(999);
    }


}
