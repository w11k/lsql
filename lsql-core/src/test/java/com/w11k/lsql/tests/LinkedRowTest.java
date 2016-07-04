package com.w11k.lsql.tests;

import com.google.common.base.Optional;
import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class LinkedRowTest extends AbstractLSqlTest {

    @SuppressWarnings("WeakerAccess")
    static public class Table1Pojo {
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
    public void save() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");

        LinkedRow row1 = table1.newLinkedRow();
        row1.addKeyVals("id", 1, "age", 1);
        Optional<?> row1Id = row1.save();
        assertTrue(row1Id.isPresent());
        assertEquals(row1Id.get(), 1);

        LinkedRow queriedRow1 = table1.load(1).get();
        assertEquals(queriedRow1.getInt("age"), (Integer) 1);

        queriedRow1.put("age", 99);
        queriedRow1.save();
        LinkedRow queriedRow1b = table1.load(1).get();
        assertEquals(queriedRow1b.getInt("age"), (Integer) 99);
    }

    @Test
    public void toObject() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");

        LinkedRow row1 = table1.newLinkedRow();
        row1.addKeyVals("id", 1, "age", 50);
        table1.insert(row1);
        LinkedRow queriedRow1 = table1.load(1).get();
        Table1Pojo table1Pojo = queriedRow1.convertTo(Table1Pojo.class);
        assertEquals(table1Pojo.id, 1);
        assertEquals(table1Pojo.age, 50);
    }

    @Test
    public void delete() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        Table table1 = lSql.table("table1");
        LinkedRow row1 = table1.newLinkedRow();
        row1.addKeyVals("id", 1, "age", 1);
        row1.save();
        assertEquals(table1.load(1).get().getInt("age"), (Integer) 1);
        row1.delete();
        assertFalse(table1.load(1).isPresent());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void linkedRowPutThrowsExceptionOnTooLongString() {
        createTable("CREATE TABLE table1 (name VARCHAR(5))");
        LinkedRow row = lSql.table("table1").newLinkedRow();
        row.put("name", "123456");
    }

    @Test
    public void newLinkedRowCopiesDataWithIdAndRevisionColumn() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT, revision INT DEFAULT 0)");
        Table table1 = lSql.table("table1");
        table1.enableRevisionSupport();
        table1.insert(Row.fromKeyVals("id", 1, "age", 1));
        LinkedRow row = table1.load(1).get();
        assertTrue(row.containsKey("id"));
        assertTrue(row.containsKey("revision"));

        LinkedRow copy = table1.newLinkedRow(row);
        assertTrue(copy.containsKey("id"));
        assertTrue(copy.containsKey("revision"));
    }

    @Test
    public void removeIdAndRevision() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT, revision INT DEFAULT 0)");
        Table table1 = lSql.table("table1");
        table1.enableRevisionSupport();

        LinkedRow row = table1.newLinkedRow(
                "id", 1,
                "age", 1,
                "revision", 1
        );

        row.removeIdAndRevision();
        assertFalse(row.containsKey("id"));
        assertTrue(row.containsKey("age"));
        assertFalse(row.containsKey("revision"));
    }

}
