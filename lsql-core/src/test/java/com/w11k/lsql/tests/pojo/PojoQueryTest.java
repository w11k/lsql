package com.w11k.lsql.tests.pojo;

import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class PojoQueryTest extends AbstractLSqlTest {

    @SuppressWarnings("WeakerAccess")
    public static class Table1 {

        private String name;

        private int age;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return this.age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @Test
    public void executeRawQuery() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        this.lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('name1', 20)");
        this.lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('name2', 30)");

        List<Table1> rows = this.lSql.executeRawQuery(
                "SELECT * FROM table1 order by age",
                Table1.class).toList();

        assertEquals(rows.size(), 2);
        assertEquals(rows.get(0).getAge(), 20);
        assertEquals(rows.get(0).getName(), "name1");
        assertEquals(rows.get(1).getAge(), 30);
        assertEquals(rows.get(1).getName(), "name2");
    }


}
