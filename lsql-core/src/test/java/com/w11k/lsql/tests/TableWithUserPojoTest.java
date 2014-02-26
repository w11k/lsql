package com.w11k.lsql.tests;

import com.w11k.lsql.RowPojo;
import org.testng.annotations.Test;

public class TableWithUserPojoTest extends AbstractLSqlTest {

    public static class PersonA extends RowPojo {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

    }

    public static class PersonB extends PersonA {
        private int age;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
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
