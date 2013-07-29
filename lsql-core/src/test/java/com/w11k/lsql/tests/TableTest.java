package com.w11k.lsql.tests;

import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class TableTest extends AbstractLSqlTest {

    @Test public void insertRow() throws SQLException {
        lSql.execute("CREATE TABLE table1 (name TEXT, age INT)");
        Table table1 = lSql.table("table1");

        Row row = new Row().addKeyVals("name", "cus1", "age", 20);
        table1.insert(row);

        Row insertedRow = lSql.executeQuery("select * from table1").getFirstRow();
        assertEquals(insertedRow.getString("name"), "cus1");
        assertEquals(insertedRow.getInt("age"), 20);
    }

    @Test public void insertShouldReturnGeneratedKey() {
        lSql.execute("CREATE TABLE table1 (id SERIAL, age INT)");
        Table table1 = lSql.table("table1");
        Object newId = table1.insert(new Row().addKeyVals("age", 1)).get();

        Row query = lSql.executeQuery("select * from table1 where id = " + newId).getFirstRow();
        assertEquals(query.getInt("age"), 1);
    }

}
