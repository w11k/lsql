package com.w11k.lsql.tests;

import com.w11k.lsql.Row;
import org.testng.annotations.Test;

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

}
