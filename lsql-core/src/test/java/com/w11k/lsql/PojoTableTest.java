package com.w11k.lsql;

import com.google.common.base.Optional;
import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PojoTableTest extends AbstractLSqlTest {

    public static class Table1 {
        private int id;
        private int age;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @Test
    public void insert() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        PojoTable<Table1> table1 = lSql.table("table1", Table1.class);
        Table1 t1 = new Table1();
        t1.setAge(20);
        table1.insert(t1);
    }

    @Test
    public void insertReturnsPk() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        PojoTable<Table1> table1 = lSql.table("table1", Table1.class);
        Table1 t1 = new Table1();
        t1.setAge(20);
        Optional<Object> pk = table1.insert(t1);
        assertTrue(pk.isPresent());
    }

    @Test
    public void insertEnrichesPojoWithMissingValues() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT DEFAULT 25)");
        PojoTable<Table1> table1 = lSql.table("table1", Table1.class);
        Table1 t1 = new Table1();
        table1.insert(t1);
        assertEquals(t1.getAge(), 25);
    }

    @Test
    public void loadById() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        PojoTable<Table1> table1 = lSql.table("table1", Table1.class);
        Table1 t1 = new Table1();
        t1.setAge(20);
        Object pk = table1.insert(t1).get();
        Table1 t1Loaded = table1.load(pk);
        System.out.println("t1Loaded = " + t1Loaded);

    }

}
