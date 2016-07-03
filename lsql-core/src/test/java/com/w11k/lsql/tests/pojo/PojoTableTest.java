package com.w11k.lsql.tests.pojo;

import com.google.common.base.Optional;
import com.w11k.lsql.LinkedRow;
import com.w11k.lsql.PojoTable;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class PojoTableTest extends AbstractLSqlTest {

    private static class Table1Pojo {
        private int id;
        private String firstName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
    }

    private static class Table1PojoSubclass extends Table1Pojo {
        private int ignore = 1;

        public int getIgnore() {
            return ignore;
        }

        public void setIgnore(int ignore) {
            this.ignore = ignore;
        }
    }

    @Test
    public void insert() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT)");
        PojoTable<Table1Pojo> table1Pojo = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        t1.setFirstName("text1");
        table1Pojo.insert(t1);
        Table table11Raw = lSql.table("table1");
        LinkedRow linkedRow = table11Raw.load(1).get();
        assertEquals(linkedRow.getInt("id"), Integer.valueOf(1));
        assertEquals(linkedRow.getString("firstName"), "text1");
    }

    @Test
    public void update() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT)");
        PojoTable<Table1Pojo> table1Pojo = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        t1.setFirstName("text1");
        table1Pojo.insert(t1);
        t1.setFirstName("text2");
        table1Pojo.update(t1);
        Table table11Raw = lSql.table("table1");
        LinkedRow linkedRow = table11Raw.load(1).get();
        assertEquals(linkedRow.getInt("id"), Integer.valueOf(1));
        assertEquals(linkedRow.getString("firstName"), "text2");
    }

    @Test
    public void delete() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT)");
        PojoTable<Table1Pojo> table1Pojo = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        t1.setFirstName("text1");
        table1Pojo.insert(t1);
        table1Pojo.delete(t1);
        Table table11Raw = lSql.table("table1");
        Optional<LinkedRow> load = table11Raw.load(1);
        assertFalse(load.isPresent());
    }

    @Test
    public void insertReturnsPojoWithMissingValues() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, first_name TEXT DEFAULT 'n/a')");
        PojoTable<Table1Pojo> table1 = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        t1 = table1.insert(t1);
        assertEquals(t1.getFirstName(), "n/a");
    }

    @Test
    public void loadById() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT)");
        lSql.table("table1").insert(Row.fromKeyVals(
            "id", 1,
            "firstName", "name1"
        )).get();

        PojoTable<Table1Pojo> table1 = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = table1.load(1).get();
        assertEquals(t1.getId(), 1);
        assertEquals(t1.getFirstName(), "name1");
    }

    @Test
    public void insertIgnoresFieldsFromSubclass() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, first_name TEXT)");
        PojoTable<Table1Pojo> table1Pojo = lSql.table("table1", Table1Pojo.class);
        Table1PojoSubclass t1 = new Table1PojoSubclass();
        t1.setId(1);
        t1.setFirstName("text1");
        t1.setIgnore(123);
        table1Pojo.insert(t1);
    }

}
