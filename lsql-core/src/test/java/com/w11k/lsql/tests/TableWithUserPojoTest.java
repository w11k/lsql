package com.w11k.lsql.tests;

import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.Table;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TableWithUserPojoTest extends AbstractLSqlTest {

    public static class PersonA extends LinkedRow {
//        protected PersonA(Table<?> table, Map<String, Object> row) {
//            super(table, row);
//        }
    }

    public static class PersonB extends PersonA {
//        protected PersonB(Table<?> table, Map<String, Object> row) {
//            super(table, row);
//        }

        public int getAge() {
            return getAs(int.class, "age");
        }

        public void setAge(Integer age) {
            put("age", age);
        }
    }

    public static class Animal extends LinkedRow {
//        protected Animal(Table<?> table, Map<String, Object> row) {
//            super(table, row);
//        }
    }

    public static class Table1 extends LinkedRow {
//        protected Table1(Table<?> table, Map<String, Object> row) {
//            super(table, row);
//        }

        public int getAge() {
            return getAs(Integer.class, "age");
        }

        public void setAge(Integer age) {
            put("age", age);
        }
    }

    @Test
    public void onlyNeedsToSetTheClassOnce() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, age INT)");
        lSql.table("table1", PersonB.class);
        Table<?> table1 = lSql.table("table1");
        LinkedRow linkedRow = table1.newLinkedRow();
        Assert.assertEquals(linkedRow.getClass(), PersonB.class);
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
    public void rowToPojo() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT, rest INT)");
        Table<Table1> table1 = lSql.table("table1", Table1.class);

        Table1 pojo1 = table1.newLinkedRow();
        pojo1.setId(1);
        pojo1.setAge(10);
        pojo1.put("rest", 20);
        table1.insert(pojo1);

        pojo1 = table1.load(1).get();
        assertEquals(pojo1.getId(), 1);
        assertEquals(pojo1.getAge(), 10);
        assertEquals(pojo1.getInt("rest"), (Integer) 20);
    }

}
