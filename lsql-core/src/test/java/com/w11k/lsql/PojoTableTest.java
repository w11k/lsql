package com.w11k.lsql;

import com.google.common.base.Optional;
import com.w11k.lsql.tests.AbstractLSqlTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class PojoTableTest extends AbstractLSqlTest {

    private static class Table1Pojo {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static class Table2Pojo {
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

    @Test
    public void insert() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, name TEXT)");
        PojoTable<Table1Pojo> table1Pojo = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        t1.setName("text1");
        table1Pojo.insert(t1);
        Table table11Raw = lSql.table("table1");
        LinkedRow linkedRow = table11Raw.load(1).get();
        assertEquals(linkedRow.getInt("id"), Integer.valueOf(1));
        assertEquals(linkedRow.getString("name"), "text1");
    }

    @Test
    public void update() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, name TEXT)");
        PojoTable<Table1Pojo> table1Pojo = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        t1.setName("text1");
        table1Pojo.insert(t1);
        t1.setName("text2");
        table1Pojo.update(t1);
        Table table11Raw = lSql.table("table1");
        LinkedRow linkedRow = table11Raw.load(1).get();
        assertEquals(linkedRow.getInt("id"), Integer.valueOf(1));
        assertEquals(linkedRow.getString("name"), "text2");
    }

    @Test
    public void delete() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, name TEXT)");
        PojoTable<Table1Pojo> table1Pojo = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        t1.setName("text1");
        table1Pojo.insert(t1);
        table1Pojo.delete(t1);
        Table table11Raw = lSql.table("table1");
        Optional<LinkedRow> load = table11Raw.load(1);
        assertFalse(load.isPresent());
    }

    @Test
    public void insertReturnsPojoWithMissingValues() {
        createTable("CREATE TABLE table1 (id INT PRIMARY KEY, name TEXT DEFAULT 'n/a')");
        PojoTable<Table1Pojo> table1 = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = new Table1Pojo();
        t1.setId(1);
        t1 = table1.insert(t1);
        assertEquals(t1.getName(), "n/a");
    }

    @Test
    public void loadById() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, name TEXT)");
        lSql.table("table1").insert(Row.fromKeyVals(
            "id", 1,
            "name", "name1"
        )).get();

        PojoTable<Table1Pojo> table1 = lSql.table("table1", Table1Pojo.class);
        Table1Pojo t1 = table1.load(1).get();
        assertEquals(t1.getId(), 1);
        assertEquals(t1.getName(), "name1");

    }

    @Test
    public void insertNameStyleConversion() {
        createTable("CREATE TABLE table2 (id INTEGER PRIMARY KEY, first_name TEXT)");
        PojoTable<Table2Pojo> table2 = lSql.table("table2", Table2Pojo.class);

        Table2Pojo t2 = new Table2Pojo();
        t2.setId(2);
        t2.setFirstName("Max");
        table2.insert(t2);

        t2 = table2.load(2).get();
        assertEquals(t2.getId(), 2);
        assertEquals(t2.getFirstName(), "Max");
    }

}
