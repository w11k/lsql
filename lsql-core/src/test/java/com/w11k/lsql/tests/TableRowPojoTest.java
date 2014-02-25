package com.w11k.lsql.tests;

import com.w11k.lsql.RowPojo;
import com.w11k.lsql.Table;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TableRowPojoTest extends AbstractLSqlTest {

    public static class Table1 extends RowPojo {
        private int id;

        private int age;

        public Table1(int id, int age) {
            this.id = id;
            this.age = age;
        }

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
    public void rowToPojo() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT)");
        Table<Table1> table1 = lSql.table("table1", Table1.class);

        Table1 pojo1 = new Table1(1, 10);
        pojo1.setId(1);
        pojo1.setAge(10);
        table1.insert(pojo1);

        pojo1 = table1.get(1).get().toPojo();
        assertEquals(pojo1.getId(), 1);
        assertEquals(pojo1.getAge(), 10);
    }

}
