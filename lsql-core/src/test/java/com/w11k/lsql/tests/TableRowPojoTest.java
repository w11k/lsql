package com.w11k.lsql.tests;

import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TableRowPojoTest extends AbstractLSqlTest {

    public static class Table1 extends Row {

        public int getId() {
            return getAs(Integer.class, "id");
        }

        public void setId(Integer id) {
            put("id", id);
        }

        public int getAge() {
            return getAs(Integer.class, "age");
        }

        public void setAge(Integer age) {
            put("age", age);
        }

    }

    @Test
    public void rowToPojo() {
        createTable("CREATE TABLE table1 (id INTEGER PRIMARY KEY, age INT, rest INT)");
        Table<Table1> table1 = lSql.table("table1", Table1.class);

        Table1 pojo1 = new Table1();
        pojo1.setId(1);
        pojo1.setAge(10);
        pojo1.put("rest", 20);
        table1.insert(pojo1);

        pojo1 = table1.get(1).get().toPojo();
        assertEquals(pojo1.getId(), 1);
        assertEquals(pojo1.getAge(), 10);
        assertEquals(pojo1.getInt("rest"), 20);
    }

}
